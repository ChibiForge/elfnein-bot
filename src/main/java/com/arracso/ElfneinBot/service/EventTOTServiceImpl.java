package com.arracso.ElfneinBot.service;


import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arracso.ElfneinBot.model.EventTOTHouse;
import com.arracso.ElfneinBot.model.EventTOTUser;
import com.arracso.ElfneinBot.repository.EventTOTHouseRepository;
import com.arracso.ElfneinBot.repository.EventTOTUserRepository;

@Service
public class EventTOTServiceImpl implements EventTOTService {
	
	@Autowired
	private EventTOTUserRepository eventTOTUserRepository;
	@Autowired
	private EventTOTHouseRepository eventTOTHouseRepository;

	@Override
	public Boolean isUserInEvent(String serverId, String userId) {
		List<EventTOTUser> eventUsers = eventTOTUserRepository.findByServerAndUser(serverId, userId);
		if(eventUsers.isEmpty()) return false;
		return true;
	}

	@Override
	public void registerHouse(String serverId, String userId, Integer position) {
		EventTOTHouse house  = new EventTOTHouse();
		house.setServer(serverId);
		house.setPosition(position);
		house.setMembers(userId);
		house.setCoins(0);
		house.setCandy(12);
		house.setEggs(0);
		house.setToiletPaperRolls(0);
		house.setCandyGiven(0);
		house.setCandyWrappers(0);
		house.setEggThrows(0);
		house.setEggshells(0);
		house.setToiletPaperRollThrows(0);
		house.setToiletPaperScraps(0);
		house.setDirtyTime(0);
		house.setDirtyState(0);
		house = eventTOTHouseRepository.save(house);
		EventTOTUser user = new EventTOTUser();
		user.setServer(serverId);
		user.setUser(userId);
		user.setHouse(house.getId());
		user.setCoinsEarned(0);
		user.setCandyGiven(0);
		user.setCandyWrappers(0);
		user.setEggThrows(0);
		user.setEggshells(0);
		user.setToiletPaperRollThrows(0);
		user.setToiletPaperScraps(0);
		eventTOTUserRepository.save(user);
	}

	@Override
	public EventTOTHouse getHouse(String serverId, String userId) {
		List<EventTOTUser> eventUsers = eventTOTUserRepository.findByServerAndUser(serverId, userId);
		if(eventUsers.isEmpty()) return null;
		return eventTOTHouseRepository.findById(eventUsers.get(0).getHouse()).orElse(null); 
	}

	@Override
	public Boolean giveCoin(String serverId, String userId) {
	    return eventTOTUserRepository.findFirstByServerAndUser(serverId, userId)
        .flatMap(user ->
        	eventTOTHouseRepository.findById(user.getHouse())
            .map(house -> {
                house.setCoins(house.getCoins() + 1);
                user.setCoinsEarned(user.getCoinsEarned() + 1);

                eventTOTHouseRepository.save(house);
                eventTOTUserRepository.save(user);

                return true;
            })
        )
        .orElse(false);
	}

	@Override
	public String cleanHouse(String serverId, String userId) {
		return eventTOTUserRepository.findFirstByServerAndUser(serverId, userId)
		.flatMap(user ->
			eventTOTHouseRepository.findById(user.getHouse())
		    .map(house -> {
		    	Integer state = 0;
		        if(house.getDirtyState() == 0) return "Your house is already clean.";
		        else if(house.getDirtyState() == 1) {
		        	if(house.getDirtyEnd().before(Timestamp.from(Instant.now()))) {
		        		house.setDirtyState(0);
		        		state = 0;
		        	} else return "Your house is already being cleaned.";
		        } else if(user.getCdHome() != null && user.getCdHome().after(Timestamp.from(Instant.now()))) {
		        	return "You are away from your house. You'll be there <t:" + user.getCdHome().getTime()/1000 + ":R>";
		        }  else if(house.getDirtyState() == 2) {
		        	state = 2;
		        	house.setDirtyTime(house.getDirtyTime()+(int)Duration.between(
	        		    house.getDirtyEnd().toInstant(),
	        		    Instant.now().plus(15, ChronoUnit.MINUTES)
	        		).toMinutes());
		        	user.setCdClean(Timestamp.from(Instant.now().plus(15, ChronoUnit.MINUTES)));
		        	house.setDirtyEnd(Timestamp.from(Instant.now().plus(15, ChronoUnit.MINUTES)));
		        	user.setEggshells(user.getEggshells()+1);
		        	house.setEggshells(house.getEggshells()+1);
		        	house.setDirtyState(1);
		        } else if(house.getDirtyState() == 3) {
		        	state = 3;
		        	house.setDirtyTime(house.getDirtyTime()+(int)Duration.between(
	        		    house.getDirtyEnd().toInstant(),
	        		    Instant.now().plus(30, ChronoUnit.MINUTES)
	        		).toMinutes());
		        	user.setCdClean(Timestamp.from(Instant.now().plus(30, ChronoUnit.MINUTES)));
		        	house.setDirtyEnd(Timestamp.from(Instant.now().plus(30, ChronoUnit.MINUTES)));
		        	user.setToiletPaperScraps(user.getToiletPaperScraps()+1);
		        	house.setToiletPaperScraps(house.getToiletPaperScraps()+1);
		        	house.setDirtyState(1);
		        }
		        eventTOTHouseRepository.save(house);
		        eventTOTUserRepository.save(user);
		        
				if(state == 1) return "Your house is already being cleaned.";
				else if(state == 2) return "You started cleaning your house from eggshells. This will take you 15 minutes.";
				else if(state == 3) return "You started cleaning your house from toilet paper scraps. This will take you 30 minutes.";
				return "Your house is already clean.";
		    })
		)
		.orElse("Your house is already clean.");
	}

