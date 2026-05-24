package com.chibiforge.elfnein.command.message;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.Container;
import discord4j.core.object.component.Separator;
import discord4j.core.object.component.Separator.SpacingSize;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class EventCommand extends MessageCommand {
	
	public EventCommand(){
		commandNames.add("event");
		commandId = Global.cmdIdEvent;
		commandId = 0;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		// Check time
		LocalDate now = LocalDate.now();
		if(now.getMonthValue() == 12 || (now.getMonthValue() == 1 && now.getDayOfMonth() < 6))
			return XmasEvent(message);
		else if(now.getMonthValue() == 2 && now.getDayOfMonth() >= 0 && now.getDayOfMonth() < 5)
			return bowEvent(message);
		else if(now.getMonthValue() == 4 && now.getDayOfMonth() >= 10 && now.getDayOfMonth() <= 30)
			return rosesAndDragonsEvent(message);
		else if(now.getMonthValue() == 5 && now.getDayOfMonth() >= 25 && now.getDayOfMonth() <= 31)
			return crayonsEvent(message);
		return Util.replyToMessage(message, "There is currently no active event.").then();
	}
	
	private Mono<Void> XmasEvent(Message message) {
		return Mono.empty(); // TODO
	}
	
	///////////////////////
	// Roses and Dragons //
	///////////////////////

	private Mono<Void> rosesAndDragonsEvent(Message message) {
		List<String> par = getParameters(message);
		if(par.size()>0 && Set.of("lb","leaderboard","ranking","rank").contains(par.get(0).toLowerCase())){
			
		}
		return Mono.empty();
	}
	
	/////////////
	// Crayons //
	/////////////
	
	private Mono<Void> crayonsEvent(Message message) {
		String userId = message.getAuthor().get().getId().asString();
		boolean canCollect = Service.userInventoryService.hasAllCrayons(userId);
		if (!canCollect) return Util.replyToMessage(message, containerCrayonEvent(userId, false, null)).then();
	    
	    String customId = "crayon:send:" + userId + ":" + java.util.UUID.randomUUID();
	    return Util.replyToMessage(message, containerCrayonEvent(userId, true, customId))
	            .flatMap(msg -> claimCrayonListener(msg, customId, userId));
	}
	
	private Container containerCrayonEvent(String userId, boolean canCollect, String customId) {
		Container crayonContainer = Container.of(
			Color.PINK,
			TextDisplay.of("# <:crayon_box:1508214832867053729> Crayon Finding <:crayon_box:1508214832867053729>"),
			Separator.of(true, SpacingSize.of(1)),
			TextDisplay.of(
				"🖍️ **Disaster!**\n" +
				"It's Ris' birthday and Arracso wanted to try drawing her something nice…\n" +
				"**But he somehow lost every single crayon. Every. Single. One.**"
		    ),
			TextDisplay.of(
				"🗺️ **Your Mission**\n" +
				"The crayons are scattered around <#1149239452074512404>.\n" +
				"Spend time chatting there and you might find one!"
	        ),
			TextDisplay.of(
				"🏹 **Event Goal**\n" +
				"• Collect **8 different crayons**\n" +
				"• Bring them back here so Arracso can finish the drawing."
			),
			TextDisplay.of(
				"🎁 **Reward**\n" +
				"**1 Crayon Box**"
		    )
		);
		
		if (canCollect) {
			crayonContainer = crayonContainer.withAddedComponents(
		        Separator.of(true, SpacingSize.of(2)),
		        TextDisplay.of(
		            "✨ **You have all 8 crayons!**\n" +
		            "Send them over and Elfnein will pack them into a ***Crayon Box*** for you after the drawing is done."
		        ),
		        ActionRow.of(Button.secondary(customId, "🖍️ Craft Crayon Box"))
		    );
		}
		
		return crayonContainer;		
	}
	
	private Mono<Void> claimCrayonListener(Message msg, String customId, String ownerUserId) {
		return Service.client.on(ButtonInteractionEvent.class)
			.filter(e-> e.getMessageId().equals(msg.getId()))
			.filter(e -> customId.equals(e.getCustomId()))
			.next() // one-shot stop after first matching click
			.timeout(Duration.ofMinutes(1))
			.flatMap(e -> {
				String clickerId = e.getUser().getId().asString();
	            if (!ownerUserId.equals(clickerId)) {
	                return e.reply().withContent("You cannot do that!").withEphemeral(true);
	            }
	            boolean ok = Service.userInventoryService.exchangeAllCrayonsForACrayonBox(clickerId);
	            if (ok) {
	                return e.reply().withContent("Elfnein received the crayons, helped finish the birthday drawing, and packed them together into a ***Crayon Box*** for you.");
	            }
				return e.reply().withContent("You cannot do that!").withEphemeral(true);
			})
			.onErrorResume(TimeoutException.class, e -> Mono.empty())
			.then();
	}
	
	//////////
	// Bows //
	//////////
	
	private Mono<Void> bowEvent(Message message) {
		String userId = message.getAuthor().get().getId().asString();
		boolean canCollect = Service.userInventoryService.hasAllBows(userId);
		if(userId.equals("696347459751903293")) canCollect = false; // Missy cannot do this
		if (!canCollect) return Util.replyToMessage(message, containerBowEvent(userId, false, null)).then();
	    
	    String customId = "bow:send:" + userId + ":" + java.util.UUID.randomUUID();
	    return Util.replyToMessage(message, containerBowEvent(userId, true, customId))
	            .flatMap(msg -> claimBowListener(msg, customId, userId));
	}
	
	private Container containerBowEvent(String userId, boolean canCollect, String customId) {
		Container bowContainer = Container.of(
			Color.PINK,
			TextDisplay.of("# <a:bow_white_animated:1467929685236252703> Missy's Birthday Bow Hunt <a:bow_white_animated:1467929685236252703>"),
			Separator.of(true, SpacingSize.of(1)),
			TextDisplay.of(
				"🎀 **Disaster!**\n" +
				"Arracso wanted to surprise Missy with *thousands of bows* for her birthday…\n" +
				"**But he lost every single one of them!**"
		    ),
			TextDisplay.of(
				"🗺️ **Your Mission**\n" +
				"The bows are scattered around <#1326619189468729466>.\n" +
				"Spend time chatting there and you might find one!"
	        ),
			TextDisplay.of(
				"🏹 **Event Goal**\n" +
				"• Collect **6 different bows**\n" +
				"• Bring them all back here"
			),
			TextDisplay.of(
				"🎁 **Reward**\n" +
				"**500 Event Coins**"
		    )
		);
		
		if (canCollect) {
		    bowContainer = bowContainer.withAddedComponents(
		        Separator.of(true, SpacingSize.of(2)),
		        TextDisplay.of(
		            "✨ **You have all 6 bows!**\n" +
		            "Send them to Missy in exchange for your reward."
		        ),
		        ActionRow.of(Button.secondary(customId, "🎀 Send bows to Missy"))
		    );
		}
		
		return bowContainer;		
	}
	
	private Mono<Void> claimBowListener(Message msg, String customId, String ownerUserId) {
		return Service.client.on(ButtonInteractionEvent.class)
			.filter(e-> e.getMessageId().equals(msg.getId()))
			.filter(e -> customId.equals(e.getCustomId()))
			.next() // one-shot stop after first matching click
			.timeout(Duration.ofMinutes(1))
			.flatMap(e -> {
				String clickerId = e.getUser().getId().asString();
	            if (!ownerUserId.equals(clickerId)) {
	                return e.reply().withContent("You cannot do that!").withEphemeral(true);
	            }
	            boolean ok = Service.userInventoryService.exchangeAllBowsForCoins(clickerId, 500);
	            if (ok) {
	                return e.reply().withContent("You sent the bows to Missy and received **500** coins in exchange.");
	            }
				return e.reply().withContent("You cannot do that!").withEphemeral(true);
			})
			.onErrorResume(TimeoutException.class, e -> Mono.empty())
			.then();
	}
	
}
