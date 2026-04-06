package com.arracso.ElfneinBot.command.message;

import java.util.List;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class SetPermissionCommand extends MessageCommand {
	
	public SetPermissionCommand(){
		commandNames.add("setpermission");
		commandNames.add("setperm");
		commandNames.add("sp");
		commandId = Global.cmdAdmin;
	}
	
	@Override
	public Mono<Void> execute(Message message) {		
		// Get command parameters
		List<String> commandParameters = getParameters(message);
		if(commandParameters.isEmpty())
			return Util.replyToMessage(message, "*Command usage:* `setpermission targetPerm s:serverID c:channelID u:channelID active`").then();
		
		// Get target perm
		Integer targetPerm = getTargetPerm(commandParameters.get(0));
		if(targetPerm == 0) 
			return Util.replyToMessage(message, "Invalid permission. Valid permissions are: datesolver, ds").then();
		
		// Get options
		String guildID = null;
		String channelID = null;
		String userID = null;
		Boolean active = null;
		for(int i = 1;i<commandParameters.size();i++) {
			String param = commandParameters.get(i);
			if(param.toLowerCase().startsWith("s:")) guildID = param.substring(2);
			else if(param.toLowerCase().startsWith("c:")) channelID = param.substring(2);
			else if(param.toLowerCase().startsWith("u:")) userID = param.substring(2);
			else if(param.toLowerCase().equals("true") || param.toLowerCase().equals("t")) active = true;
			else if(param.toLowerCase().equals("false") || param.toLowerCase().equals("f")) active = false;
		}
		
		// Validate formats
        if (guildID != null && !guildID.matches("\\d+")) return Util.replyToMessage(message, "Invalid server ID format. Please provide a numeric server ID.").then();
        if (channelID != null && !channelID.matches("\\d+")) return Util.replyToMessage(message, "Invalid channel ID format. Please provide a numeric channel ID.").then();
        if (userID != null && !userID.matches("\\d+")) return Util.replyToMessage(message, "Invalid channel ID format. Please provide a numeric channel ID.").then();
        
        Boolean activated = true;
        try {
        	activated = Service.commandService.setPerms(targetPerm, guildID, channelID, userID, active);
        }catch(Exception e) {
        	e.printStackTrace();
        }
        
        
        if(activated) {
        	return Util.replyToMessage(message, "Permission activated.").then();
        } else {
        	return Util.replyToMessage(message, "Permission deactivated.").then();
        }
        
	}

	private Integer getTargetPerm(String str) {
		if(str.toLowerCase().equals("solver")
			|| str.toLowerCase().equals("datesolver")
			|| str.toLowerCase().equals("ds")
			|| str.toLowerCase().equals("1")
		) return 1;
		
		if(str.matches("\\d+")) return Integer.valueOf(str);
		
		return 0;
	}
	
	

}
