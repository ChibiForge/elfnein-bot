package com.arracso.ElfneinBot.service;


import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;

import com.arracso.ElfneinBot.game.bleach.Hollow;
import com.arracso.ElfneinBot.model.BleachEventUser;

public interface BleachEventService {
	
	Boolean isUserInEvent(String serverId, String userId);
	BleachEventUser getUser(String serverId, String userId);
	void registerUser(String serverId, String userId, String fandom, String technique, Integer reiatsu);
	Boolean isUserInTrainCd(String guildId, String userId);
	Timestamp getUserTrainCd(String guildId, String userId);
	void setTraining(String guildId, String userId, String technique, Map<String, String> metadata);
	List<BleachEventUser> getUsers(String guildId, String order);
	Boolean canHollowSpawn(String channelId);
	Hollow getRandomHollow(String channelId);
	Boolean isUserInjured(String guildId, String userId);
	Timestamp getUserInjureCd(String guildId, String userId);
	Boolean registerShinigamiIntoBattle(String guildId, String channelId, String userId, String technique);
	Map<String, List<String>> performBattle(String guildId, String channelId, Hollow hollow);
	Boolean nameZanpakuto(String guildId, String userId, String ascend, String name);
	Boolean isUserInHealCd(String guildId, String userId);
	Pair<Timestamp, Integer> healShinigami(String guildId, String userId, String targetId);
	void forceHeal(String guildId, String userId);
	Timestamp getUserHealCd(String guildId, String userId);
	Boolean isChannelNotAllowed(String channelId);
	String updateUser(String type, String guildId, String targetId, String stat, String value);
}
