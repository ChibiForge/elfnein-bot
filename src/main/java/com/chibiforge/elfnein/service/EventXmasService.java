package com.chibiforge.elfnein.service;

import java.util.List;

import com.chibiforge.elfnein.model.EventXmasUser;

public interface EventXmasService {
	
	Boolean isUserInEvent(String userId);

	String giveRandomItem(String userId);

	EventXmasUser getUser(String userId);

	Integer startCraftingToy(String userId, String toy);

	Integer updateCraftingProgression(String userId);

	List<EventXmasUser> getUsers(String order);

	Integer getCraftDuration(int i);

	String updateUserCoins(String targetId, int amount);

	String casinoCF(String userId, String side, int amount);
	
}
