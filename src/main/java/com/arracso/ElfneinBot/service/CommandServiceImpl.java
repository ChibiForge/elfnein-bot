package com.arracso.ElfneinBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arracso.ElfneinBot.model.CommandSetting;
import com.arracso.ElfneinBot.repository.CommandSettingRepository;

import discord4j.core.object.entity.Message;

@Service
public class CommandServiceImpl implements CommandService {
	
	@Autowired
	private CommandSettingRepository commandSettingRepository;
	
	@Override
	public Boolean isActive(Integer commandId, Message message) {
		if(commandId == 0) return true;
		
		String serverId = message.getGuildId().isPresent() ? message.getGuildId().get().toString() : null;
		String channelId = message.getChannelId().asString();
		String userId = message.getAuthor().isPresent() ? message.getAuthor().get().getId().asString() : null;
		
		for(CommandSetting commandSetting : commandSettingRepository.findByCommand(commandId)) {
			if((commandSetting.getServer() == null || commandSetting.getServer().equals(serverId))
			&& (commandSetting.getChannel() == null || commandSetting.getChannel().equals(channelId))
			&& (commandSetting.getUser() == null || commandSetting.getUser().equals(userId))
			&& (commandSetting.getActive())) return true;
		}
		
		return false;
	}

}
