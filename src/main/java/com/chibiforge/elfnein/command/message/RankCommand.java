package com.chibiforge.elfnein.command.message;

import java.util.List;

import com.chibiforge.elfnein.model.UserServerActivity;
import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class RankCommand extends MessageCommand {

	public RankCommand(){
		commandNames.add("rank");
		commandNames.add("level");
		commandId = Global.cmdIdActivity;
	}
	
	@Override
	public Mono<Void> execute(Message message) {	
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve user id. Please tell <@278957461120090113> to fix me!").then();
		String userId = message.getAuthor().get().getId().asString();
		
		// Use another user id
		List<String> parameter = getParameters(message);
		if(!parameter.isEmpty()) {
			if(parameter.get(0).startsWith("<@"))
				userId = parameter.get(0).split("@")[1].split(">")[0];
			else userId = parameter.get(0);
		}
		
		if(!message.getGuildId().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve guild id. Please tell <@278957461120090113> to fix me!").then();
		
		String serverId = message.getGuildId().get().asString();
		
		UserServerActivity activity = Service.userService.getUserActivity(serverId,userId);
		
		if(activity == null)
			return Util.replyToMessage(message, "User don't have registered activity yet.").then();
		
		return Util.replyToMessage(message, getActivityEmbed(activity)).then();
	}
	
	
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
	}
	
}
