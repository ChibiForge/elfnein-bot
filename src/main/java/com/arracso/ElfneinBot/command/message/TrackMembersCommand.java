package com.arracso.ElfneinBot.command.message;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.Embed;
import discord4j.core.object.Embed.Field;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

public class TrackMembersCommand extends MessageCommand {
	
	public TrackMembersCommand() {
		this.commandNames.add("trackmembers");
		this.commandNames.add("tm");
		this.commandId = Global.cmdIdTrack;
	}	
	
	@Override
	public Mono<Void> execute(Message message) {
		// Get command parameters
		List<String> commandParameters = getParameters(message);
		if(commandParameters.isEmpty())
			return trackMembers(message);
		else if(commandParameters.get(0).equals("lost"))
			if(commandParameters.size() == 2)
				return Service.paginationService.paginate("Lost Members","**Showing lost members of <@"+commandParameters.get(1)+">**\n",
						Service.userService.getLostUsersFromClan(commandParameters.get(1)), message);
			

		return Mono.empty();
	}
	
	private Mono<Void> trackMembers(Message message) {
		System.out.println("Tracking members");
		// Check refMessage
		if(!message.getReferencedMessage().isPresent())
			return Util.replyToMessage(message, "You need to reply to clan view.").then();
		Message refMessage = message.getReferencedMessage().get();
		
		// Get embed
		if(refMessage.getEmbeds().size()==0
			|| !refMessage.getEmbeds().get(0).getTitle().isPresent()
			|| !refMessage.getEmbeds().get(0).getTitle().get().startsWith("View Clan"))
			return Util.replyToMessage(message, "You need to reply to clan view.").then();
		Embed embed = refMessage.getEmbeds().get(0);
		
		// Get user id
		String desc = embed.getDescription().orElse("");
		if(!desc.startsWith("Showing clan details"))
			return Util.replyToMessage(message, "Error.").then();
		String userId = desc.split("@")[1].split(">")[0];
		
		// Get followers
		if(embed.getFields().isEmpty())
			return Util.replyToMessage(message, "User don't have followers.").then();
		Field field = embed.getFields().get(0);
		if(!field.getName().equals("Followers")) 
			return Util.replyToMessage(message, "User don't have followers.").then();
		List<String> followersID = extractIds(field.getValue());
		
		for(String followerID: followersID) {
			Service.userService.updateUserClan(userId,followerID);
		}
		
		return Util.replyToMessage(message, "Tracking clan members. Please go through all pages.").then(setupListener(message.getChannel().block(), message, refMessage.getId().asString()));
	}
	
	private Mono<Void> setupListener(MessageChannel channel, Message messageCmd, String messageId) {
		AtomicBoolean activated = new AtomicBoolean(false);
		return Mono.defer(() -> {
			return Service.client.on(MessageUpdateEvent.class)
			.filter(event -> event.getMessage().block().getId().asString().equals(messageId))
			.flatMap(event -> event.getMessage().flatMap(message -> {
				activated.set(true);
				// Get embed
				if(message.getEmbeds().isEmpty()) return Mono.empty();
				Embed embed = message.getEmbeds().get(0);
				
				// Get user id
				String desc = embed.getDescription().orElse("");
				if(!desc.startsWith("Showing clan details")) return Mono.empty();
				String userId = desc.split("@")[1].split(">")[0];
				
				// Get followers
				if(embed.getFields().isEmpty()) return Mono.empty();
				Field field = embed.getFields().get(0);
				if(!field.getName().equals("Followers")) return Mono.empty();
				List<String> followersID = extractIds(field.getValue());
				
				Service.userService.updateUsersClan(userId,followersID);
				
				/*
				for(String followerID: followersID) {
					Service.userService.updateUserClan(userId,followerID);
				}*/
				
				System.out.println("Reading: " + embed.getFooter().get().getText());
				
				return Mono.empty();
			}))
			.timeout(Duration.ofSeconds(60))
			.onErrorResume(TimeoutException.class, e -> {
				if(activated.get()) return setupListener(channel, messageCmd, messageId);
		        return Util.replyToMessage(messageCmd, "Timeout.").then();
		    }).next();
		
		}).then();
	}
	
	
	private List<String> extractIds(String input) {
		List<String> ids = new ArrayList<>();
		Pattern pattern = Pattern.compile("<@(\\d+)>");
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) ids.add(matcher.group(1));
		return ids;
	}
}
