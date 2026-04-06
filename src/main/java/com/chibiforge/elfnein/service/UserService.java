package com.chibiforge.elfnein.service;

import java.util.List;

import com.chibiforge.elfnein.model.UserServerActivity;

public interface UserService {
	
	Boolean isBanned(String userId);
	
	String getReasonOfBan(String userId);

	Boolean updateUserActivity(String serverId, String userId);
	
	UserServerActivity getUserActivity(String serverId, String userId);

	void fixServerActivity(String serverId);

	List<UserServerActivity> getUsersActivity(String serverId);
	
	void setUserLoopSize(String user, Integer size);
	
	Integer getUserLoopSize(String user);

	void updateUserClan(String userId, String followerID);
	void updateUsersClan(String shogunId, List<String> followerIds);
	
	List<String> getLostUsersFromClan(String userId);

	

}
