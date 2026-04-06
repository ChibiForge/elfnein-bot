package com.chibiforge.elfnein.service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chibiforge.elfnein.model.ClanTracking;
import com.chibiforge.elfnein.model.UserLoopSize;
import com.chibiforge.elfnein.model.UserServerActivity;
import com.chibiforge.elfnein.repository.BannedUserRepository;
import com.chibiforge.elfnein.repository.ClanTrackingRepository;
import com.chibiforge.elfnein.repository.UserLoopSizeRepository;
import com.chibiforge.elfnein.repository.UserServerActivityRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private BannedUserRepository bannedUserRepository;
	
	@Autowired
	private UserServerActivityRepository userServerActivityRepository;
	
	@Autowired
	private UserLoopSizeRepository userLoopSizeRepository;
	
	@Autowired
	private ClanTrackingRepository clanTrackingRepository;
	
	@Override
	public Boolean isBanned(String userId) {
		return bannedUserRepository.findByUser(userId).size()>0;
	}

	@Override
	public String getReasonOfBan(String userId) {
		return bannedUserRepository.findByUser(userId).get(0).getReason();
	}

	@Override
	public Boolean updateUserActivity(String serverId, String userId) {
		Boolean change = false;
		
		Timestamp ts = Timestamp.from(Instant.now());
		
		List<UserServerActivity> activity = userServerActivityRepository.findByServerAndUser(serverId, userId);
		
		if(activity.isEmpty()) {
			UserServerActivity userServerActivity = new UserServerActivity();
			userServerActivity.setServer(serverId);
			userServerActivity.setUser(userId);
			userServerActivity.setMessages(1L);
			userServerActivity.setExperience(1L);
			userServerActivity.setLevel(0);
			userServerActivity.setLastActivity(ts);
			userServerActivityRepository.save(userServerActivity);
		} else {			
			UserServerActivity userServerActivity = activity.get(0);
			Long seconds = Math.min((ts.getTime() - userServerActivity.getLastActivity().getTime())/1000,xpWindows);
			
			if(seconds>=xpSpan) {
				Long totalExp = userServerActivity.getExperience()+seconds/xpSpan;
				
				userServerActivity.setExperience(totalExp);
				userServerActivity.setMessages(userServerActivity.getMessages()+1);
				userServerActivity.setLastActivity(ts);
				
				Integer level = userServerActivity.getLevel();
				Long reqExp = (long) ((baseLv*Math.pow(incrLv,level+1)-baseLv)/(incrLv-1));
				
				if(reqExp<=totalExp) {
					change = true;
					userServerActivity.setLevel(level+1);
				}
				
				userServerActivityRepository.save(userServerActivity);
			}
		}
		
		return change;
	}

	@Override
	public UserServerActivity getUserActivity(String serverId, String userId) {
		List<UserServerActivity> activity = userServerActivityRepository.findByServerAndUser(serverId, userId);
		if(activity.isEmpty()) return null;
		else return activity.get(0);
	}
	
	@Override
	public List<UserServerActivity> getUsersActivity(String serverId) {
		return userServerActivityRepository.findByServerOrderByExperienceDesc(serverId);
	}
	
	
	@Override
	public void fixServerActivity(String serverId) {
		List<UserServerActivity> activities = userServerActivityRepository.findByServer(serverId);
		for(UserServerActivity activity : activities) {
			Integer level = 0;
			while((baseLv*Math.pow(incrLv,level)-baseLv)/(incrLv-1) <= activity.getExperience())
				level ++;
			activity.setLevel(level-1);
			userServerActivityRepository.save(activity);
		}
	}
	
	@Override
	public void setUserLoopSize(String user, Integer size) {
		List<UserLoopSize> userLoopSizes = userLoopSizeRepository.findByUser(user);
		if(userLoopSizes.isEmpty()) {
			UserLoopSize userLoopSize = new UserLoopSize();
			userLoopSize.setUser(user);
			userLoopSize.setSize(size);
			userLoopSizeRepository.save(userLoopSize);
		}else {
			UserLoopSize userLoopSize = userLoopSizes.get(0);
			userLoopSize.setSize(size);
			userLoopSizeRepository.save(userLoopSize);
		}
	}

	@Override
	public Integer getUserLoopSize(String user) {
		List<UserLoopSize> userLoopSize = userLoopSizeRepository.findByUser(user);
		if(userLoopSize.isEmpty()) return null;
		return userLoopSize.get(0).geSize();
	}
	
	@Override
	public void updateUserClan(String shogunId, String followerId) {
		List<ClanTracking> clanTrackings = clanTrackingRepository.findByUser(followerId);
		
		Timestamp ts = Timestamp.from(Instant.now());
		ClanTracking clanTracking = new ClanTracking();
		
		if(!clanTrackings.isEmpty()) clanTracking = clanTrackings.get(0);

		clanTracking.setShogun(shogunId);
		clanTracking.setUser(followerId);
		clanTracking.setUpdated(ts);
		
		clanTrackingRepository.save(clanTracking);
	}
	
	
	@Override
	@Transactional
	public void updateUsersClan(String shogunId, List<String> followerIds) {
		Timestamp now = Timestamp.from(Instant.now());

		// Get all existing tracking entries for the given followers
		List<ClanTracking> existingTrackings = clanTrackingRepository.findByUserIn(followerIds);

		// Map for fast lookup
		Map<String, ClanTracking> trackingMap = new HashMap<>();
		for (ClanTracking tracking : existingTrackings) {
			trackingMap.put(tracking.getUser(), tracking);
		}

		List<ClanTracking> updatedTrackings = new ArrayList<>();

		for (String followerId : followerIds) {
			ClanTracking tracking = trackingMap.getOrDefault(followerId, new ClanTracking());
			tracking.setShogun(shogunId);
			tracking.setUser(followerId);
			tracking.setUpdated(now);
			updatedTrackings.add(tracking);
		}

		clanTrackingRepository.saveAll(updatedTrackings);
	}
	
	@Override
	public List<String> getLostUsersFromClan(String userId) {
		List<ClanTracking> cts = clanTrackingRepository.findByShogun(userId);
		
		Timestamp ts = Timestamp.from(Instant.now().minus(Duration.ofHours(6)));
		List<String> userIds = new ArrayList<String>();
		for(ClanTracking ct:cts) {
			if(ct.getUpdated().before(ts)) {
				userIds.add(ct.getUser() + " - <@" + ct.getUser() + ">");
			}
		}
		
		return userIds;
	}
	///////////////
	// Constants //
	///////////////
	
	Integer xpSpan = 6; // 6 seconds per 1 exp
	Integer xpWindows = 600; // 10 minutes
	Integer baseLv = 1000;
	Float incrLv = 1.05F;


	
}