	@Override
	public List<EventTOTHouse> getHouses(String serverId, String order) {
		if(order.equals("wrappers"))  return eventTOTHouseRepository.findByServerOrderByCandyWrappersDesc(serverId);
		else if(order.equals("tricks"))  return eventTOTHouseRepository.findByServerOrderByTricksDesc(serverId);
		else if(order.equals("treats")) return eventTOTHouseRepository.findByServerOrderByCandyGivenDesc(serverId);
		else if(order.equals("cleaning")) return eventTOTHouseRepository.findByServerOrderByDirtyTimeDesc(serverId);
		
		return mergeRankings(
			eventTOTHouseRepository.findByServerOrderByCandyWrappersDesc(serverId),
			eventTOTHouseRepository.findByServerOrderByTricksDesc(serverId), 
			eventTOTHouseRepository.findByServerOrderByCandyGivenDesc(serverId),
			eventTOTHouseRepository.findByServerOrderByDirtyTimeDesc(serverId)
		);
	}
	
	private List<EventTOTHouse> mergeRankings(List<EventTOTHouse> wrappersRank, List<EventTOTHouse> tricksRank, List<EventTOTHouse> treatsRank, List<EventTOTHouse> cleaningRank) {
		Map<Long,Integer> points = new HashMap<>();
		
		Integer pointsAux = 0;
		points.merge(wrappersRank.get(wrappersRank.size()-1).getId(), pointsAux, Integer::sum);
		for(int i = wrappersRank.size()-2; i>=0; i--) {
			EventTOTHouse act = wrappersRank.get(i);
			EventTOTHouse ant = wrappersRank.get(i+1);
			if(act.getCandyWrappers() != ant.getCandyWrappers()) pointsAux = pointsAux + 2;
			points.merge(act.getId(), pointsAux, Integer::sum);
		}
		
		pointsAux = 0;
		points.merge(tricksRank.get(tricksRank.size()-1).getId(), pointsAux, Integer::sum);
		for(int i = tricksRank.size()-2; i>=0; i--) {
			EventTOTHouse act = tricksRank.get(i);
			EventTOTHouse ant = tricksRank.get(i+1);
			if(act.getEggThrows() + act.getToiletPaperRollThrows() != ant.getEggThrows() + ant.getToiletPaperRollThrows()) pointsAux = pointsAux + 1;
			points.merge(act.getId(), pointsAux, Integer::sum);
		}
		
		pointsAux = 0;
		points.merge(treatsRank.get(treatsRank.size()-1).getId(), pointsAux, Integer::sum);
		for(int i = treatsRank.size()-2; i>=0; i--) {
			EventTOTHouse act = treatsRank.get(i);
			EventTOTHouse ant = treatsRank.get(i+1);
			if(act.getCandyGiven() != ant.getCandyGiven()) pointsAux = pointsAux + 1;
			points.merge(act.getId(), pointsAux, Integer::sum);
		}
		
		pointsAux = 0;
		points.merge(cleaningRank.get(0).getId(), pointsAux, Integer::sum);
		for(int i = 1; i<cleaningRank.size()-1; i++) {
			EventTOTHouse act = cleaningRank.get(i);
			EventTOTHouse ant = cleaningRank.get(i-1);
			if(act.getDirtyTime() != ant.getDirtyTime()) pointsAux = pointsAux + 1;
			points.merge(act.getId(), pointsAux, Integer::sum);
		}
		
		List<EventTOTHouse> finalRanking = Stream
            .of(wrappersRank, tricksRank, treatsRank, cleaningRank)
            .flatMap(List::stream)
            .distinct()
            .sorted(Comparator.comparingInt(
                    (EventTOTHouse h) -> points.getOrDefault(h.getId(), 0))
                    .reversed())
            .toList();

		return finalRanking;
	}

