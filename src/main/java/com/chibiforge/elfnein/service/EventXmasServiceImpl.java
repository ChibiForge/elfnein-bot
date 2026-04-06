package com.chibiforge.elfnein.service;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.chibiforge.elfnein.model.EventXmasUser;
import com.chibiforge.elfnein.repository.EventXmasUserRepository;

@Service
public class EventXmasServiceImpl implements EventXmasService {
	
	@Autowired
	private EventXmasUserRepository eventXmasUserRepository;

	private static Map<String,Timestamp> lastTimeSpeak = new HashMap<>();
	private static Map<String,Timestamp> lastTimeCasino = new HashMap<>();
	
	@Override
	public Boolean isUserInEvent(String userId) {
		List<EventXmasUser> eventUsers = eventXmasUserRepository.findByUser(userId);
		if(eventUsers.isEmpty()) return false;
		return true;
	}

	@Override
	public String giveRandomItem(String userId) {
		List<EventXmasUser> eventUsers = eventXmasUserRepository.findByUser(userId);
		EventXmasUser user = null;
		if(eventUsers.isEmpty()) {
			user = new EventXmasUser();
			user.setUser(userId);
			user.setType(0);
			user.setCoins(0);
			user.setWood(0);
			user.setPaint(0);
			user.setCraftingToy(0);
			user.setCraftingProgression(0);
			user.setWoodenCarsCrafted(0);
			user.setWoodenCarsGiven(0);
			user.setWoodenCarsReceived(0);
			user.setWoodenDollsCrafted(0);
			user.setWoodenDollsGiven(0);
			user.setWoodenDollsReceived(0);
			user.setWoodenMarbleRunsCrafted(0);
			user.setWoodenMarbleRunsGiven(0);
			user.setWoodenMarbleRunsReceived(0);
		} else {
			user = eventUsers.get(0);
		}
		
		String item = null;
		if((int)(Math.random() * 3) == 0) {
			user.setPaint(user.getPaint()+1);
			item = "paint bottle";
		} else {
			user.setWood(user.getWood()+1);
			item = "wooden log";
		}
		
		eventXmasUserRepository.save(user);
		return item;
	}

	@Override
	public EventXmasUser getUser(String userId) {
		List<EventXmasUser> eventUsers = eventXmasUserRepository.findByUser(userId);
		if(eventUsers.isEmpty()) return null;
		return eventUsers.get(0);
	}

	@Override
	public Integer startCraftingToy(String userId, String toy) {
		EventXmasUser user = getUser(userId);
		if(user == null) return -1;
		if(user.getCraftingToy()!=0) return 0;
		if(toy.equals("car") && user.getWood()>=1) {
			user.setCraftingToy(1);
			user.setWood(user.getWood()-1);
			user.setCraftingProgression(0);
			eventXmasUserRepository.save(user);
			return 1;
		}
		if(toy.equals("doll") && user.getWood()>=1 && user.getPaint()>=2) {
			user.setCraftingToy(2);
			user.setWood(user.getWood()-1);
			user.setPaint(user.getPaint()-2);
			user.setCraftingProgression(0);
			eventXmasUserRepository.save(user);
			return 2;
		}
		if(toy.equals("marble") && user.getWood()>=2 && user.getPaint()>=1) {
			user.setCraftingToy(3);
			user.setWood(user.getWood()-2);
			user.setPaint(user.getPaint()-1);
			user.setCraftingProgression(0);
			eventXmasUserRepository.save(user);
			return 3;
		}
		return -1;
	}

