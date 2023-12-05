package com.arracso.ElfneinBot.service;

import discord4j.core.object.entity.Message;

public interface CommandService {
	
	Boolean isActive(Integer commandId, Message message);

}