	@Override
	public void joinHouse(String serverId, String inviterId, String userId) {
		List<EventTOTUser> eventUsers = eventTOTUserRepository.findByServerAndUser(serverId, inviterId);
		if(!eventUsers.isEmpty()) {
			eventTOTHouseRepository.findById(eventUsers.get(0).getHouse())
			.ifPresent(house ->{
				EventTOTUser user = new EventTOTUser();
				user.setServer(serverId);
				user.setUser(userId);
				user.setHouse(house.getId());
				user.setCoinsEarned(0);
				user.setCandyGiven(0);
				user.setCandyWrappers(0);
				user.setEggThrows(0);
				user.setEggshells(0);
				user.setToiletPaperRollThrows(0);
				user.setToiletPaperScraps(0);
				eventTOTUserRepository.save(user);
				
				house.setMembers(house.getMembers() + "," + userId);
				eventTOTHouseRepository.save(house);
			}); 			
		}
		
	}

	@Override
	public String buy(String serverId, String userId, String item) {
		List<EventTOTUser> eventUsers = eventTOTUserRepository.findByServerAndUser(serverId, userId);
		if(!eventUsers.isEmpty()) {
			return eventTOTHouseRepository.findById(eventUsers.get(0).getHouse())
			.map(house -> {
				String answer = "You don't have enought coins to buy this item.";
				if(item.equals("candy") && house.getCoins()>=1) {
					house.setCoins(house.getCoins()-1);
					house.setCandy(house.getCandy()+12);
					answer = "You just bought a `bag of candies` for 1 coin.";
				} else if(item.equals("eggs") && house.getCoins()>=1) {
					house.setCoins(house.getCoins()-1);
					house.setEggs(house.getEggs()+6);
					answer = "You just bought an `egg carton` for 1 coin.";
				} else if(item.equals("rolls") && house.getCoins()>=1) {
					house.setCoins(house.getCoins()-1);
					house.setToiletPaperRolls(house.getToiletPaperRolls()+4);
					answer = "You just bought a `toilet paper roll pack` for 1 coin.";
				}
				eventTOTHouseRepository.save(house);
				return answer;
			}).orElse("You don't even have a home.");			
		}
		return "Do you even exist?";
	}

	@Override
	public boolean houseIsFull(String serverId, String userId) {
		List<EventTOTUser> eventUsers = eventTOTUserRepository.findByServerAndUser(serverId, userId);
		if(!eventUsers.isEmpty()) {
			return eventTOTHouseRepository.findById(eventUsers.get(0).getHouse()).map(house -> house.getMembers().split(",").length>4).orElse(true);			
		}
		return true;
	}

	@Override
	public String getCd(String serverId, String userId) {
		List<EventTOTUser> eventUsers = eventTOTUserRepository.findByServerAndUser(serverId, userId);
		if(!eventUsers.isEmpty()) {
			EventTOTUser user = eventUsers.get(0);
			if(user.getCdHome() != null && user.getCdHome().after(Timestamp.from(Instant.now())))
				return "You are returning back home. You'll be there <t:" + user.getCdHome().getTime()/1000 + ":R>.";
			if(user.getCdClean() != null && user.getCdClean().after(Timestamp.from(Instant.now())))
				return "You are cleaning your house. You'll be done <t:" + user.getCdClean().getTime()/1000 + ":R>.";
		}
		return null;
	}

	@Override
	public String getCleanState(String serverId, String userId) {
		List<EventTOTUser> eventUsers = eventTOTUserRepository.findByServerAndUser(serverId, userId);
		if(!eventUsers.isEmpty()) {
			return eventTOTHouseRepository.findById(eventUsers.get(0).getHouse()).map(house -> {
				if (house.getDirtyState() == 1) {
					if(house.getDirtyEnd() != null && house.getDirtyEnd().before(Timestamp.from(Instant.now()))) {
						house.setDirtyState(0);
						eventTOTHouseRepository.save(house);
					}else {
						return "This house is being cleaned right now. Looks like we won't be able to get any candy. Let's come back later.";
					}
				}
				if (house.getDirtyState() == 2) return "You can smell rotten eggs coming from that house. Looks like we won't be able to get any candy. Let's come back later.";
				if (house.getDirtyState() == 3) return "This house is covered in toilet paper. Looks like we won't be able to get any candy. Let's come back later.";
				return null;
			}).orElse(null);			
		}
		return null;
	}

