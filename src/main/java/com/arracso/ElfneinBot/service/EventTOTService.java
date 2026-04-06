package com.arracso.ElfneinBot.service;

import java.util.List;

import com.arracso.ElfneinBot.model.EventTOTHouse;

public interface EventTOTService {
	
	Boolean isUserInEvent(String serverId, String userId);

	void registerHouse(String serverId, String userId, Integer position);

	EventTOTHouse getHouse(String serverId, String userId);

	Boolean giveCoin(String serverId, String userId);

	String cleanHouse(String serverId, String userId);

	List<EventTOTHouse> getHouses(String serverId, String order);

	void joinHouse(String serverId, String inviterId, String userId);

	String buy(String serverId, String userId, String item);

	boolean houseIsFull(String serverId, String userId);

	String getCd(String serverId, String userId);

	String getCleanState(String serverId, String userId);

	boolean giveCandy(String serverId, String giverId, String receiverId);

	boolean trick(String serverId, String userId, String targetId, String trick);

	boolean deleteUser(String serverId, String userId);

	boolean deleteHouse(String serverId, String userId);

	boolean isSameHouse(String serverId, String userId, String targetId);

	void swapHouseLocations(String serverId);
	
}
