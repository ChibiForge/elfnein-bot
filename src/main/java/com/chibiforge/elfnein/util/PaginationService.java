package com.chibiforge.elfnein.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.Embed;
import discord4j.core.object.Embed.Footer;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.emoji.Emoji;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.discordjson.json.MessageReferenceData;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

@Component
public class PaginationService {

    private static final int ITEMS_PER_PAGE = 10;
    
    public Mono<Void> paginate(String title, String desc, List<String> items, Message message) {
        return paginate(title,desc,items,message,Color.SEA_GREEN);
    }
    
    public Mono<Void> paginate(String title, String desc, List<String> items, Message message, Color color) {
        List<EmbedCreateSpec> pages = createPages(color,title,desc,items);
        
        boolean isFirstPage = true;
	    boolean isLastPage = 0 == pages.size()-1;
	  
	    List<Button> buttons = new ArrayList<Button>();
		buttons.add(Button.secondary("ini", Emoji.unicode("⏪")).disabled(isFirstPage));
		buttons.add(Button.secondary("left", Emoji.unicode("◀️")).disabled(isFirstPage));
		buttons.add(Button.secondary("right", Emoji.unicode("▶")).disabled(isLastPage));
		buttons.add(Button.secondary("end", Emoji.unicode("⏩")).disabled(isLastPage));
	    
		MessageReferenceData ref = MessageReferenceData.builder().messageId(message.getId().asLong()).build();
        return message.getChannel().block().createMessage(pages.get(0)).withMessageReference(ref).withComponents(ActionRow.of(buttons)) // TODO reply with components
        	.flatMap(messageRes -> refreshListener(pages,messageRes.getId().asString())).then();
    }

    private List<EmbedCreateSpec> createPages(Color color, String title, String desc, List<String> items) {
        List<EmbedCreateSpec> pages = new ArrayList<>();
        int totalPages = (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE);

        for (int i = 0; i < totalPages; i++) {
            EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
            embedBuilder.color(color);
            embedBuilder.title(title);
            
            embedBuilder.footer("Showing page " + (i + 1) + " of " + totalPages,null);

            int start = i * ITEMS_PER_PAGE;
            int end = Math.min(start + ITEMS_PER_PAGE, items.size());
            
            String descAux = desc;
            for(int j = start; j<end; j++) {
            	descAux = descAux + "\n" + items.get(j);
            }
            embedBuilder.description(descAux);
            
            pages.add(embedBuilder.build());
        }

        return pages;
    }
    
	private Mono<Void> handlePagination(ButtonInteractionEvent event, List<EmbedCreateSpec> pages) {
    	try {
    	// Check message
    	Message eventMessage = event.getMessage().orElse(null);
    	if(eventMessage == null) return Mono.empty();
    	/*
    	// Check its same user
    	User user = event.getInteraction().getUser();
    	User author = message.getAuthor().orElse(null);
    	if(author==null || user.getId().asString().equals(author.getId().asString()))
    		return Mono.empty();
    	*/

    	Integer targetPage = getCurrentPage(eventMessage);
		
		if (event.getCustomId().equals("right")) targetPage++;
		else if(event.getCustomId().equals("left")) targetPage--;
		else if(event.getCustomId().equals("ini")) targetPage = 0;
		else if(event.getCustomId().equals("end")) targetPage = pages.size()-1;
		else return Mono.empty();
		
		boolean isFirstPage = targetPage == 0;
	    boolean isLastPage = targetPage == pages.size()-1;
	    
	    List<Button> buttons = new ArrayList<Button>();
		
		buttons.add(Button.secondary("ini", Emoji.unicode("⏪")).disabled(isFirstPage));
		buttons.add(Button.secondary("left", Emoji.unicode("◀️")).disabled(isFirstPage));
		buttons.add(Button.secondary("right", Emoji.unicode("▶")).disabled(isLastPage));
		buttons.add(Button.secondary("end", Emoji.unicode("⏩")).disabled(isLastPage));
		
		return event.deferEdit().then(eventMessage.edit(MessageEditSpec.builder()
			.addEmbed(pages.get(targetPage))
			.addComponent(ActionRow.of(buttons))
			.build()).then()).then();
		
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
		return Mono.empty();
	}
    
	private int getCurrentPage(Message message) {
	    List<Embed> embeds = message.getEmbeds();
	    if (embeds.isEmpty()) {
	        return 0; // Default to page 0 if no embeds are found
	    }

	    Embed embed = embeds.get(0); // Assuming there's only one embed in the message
	    Optional<Footer> footer = embed.getFooter();

	    if (footer.isPresent()) {
	        String footerText = footer.get().getText();
	        Pattern pattern = Pattern.compile("Showing page (\\d+) of (\\d+)");
	        Matcher matcher = pattern.matcher(footerText);

	        if (matcher.find()) {
	            return Integer.parseInt(matcher.group(1)) - 1; // Convert to zero-based page index
	        }
	    }

	    return 0; // Default to page 0 if footer text is not found or doesn't match
	}
	
	private Mono<Void> refreshListener(List<EmbedCreateSpec> pages, String messageId) {		
		AtomicBoolean activated = new AtomicBoolean(false);
		return Mono.defer(() -> {
			return Service.client.on(ButtonInteractionEvent.class)
				.filter(event -> event.getMessageId().asString().equals(messageId))
				.flatMap(event -> {
					activated.set(true);
					return handlePagination(event,pages);
				})
				.timeout(Duration.ofSeconds(10))
				.onErrorResume(TimeoutException.class, e -> {
					if(activated.get()) return refreshListener(pages,messageId);
			        return Mono.empty();
				}).next();
		}).then();
	}
    
}