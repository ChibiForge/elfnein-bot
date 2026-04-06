package com.arracso.ElfneinBot.command.message;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.arracso.ElfneinBot.model.EventTOTHouse;
import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

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

public class EventTOTCommand extends MessageCommand {
	
	public EventTOTCommand(){
		commandNames.add("tot");
		commandNames.add("trick or treat");
		commandId = Global.eventTOTCmd;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve user id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();
		
		if(!message.getGuildId().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve guild id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();		
		
		return message.getAuthorAsMember().flatMap(member -> {
			if(!Service.eventTOTService.isUserInEvent(member.getGuildId().asString(),member.getId().asString()))
				return Util.replyToMessage(message,"You are not a member of a house yet!\n-# Ask someone to invite you to their house doing `e.house invite` or do `e.house create` to create your own.").then();
			
			String cd = Service.eventTOTService.getCd(member.getGuildId().asString(),member.getId().asString());
			if(cd != null) return Util.replyToMessage(message,cd).then();
			
			List<String> parameter = getParameters(message);
			if(parameter.size()==0)
				return Util.replyToMessage(message,"You have to specify a user.").then();
			
			String targetID = parseId(parameter.get(0));
			if(!Service.eventTOTService.isUserInEvent(member.getGuildId().asString(), targetID))
				return Util.replyToMessage(message,"This user doesn't have a house.").then();
			
			String cleanState = Service.eventTOTService.getCleanState(member.getGuildId().asString(), targetID);
			if(cleanState != null) return Util.replyToMessage(message,cleanState).then();
			
			if(Service.eventTOTService.isSameHouse(member.getGuildId().asString(), member.getId().asString(), targetID))
				return Util.replyToMessage(message,"You cannot go do trick or treat on your own house...").then();
			
			return Util.replyToMessage(message,totContainer(member.getGuildId().asString(),member.getId().asString(),targetID,"","","")).flatMap(msg -> totListener(msg,member.getGuildId().asString(),member.getId().asString(),targetID)).then();
			
		});
		
	}
	
	private String parseId(String par) {
		String id = par;
		if(par.startsWith("<@")) id = par.split("@")[1].split(">")[0];
		return id;
	}
	
	private Container totContainer(String serverId, String userId, String targetId, String result, String ansUserId, String result2) {
		EventTOTHouse house = Service.eventTOTService.getHouse(serverId, targetId);
		Container totContainer = Container.of(
			Color.BLACK,
			TextDisplay.of("# 🎃 Trick or Treat 🎃"),
			TextDisplay.of("<@" + house.getMembers().replace(",",">, <@") + "> someone is knocking on your door."),
			TextDisplay.of("It's <@" + userId + ">! Asking for candy."),
			Separator.of(true, SpacingSize.of(1)),
			TextDisplay.of("Will you give them some candy?"),
			result.equals("to") ?
				ActionRow.of(Button.secondary("tot:action:no", "No").disabled(), Button.secondary("tot:action:yes", "Yes").disabled())
			: ( result.equals("no") ?
				ActionRow.of(Button.danger("tot:action:no", "No").disabled(), Button.secondary("tot:action:yes", "Yes").disabled())
			: ( result.equals("yes") ?
				ActionRow.of(Button.secondary("tot:action:no", "No").disabled(), Button.success("tot:action:yes", "Yes").disabled())
			: 
				ActionRow.of(Button.secondary("tot:action:no:user="+userId+"&target="+house.getId(), "No"), Button.secondary("tot:action:yes:user="+userId+"&target="+house.getId(), "Yes"))
			))
		);
		
		if(result.equals("to") || result.equals("no"))
			totContainer = totContainer.withAddedComponents(
				Separator.of(true, SpacingSize.of(1)),
				TextDisplay.of(result.equals("to")? "Nobody answered the door. What you gonna do?" : "<@" + ansUserId + "> doesn't want to give you any candy. What you gonna do?"),
				result2.equals("to") ?
					ActionRow.of(Button.secondary("tot:trick:egg", "Throw an egg").disabled(), Button.secondary("tot:trick:roll", "Throw a toilet paper roll").disabled(), Button.secondary("tot:trick:home", "Go back home").disabled())
				: ( result2.equals("egg") ?
					ActionRow.of(Button.success("tot:trick:egg", "Throw an egg").disabled(), Button.secondary("tot:trick:roll", "Throw a toilet paper roll").disabled(), Button.secondary("tot:trick:home", "Go back home").disabled())
				: ( result2.equals("roll") ?
					ActionRow.of(Button.secondary("tot:trick:egg", "Throw an egg").disabled(), Button.success("tot:trick:roll", "Throw a toilet paper roll").disabled(), Button.secondary("tot:trick:home", "Go back home").disabled())
				: ( result2.equals("home") ?
					ActionRow.of(Button.secondary("tot:trick:egg", "Throw an egg").disabled(), Button.secondary("tot:trick:roll", "Throw a toilet paper roll").disabled(), Button.success("tot:trick:home", "Go back home").disabled())
				: ActionRow.of(
					Button.secondary("tot:trick:egg:user="+userId+"&target="+house.getId(), "Throw an egg"), 
					Button.secondary("tot:trick:roll:user="+userId+"&target="+house.getId(), "Throw a toilet paper roll"), 
					Button.secondary("tot:trick:home:user="+userId+"&target="+house.getId(), "Go back home"))
				)))
			);
		
		if(result.equals("yes"))
			totContainer = totContainer.withAddedComponents(
				Separator.of(true, SpacingSize.of(1)),
				TextDisplay.of("<@" + ansUserId + "> gives 1 candy to <@" + userId + ">.\nThey unwrap it and eat it while returning home obtaining a `candy wrapper`.")
			);
		
		if(!result2.equals("")) {
			totContainer = totContainer.withAddedComponents(
				Separator.of(true, SpacingSize.of(1)),
				result2.equals("to")?
					TextDisplay.of("<@" + userId + "> goes back home wondering if they should have done something.")
				: ( result2.equals("home")?
					TextDisplay.of("<@" + userId + "> decides to go back home empty handed.")
				: ( result2.equals("egg")?
					TextDisplay.of("<@" + userId + "> throws and egg at the house and starts running away back home.")
				:
					TextDisplay.of("<@" + userId + "> throws toilet paper all over the house and runs back home.")
				))
				
			);
		}
		
		return totContainer;		
	}
	
	private Mono<Void> totListener(Message msg, String serverId, String userId, String targetId) {
		AtomicBoolean edited = new AtomicBoolean(false);
		return Service.client.on(ButtonInteractionEvent.class)
			.filter(e-> e.getMessageId().asString().equals(msg.getId().asString()))
			.filter(e -> e.getCustomId().startsWith("tot:action:"))
			.flatMap(e -> {
				String userIdAns = e.getUser().getId().asString();
				if(!Service.eventTOTService.isUserInEvent(serverId, userIdAns)) 
					return e.reply().withContent("You cannot do that!").withEphemeral(true);
				
				String [] args = e.getCustomId().split(":");
				Map<String,String> metadata = Util.parseButtonMetadata(args[3]);
				if(!metadata.get("target").equals(""+Service.eventTOTService.getHouse(serverId, userIdAns).getId())) 
					return e.reply().withContent("You cannot do that!").withEphemeral(true);
				
				String cd = Service.eventTOTService.getCd(serverId, userIdAns);
				if(cd != null) return e.reply().withContent(cd).withEphemeral(true);
				
				if(Service.eventTOTService.getCd(serverId, userId) != null) 
					return e.reply().withContent("This user already left.").withEphemeral(true);
				
				String result = args[2];
				if(result.equals("yes")) 
					if(!Service.eventTOTService.giveCandy(serverId,userIdAns,userId))
						return e.reply().withContent("You don't have any candy! Go buy some quick!").withEphemeral(true);
				
				edited.set(true);
				
				if(result.equals("no")) 
					return e.edit().withComponents(totContainer(serverId,userId,targetId,result,userIdAns,"")).then(trickListener(msg,serverId,userId,targetId,result,userIdAns)).then();
				
				return e.edit().withComponents(totContainer(serverId,userId,targetId,result,userIdAns,"")).then();		
			})
			.next()
			.timeout(Duration.ofSeconds(30))
			.onErrorResume(TimeoutException.class, e -> {
				if(edited.get()) return Mono.empty();
				return msg.edit().withComponents(totContainer(serverId,userId,targetId,"to","","")).flatMap(msg2 -> trickListener(msg2,serverId,userId,targetId,"to","")).then();
			})
			.then();
	}
	
	private Mono<Void> trickListener(Message msg, String serverId, String userId, String targetId, String result, String userIdAns) {
		AtomicBoolean edited = new AtomicBoolean(false);
		return Service.client.on(ButtonInteractionEvent.class)
			.filter(e-> e.getMessageId().asString().equals(msg.getId().asString()))
			.filter(e -> e.getCustomId().startsWith("tot:trick:"))
			.flatMap(e -> {
				String userIdAns2 = e.getUser().getId().asString();
				String [] args = e.getCustomId().split(":");
				Map<String,String> metadata = Util.parseButtonMetadata(args[3]);
				if(!metadata.get("user").equals(userIdAns2)) return e.reply().withContent("You cannot do that!").withEphemeral(true);
				
				if(Service.eventTOTService.getCd(serverId, userId) != null) 
					return e.reply().withContent("You are on cd!").withEphemeral(true);
				
				String result2 = args[2];
				if(!Service.eventTOTService.trick(serverId,userId,targetId,result2))
					return e.reply().withContent("You don't have any to throw!").withEphemeral(true);
				edited.set(true);
				return e.edit().withComponents(totContainer(serverId,userId,targetId,result,userIdAns,result2)).then();		
			})
			.next()
			.timeout(Duration.ofSeconds(30))
			.onErrorResume(TimeoutException.class, e -> {
				if(edited.get()) return Mono.empty();
				Service.eventTOTService.trick(serverId,userId,targetId,"home");
				return msg.edit().withComponents(totContainer(serverId,userId,targetId,result,userIdAns,"to")).then();
			})
			.then();
	}
	
}
