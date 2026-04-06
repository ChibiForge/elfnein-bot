package com.chibiforge.elfnein.service;

import discord4j.core.object.entity.Message;

public interface CommandService {
	
	Boolean isActive(Integer commandId, Message message);
	
	Boolean setPerms(Integer commandId, String serverId, String channelID, String userId, Boolean active);

}
