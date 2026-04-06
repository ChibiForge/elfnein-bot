package com.arracso.ElfneinBot.command.message;

import com.arracso.ElfneinBot.util.Util;

import com.arracso.ElfneinBot.model.UserServerActivity;
import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Locator;
import com.arracso.ElfneinBot.util.Locator.Location;
import com.arracso.ElfneinBot.util.Service;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class RegisterRankCommand extends MessageCommand {

	public RegisterRankCommand(){
		commandNames.add("registerrank");
		commandNames.add("register");
		commandNames.add("rr");
		commandId = Global.cmdIdActivityRoles;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		// Check user
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve user id. Please tell <@278957461120090113> to fix me!").then();
		String userId = message.getAuthor().get().getId().asString();
		
		// Check refMessage
		if(!message.getReferencedMessage().isPresent())
			return Util.replyToMessage(message, "You need to reply to your job board.").then();
		Message refMessage = message.getReferencedMessage().get();
		
		// Check its job board message
		Location[] loc = {Location.EMBED,Location.DESCRIPTION};
		String desc = Locator.get(refMessage, loc);
		if(!desc.startsWith("Showing board of <@"+userId))
			return Util.replyToMessage(message, "You need to reply to your job board.").then();
		
		// Check contribution
		Integer contribution = Integer.valueOf(Util.substring(desc.split("Contribution: ")[1].split("\n")[0], 2, -2).replace(",", ""));
		
		// Check guild id
		if(!message.getGuildId().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve guild id. Please tell <@278957461120090113> to fix me!").then();
		String serverId = message.getGuildId().get().asString();
		
		// Retrieve activity
		UserServerActivity activity = Service.userService.getUserActivity(serverId,userId);
		if(activity == null)
			return Util.replyToMessage(message, "User don't have registered activity yet.").then();
		
		Integer lv = activity.getLevel();
		
		char rank = 'E';
		if(contribution>=2100) {
			if(lv>=50) rank = 'S';
			else if (lv>=30) rank = 'A';
			else if (lv>=20) rank = 'B';
			else if (lv>=10) rank = 'C';
			else if (lv>=5) rank = 'D';
		}else if(contribution>=2000) {
			if(lv>60) rank = 'S';
			else if (lv>=40) rank = 'A';
			else if (lv>=20) rank = 'B';
			else if (lv>=10) rank = 'C';
			else if (lv>=5) rank = 'D';
		}else if(contribution>=1800) {
			if(lv>65) rank = 'S';
			else if (lv>=50) rank = 'A';
			else if (lv>=20) rank = 'B';
			else if (lv>=10) rank = 'C';
			else if (lv>=5) rank = 'D';
		}else if(contribution>=1600) {
			if(lv>70) rank = 'S';
			else if (lv>=50) rank = 'A';
			else if (lv>=25) rank = 'B';
			else if (lv>=10) rank = 'C';
			else if (lv>=5) rank = 'D';
		}else if(contribution>=1400) {
			if(lv>=75) rank = 'S';
			else if (lv>=50) rank = 'A';
			else if (lv>=25) rank = 'B';
			else if (lv>=10) rank = 'C';
			else if (lv>=5) rank = 'D';
		}else if(contribution>=1200) {
			if(lv>80) rank = 'S';
			else if (lv>=60) rank = 'A';
			else if (lv>=30) rank = 'B';
			else if (lv>=10) rank = 'C';
			else if (lv>=5) rank = 'D';
		}else if(contribution>=1000) {
			if(lv>90) rank = 'S';
			else if (lv>=75) rank = 'A';
			else if (lv>=50) rank = 'B';
			else if (lv>=10) rank = 'C';
			else if (lv>=5) rank = 'D';
		}else if(contribution>=800) {
			if(lv>100) rank = 'S';
			else if (lv>=80) rank = 'A';
			else if (lv>=60) rank = 'B';
			else if (lv>=20) rank = 'C';
			else if (lv>=5) rank = 'D';
		}else if(contribution>=500) {
			if(lv>200) rank = 'S';
			else if (lv>100) rank = 'A';
			else if (lv>80) rank = 'B';
			else if (lv>50) rank = 'C';
			else if (lv>5) rank = 'D';
		}
		
		// Add roles
		message.getAuthor().get().asMember(message.getGuildId().get()).block().addRole(Snowflake.of(1207416302554193920L)).subscribe();
		if(rank != 'E') {
			message.getAuthor().get().asMember(message.getGuildId().get()).block().addRole(Snowflake.of(1207416298930315366L)).subscribe();
			if(rank != 'D') {
				message.getAuthor().get().asMember(message.getGuildId().get()).block().addRole(Snowflake.of(1207416144714272819L)).subscribe();
				if(rank != 'C') {
					message.getAuthor().get().asMember(message.getGuildId().get()).block().addRole(Snowflake.of(1207411956441546752L)).subscribe();
					if(rank != 'B') {
						message.getAuthor().get().asMember(message.getGuildId().get()).block().addRole(Snowflake.of(1207370824554778664L)).subscribe();
						if(rank != 'A') {
							message.getAuthor().get().asMember(message.getGuildId().get()).block().addRole(Snowflake.of(1207364269298028574L)).subscribe();
						}
					}
				}
			}
		}
		
		// Remove roles
		if(rank != 'S') {
			message.getAuthor().get().asMember(message.getGuildId().get()).block().removeRole(Snowflake.of(1207364269298028574L)).subscribe();
			if(rank != 'A') {
				message.getAuthor().get().asMember(message.getGuildId().get()).block().removeRole(Snowflake.of(1207370824554778664L)).subscribe();
				if(rank != 'B') {
					message.getAuthor().get().asMember(message.getGuildId().get()).block().removeRole(Snowflake.of(1207411956441546752L)).subscribe();
					if(rank != 'C') {
						message.getAuthor().get().asMember(message.getGuildId().get()).block().removeRole(Snowflake.of(1207416144714272819L)).subscribe();
						if(rank != 'D') {
							message.getAuthor().get().asMember(message.getGuildId().get()).block().removeRole(Snowflake.of(1207416298930315366L)).subscribe();
						}
					}
				}
			}
		}
			
		
		
		return Util.replyToMessage(message, "Your rank is " + rank).then();
	}
	/*
	private EmbedCreateSpec getActivityEmbed(UserServerActivity activity) {
		EmbedCreateSpec embed = EmbedCreateSpec.builder()
				.color(Color.SEA_GREEN)
				.title("Rank Server")
				.description("Viewing server rank for <@" + activity.getUser() + ">.")
				.addField("LEVEL", ""+activity.getLevel(), true)
				.addField("MESSAGES", ""+activity.getMessages(),true)
				.addField("EXPERIENCE", ""+activity.getExperience(),true)
				.build();
		return embed;
	}*/
	
}
