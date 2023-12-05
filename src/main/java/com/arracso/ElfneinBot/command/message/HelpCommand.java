package com.arracso.ElfneinBot.command.message;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class HelpCommand extends MessageCommand {

	public HelpCommand(){
		commandName = "help";
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		//String target = message.getContent().replaceFirst(".*?help", "").trim();
		return message.getChannel().block()
			.createMessage("<@278957461120090113> was too lazy to do the help page so, if you have any doubs, ask him.")
			.withMessageReference(message.getId())
			.then();
	}
	
	
/*
EmbedCreateSpec embed = EmbedCreateSpec.builder()
.color(Color.RED)
.title("Which way is your car facing?")
.build();

Button buttonRight = Button.primary("right", "Right");
Button buttonLeft = Button.primary("left", "Left");

List<Button> buttons = new ArrayList<Button>();
buttons.add(buttonLeft);
buttons.add(buttonRight);

Mono<Void> tempListener = message.getClient().on(ButtonInteractionEvent.class, event -> extracted(event,message)).timeout(Duration.ofSeconds(30))
.onErrorResume(TimeoutException.class, ignore -> Mono.empty()).then();

return message.getChannel().block().createMessage(embed).withMessageReference(message.getId()).withComponents(ActionRow.of(buttons)).then(tempListener);
*/

/*
private Publisher<Void> extracted(ButtonInteractionEvent event, Message message) {
	//return event.reply("You clicked me!").withEphemeral(true);
	if (event.getCustomId().equals("right")) {
		event.getMessage().get().delete().subscribe();
		return message.getChannel().block().createMessage(Global.loadingGIF).withMessageReference(message.getId())
		.flatMap(messageRes -> executeSolve(message, messageRes, '>')).then();
	} else if(event.getCustomId().equals("left")) {
		event.getMessage().get().delete().subscribe();
		return message.getChannel().block().createMessage(Global.loadingGIF).withMessageReference(message.getId())
		.flatMap(messageRes -> executeSolve(message, messageRes, '<')).then();
	}
	return Mono.empty();
}
*/
}