	@Override
	public Integer updateCraftingProgression(String userId) {
		EventXmasUser user = getUser(userId);
		if(user == null) return 0;
		if(user.getCraftingToy() == 0) return 0;
		
		if(lastTimeSpeak.containsKey(userId)) {
			if(lastTimeSpeak.get(userId).before(Timestamp.from(Instant.now().minus(1,ChronoUnit.MINUTES)))) {
				lastTimeSpeak.replace(userId, Timestamp.from(Instant.now()));
				user.setCraftingProgression(user.getCraftingProgression()+1);
				Integer toy = 0;
				Integer duration = getCraftDuration(user.getCraftingToy());
				if(user.getCraftingProgression()>=duration) {
					toy = user.getCraftingToy();
					if(toy == 1) {
						user.setCoins(user.getCoins()+2);
						user.setWoodenCarsCrafted(user.getWoodenCarsCrafted()+1);
					} else if(toy == 2) {
						user.setCoins(user.getCoins()+12);
						user.setWoodenDollsCrafted(user.getWoodenDollsCrafted()+1);
					} else if(toy == 3) {
						user.setCoins(user.getCoins()+8);
						user.setWoodenMarbleRunsCrafted(user.getWoodenMarbleRunsCrafted()+1);
					}
					user.setCraftingToy(0);
					user.setCraftingProgression(0);
				}
				eventXmasUserRepository.save(user);
				return toy;
			}
		} else {
			lastTimeSpeak.put(userId, Timestamp.from(Instant.now()));
		}
		return 0;
	}
	
	@Override
	public List<EventXmasUser> getUsers(String order) {
		if(order.equals("coins")) return eventXmasUserRepository.findAll(Sort.by(Sort.Direction.DESC, "coins"));
		return eventXmasUserRepository.findAll(Sort.by(Sort.Direction.DESC, "coins"));
	}

	@Override
	public Integer getCraftDuration(int i) {
		return i==1?10:(i==2?30:20);
	}

	@Override
	public String updateUserCoins(String targetId, int amount) {
		EventXmasUser user = getUser(targetId);
		Integer coins = user.getCoins();
		if(coins < (-1*amount)) return null;
		user.setCoins(coins + amount);
		eventXmasUserRepository.save(user);
		return "" + coins + "🪙 -> " + user.getCoins() + "🪙";
	}

	@Override
	public String casinoCF(String userId, String side, int amount) {
		EventXmasUser user = getUser(userId);
		Integer coins = user.getCoins();
		if(amount < 1) return "Minimum bet is 1 coins.";
		if(amount > 25) return "Maximum bet is 25 coins.";
		if(amount > coins) return "You don't have enought coins to bet.";
		
		String actualSide = "its **side**";
		String res = "lost";
		Integer value = (int)(Math.random() * 100);
		if(value < 49) actualSide = "**heads**";
		else if(value < 98) actualSide = "**tails**";
		
		if(side.toLowerCase().equals("tails") || side.toLowerCase().equals("t")) {
			if(actualSide.equals("**tails**")) res = "win";
		} else if(side.toLowerCase().equals("heads") || side.toLowerCase().equals("h")) {
			if(actualSide.equals("**heads**")) res = "win";
		} else if(side.toLowerCase().equals("side") || side.toLowerCase().equals("s")) {
			if(actualSide.equals("its **side**")) {
				res = "win";
				amount = amount*45;
			}
		} else return "Thats not a valid side.";
		
		if(lastTimeCasino.containsKey(userId)) {
			Timestamp offCd = lastTimeCasino.get(userId);
			if(offCd.before(Timestamp.from(Instant.now())))
				lastTimeCasino.replace(userId, Timestamp.from(Instant.now().plus(5,ChronoUnit.MINUTES)));
			else return "You will be off cooldown <t:" + (offCd.getTime()/1000) + ":R>";
		} else lastTimeCasino.put(userId, Timestamp.from(Instant.now().plus(5,ChronoUnit.MINUTES)));
		
		user.setCoins(user.getCoins() + ((res.equals("win")?1:-1)*amount));
		eventXmasUserRepository.save(user);
		return "The grinch flipped the coin and it landed on " + actualSide + "! You " + res + " " + amount + " coins!";
	}

}
