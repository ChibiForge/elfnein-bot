package com.arracso.ElfneinBot.command.message;

import java.util.Random;

import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class TriggerMessageCommand extends MessageCommand {
	
	private boolean hasToMentionBot;
	private String[] triggers;
	private String[] answers;
	private double chance;
	
	public TriggerMessageCommand(String[] triggers,String[] answers,double chance,boolean hasToMentionBot){
		this.hasToMentionBot = hasToMentionBot;
		this.triggers = triggers;
		this.answers = answers;
		this.chance = chance;
		this.commandId = 0;
	}
	
	public TriggerMessageCommand(String[] triggers,String[] answers,double chance){
		this(triggers,answers,chance,false);
	}
	
	public TriggerMessageCommand(String[] triggers,String[] answers,boolean hasToMentionBot) {
		this(triggers,answers,1,hasToMentionBot);
	}
	
	public TriggerMessageCommand(String[] triggers,String[] answers) {
		this(triggers,answers,1,false);
	}
	
	@Override
	public Boolean check(Message message) {
		if(message.getAuthor().map(user -> !user.isBot()).orElse(false)) {
			String messageStr = message.getContent().toLowerCase();
			for(String trigger : triggers)
				if(messageStr.contains(trigger)) return true;
		}
		
		return false;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		Random rn = new Random();
		if(!hasToMentionBot && chance >= rn.nextDouble() || Util.mentionsBot(message)) {
			String response = answers[rn.nextInt(answers.length)];
			return message.getChannel().flatMap(channel -> channel.createMessage(response)
			.withMessageReference(message.getId()))
		    .then();
		}else return Mono.empty();
	}

}