	@Override
	public boolean giveCandy(String serverId, String giverId, String receiverId) {
		List<EventTOTUser> giverUsers = eventTOTUserRepository.findByServerAndUser(serverId, giverId);
		List<EventTOTUser> receiverUsers = eventTOTUserRepository.findByServerAndUser(serverId, receiverId);
		if(!giverUsers.isEmpty() && !receiverUsers.isEmpty()) {
			EventTOTUser giver = giverUsers.get(0);
			EventTOTUser receiver = receiverUsers.get(0);
			return eventTOTHouseRepository.findById(giver.getHouse()).map(giverHouse -> eventTOTHouseRepository.findById(receiver.getHouse()).map(receiverHouse -> {
				if(giverHouse.getCandy()==0) return false;
				giverHouse.setCandy(giverHouse.getCandy()-1);
				giverHouse.setCandyGiven(giverHouse.getCandyGiven()+1);
				giver.setCandyGiven(giver.getCandyGiven()+1);
				receiverHouse.setCandyWrappers(receiverHouse.getCandyWrappers()+1);
				receiver.setCandyWrappers(receiver.getCandyWrappers()+1);
				receiver.setCdHome(Timestamp.from(Instant.now().plus(homeDistances[giverHouse.getPosition()][receiverHouse.getPosition()], ChronoUnit.MINUTES)));
				eventTOTUserRepository.save(giver);
				eventTOTUserRepository.save(receiver);
				eventTOTHouseRepository.save(giverHouse);
				eventTOTHouseRepository.save(receiverHouse);
				return true;
			}).orElse(null)).orElse(null);
		}
		return false;
	}
	
	@Override
	public boolean trick(String serverId, String userId, String targetId, String trick) {
		List<EventTOTUser> users = eventTOTUserRepository.findByServerAndUser(serverId, userId);
		List<EventTOTUser> targets = eventTOTUserRepository.findByServerAndUser(serverId, targetId);
		if(!users.isEmpty() && !targets.isEmpty()) {
			EventTOTUser user = users.get(0);
			EventTOTUser target = targets.get(0);
			return eventTOTHouseRepository.findById(user.getHouse()).map(userHouse -> eventTOTHouseRepository.findById(target.getHouse()).map(targetHouse -> {
				if(trick.equals("home")) {
					user.setCdHome(Timestamp.from(Instant.now().plus(homeDistances[targetHouse.getPosition()][userHouse.getPosition()], ChronoUnit.MINUTES)));
					eventTOTUserRepository.save(user);
					return true;
				} else if(trick.equals("egg")) {
					if(userHouse.getEggs() == 0) return false;
					userHouse.setEggs(userHouse.getEggs()-1);
					userHouse.setEggThrows(userHouse.getEggThrows()+1);
					user.setEggThrows(user.getEggThrows()+1);
					targetHouse.setDirtyState(2);
					
				} else if(trick.equals("roll")) {
					if(userHouse.getToiletPaperRolls() == 0) return false;
					userHouse.setToiletPaperRolls(userHouse.getToiletPaperRolls()-1);
					userHouse.setToiletPaperRollThrows(userHouse.getToiletPaperRollThrows()+1);
					user.setToiletPaperRollThrows(user.getToiletPaperRollThrows()+1);
					targetHouse.setDirtyState(3);
				}
				user.setCdHome(Timestamp.from(Instant.now().plus(homeDistances[targetHouse.getPosition()][userHouse.getPosition()], ChronoUnit.MINUTES)));
				targetHouse.setDirtyEnd(Timestamp.from(Instant.now()));
				eventTOTUserRepository.save(user);
				eventTOTHouseRepository.save(userHouse);
				eventTOTHouseRepository.save(targetHouse);
				return true;
			}).orElse(null)).orElse(null);
		}
		return false;
	}
	
	// 
	
