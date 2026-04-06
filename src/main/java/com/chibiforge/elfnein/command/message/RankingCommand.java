package com.chibiforge.elfnein.command.message;

import java.util.ArrayList;
import java.util.List;

import com.chibiforge.elfnein.model.UserServerActivity;
import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class RankingCommand extends MessageCommand {

	public RankingCommand(){
		commandNames.add("ranking");
		commandNames.add("leaderboard");
		commandNames.add("topusers");
		commandId = Global.cmdIdActivity;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve user id. Please tell <@278957461120090113> to fix me!").then();
		
		if(!message.getGuildId().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve guild id. Please tell <@278957461120090113> to fix me!").then();
		
		String serverId = message.getGuildId().get().asString();
		
		List<UserServerActivity> activityList = Service.userService.getUsersActivity(serverId);
		
		if(activityList == null)
			return Util.replyToMessage(message, "Server don't have registered activity yet.").then();
		
		return Service.paginationService.paginate("Ranking Server","",getRankingLines(activityList), message); 
	}
	
	private List<String> getRankingLines(List<UserServerActivity> activityList){
		List<String> rankingLines = new ArrayList<String>();
		for(int i = 0; i<activityList.size(); i++) {
			UserServerActivity activity =  activityList.get(i);
			rankingLines.add(""+(i+1) + ". <@" + activity.getUser() + "> - Lv" + activity.getLevel() + " - messages: " + activity.getMessages());
		}
		return rankingLines;
	}
}