	private static Integer [][] homeDistances = {
	//			0	1	2	3	4	5	6	7	8	9	10	11	12	13	14	15	16	17	18
	/* 0 */	{	0,	30,	30,	30,	30,	30,	30,	30,	30,	30,	30,	30,	30,	30,	30,	30,	30,	30,	30	},
	/* 1 */ {	0,	10,	15,	20,	20,	20,	15,	15,	15,	15,	20,	20,	25,	25,	25,	25,	25,	20,	20	},
	/* 2 */ {	0,	15,	10,	15,	20,	20,	20,	20,	20,	15,	15,	15,	20,	20,	25,	25,	25,	25,	25	},
	/* 3 */ {	0,	20,	15,	10,	15,	20,	20,	25,	25,	20,	20,	15,	15,	15,	20,	20,	25,	25,	25	},
	/* 4 */ {	0,	20,	20,	15,	10,	15,	20,	25,	25,	25,	25,	20,	20,	15,	15,	15,	20,	20,	25	},
	/* 5 */ {	0,	20,	20,	20,	15,	10,	15,	20,	25,	25,	25,	25,	25,	20,	20,	15,	15,	15,	20	},
	/* 6 */ {	0,	15,	20,	20,	20,	15,	10,	15,	20,	20,	25,	25,	25,	25,	25,	20,	20,	15,	15	},
	/* 7 */ {	0,	15,	20,	25,	25,	20,	15,	10,	15,	20,	25,	25,	30,	30,	30,	25,	25,	20,	15	},
	/* 8 */ {	0,	15,	20,	25,	25,	25,	20,	15,	10,	15,	20,	25,	30,	30,	30,	30,	30,	25,	20	},
	/* 9 */ {	0,	15,	15,	20,	25,	25,	20,	20,	15,	10,	15,	20,	25,	25,	30,	30,	30,	25,	25	},
	/* 10 */{	0,	20,	15,	20,	25,	25,	25,	25,	20,	15,	10,	15,	20,	25,	30,	30,	30,	30,	30	},
	/* 11 */{	0,	20,	15,	15,	20,	25,	25,	25,	25,	20,	15,	10,	15,	20,	25,	25,	30,	30,	30	},
	/* 12 */{	0,	25,	20,	15,	20,	25,	25,	30,	30,	25,	20,	15,	10,	15,	20,	25,	30,	30,	30	},
	/* 13 */{	0,	25,	20,	15,	15,	20,	25,	30,	30,	25,	25,	20,	15,	10,	15,	20,	25,	25,	30	},
	/* 14 */{	0,	25,	25,	20,	15,	20,	25,	30,	30,	30,	30,	25,	20,	15,	10,	15,	20,	25,	30	},
	/* 15 */{	0,	25,	25,	20,	15,	15,	20,	25,	30,	30,	30,	25,	25,	20,	15,	10,	15,	20,	25	},
	/* 16 */{	0,	25,	25,	25,	20,	15,	20,	25,	30,	30,	30,	30,	30,	25,	20,	15,	10,	15,	20	},
	/* 17 */{	0,	20,	25,	25,	20,	15,	15,	20,	25,	25,	30,	30,	30,	25,	25,	20,	15,	10,	15	},
	/* 18 */{	0,	20,	25,	25,	25,	20,	15,	15,	20,	25,	30,	30,	30,	30,	30,	25,	20,	15,	10	}
	};

	@Override
	public boolean deleteUser(String serverId, String userId) {
		List<EventTOTUser> users = eventTOTUserRepository.findByServerAndUser(serverId, userId);
		if(!users.isEmpty()) {
			EventTOTUser user = users.get(0);
			return eventTOTHouseRepository.findById(user.getHouse()).map(house -> {
				eventTOTUserRepository.delete(user);
				if(house.getMembers().equals(userId)) 
					eventTOTHouseRepository.delete(house);
				else {
					house.setMembers(house.getMembers().replace(userId+",", "").replace(","+userId, ","));
					eventTOTHouseRepository.save(house);
				}
				return true;
			}).orElse(false);			
		}
		return false;
	}

	@Override
	public boolean deleteHouse(String serverId, String userId) {
		List<EventTOTUser> users = eventTOTUserRepository.findByServerAndUser(serverId, userId);
		if(!users.isEmpty()) {
			EventTOTUser user = users.get(0);
			return eventTOTHouseRepository.findById(user.getHouse()).map(house -> {
				eventTOTUserRepository.deleteAll(eventTOTUserRepository.findByHouse(house.getId()));
				eventTOTHouseRepository.delete(house);
				return true;
			}).orElse(false);			
		}
		return false;
	}

	@Override
	public boolean isSameHouse(String serverId, String userId, String targetId) {
		List<EventTOTUser> users = eventTOTUserRepository.findByServerAndUser(serverId, userId);
		List<EventTOTUser> targets = eventTOTUserRepository.findByServerAndUser(serverId, targetId);
		if(!users.isEmpty() && !targets.isEmpty()) {
			EventTOTUser user = users.get(0);
			EventTOTUser target = targets.get(0);
			return user.getHouse() == target.getHouse();
		}
		return false;
	}

	@Override
	public void swapHouseLocations(String serverId) {
		eventTOTHouseRepository.randomizePositions(serverId);
	}

}
