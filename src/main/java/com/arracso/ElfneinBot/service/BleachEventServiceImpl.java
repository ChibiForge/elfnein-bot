package com.arracso.ElfneinBot.service;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.arracso.ElfneinBot.game.bleach.Hollow;
import com.arracso.ElfneinBot.model.BleachEventUser;
import com.arracso.ElfneinBot.repository.BleachEventUserRepository;

@Service
public class BleachEventServiceImpl implements BleachEventService {
	
	@Autowired
	private BleachEventUserRepository bleachEventUserRepository;

	@Override
	public BleachEventUser getUser(String serverId, String userId) {
		List<BleachEventUser> eventUsers = bleachEventUserRepository.findByServerAndUser(serverId, userId);
		if(eventUsers.isEmpty()) return null;
		return eventUsers.get(0);
	}

	@Override
	public Boolean isUserInEvent(String serverId, String userId) {
		List<BleachEventUser> eventUsers = bleachEventUserRepository.findByServerAndUser(serverId, userId);
		if(eventUsers.isEmpty()) return false;
		return true;
	}

	@Override
	public void registerUser(String serverId, String userId, String fandom, String technique, Integer reiatsu) {
		
		BleachEventUser user = new BleachEventUser();
		user.setServer(serverId);
		user.setUser(userId);
		user.setDivision(fandom);
		user.setReiatsu(reiatsu);
		user.setZanjutsuLv(technique.equals("zanjutsu")?5:3);
		user.setKidoLv(technique.equals("kido")?3:1);
		user.setHakudaLv(technique.equals("hakuda")?3:1);
		user.setHohoLv(technique.equals("hoho")?3:1);
		user.setDemiHollowKills(0);
		user.setHugeHollowKills(0);
		user.setGillianKills(0);
		user.setVastoLordeKills(0);
		user.setAdjuchasKills(0);
		user.setArrancarKills(0);
		user.setInjuries(0);
		user.setMortalInjuries(0);
		user.setHealings(0);
		user.setFullHealings(0);
		user.setScaling(1.0);
		bleachEventUserRepository.save(user);
	}

	@Override
	public Boolean isUserInTrainCd(String guildId, String userId) {
		Timestamp cd = getUser(guildId, userId).getTrainCd();
		if(cd==null) return false;
		return cd.after(Timestamp.from(Instant.now()));
	}

	@Override
	public Timestamp getUserTrainCd(String guildId, String userId) {
		return getUser(guildId, userId).getTrainCd();
	}

	@Override
	public void setTraining(String guildId, String userId, String technique, Map<String, String> metadata) {
		Integer lvZ = Integer.valueOf(metadata.get("lvZ"));
		Integer lvK = Integer.valueOf(metadata.get("lvK"));
		Integer lvH = Integer.valueOf(metadata.get("lvH"));
		Integer lvS = Integer.valueOf(metadata.get("lvS"));
		Integer reiatsu = Integer.valueOf(metadata.get("reiatsu"));
		
		if(technique.equals("zanjutsu")) lvZ++;
		else if(technique.equals("kido")) lvK++;
		else if(technique.equals("hakuda")) lvH++;
		else if(technique.equals("hoho")) lvS++;
		
		BleachEventUser user = getUser(guildId,userId);
		user.setZanjutsuLv(lvZ);
		user.setKidoLv(lvK);
		user.setHakudaLv(lvH);
		user.setHohoLv(lvS);
		user.setReiatsu(reiatsu + 10 + (int)(Math.random() * 11));
		user.setTrainCd(Timestamp.from(Instant.now().plus((int)(30*Math.pow(0.99, user.getHakudaLv()-1)), ChronoUnit.MINUTES)));
		
		bleachEventUserRepository.save(user);
		
	}

	@Override
	public List<BleachEventUser> getUsers(String guildId, String order) {
		if(order.equals("reiatsu")) {
			return bleachEventUserRepository.findByServerOrderByReiatsuDesc(guildId);
		} else if(order.equals("healing")) {
			return bleachEventUserRepository.findByServerOrderByTotalHealingsDesc(guildId);
		}
			
		return bleachEventUserRepository.findByServerOrderByReiatsuDesc(guildId);
	}
	
	@Override
	public Boolean canHollowSpawn(String channelId) {
		if(!lastHollowSpawn.containsKey(channelId)) return true;
		return lastHollowSpawn.get(channelId).before(Timestamp.from(Instant.now()));
	}
	
	//private static String gLink = "https://drive.google.com/uc?export=view&id=";
	
	private static List<Hollow> hollows = List.of(
		new Hollow("Demi-Hollow",null,"https://i.postimg.cc/PxypBrb3/Demi-Hollow-0.png",75,4,1,3,2),
		new Hollow("Demi-Hollow",null,"https://i.postimg.cc/T1jg41JC/Demi-Hollow-1.png",50,1,3,2,0),
		new Hollow("Huge Hollow","Fishbone D",	"https://i.postimg.cc/qBSpWxvF/Huge-Hollow-0.png",150,3,2,0,2),
		new Hollow("Huge Hollow","Hexapodus",	"https://i.postimg.cc/vHnGyx1Z/Huge-Hollow-1.png",200,2,4,1,3),
		new Hollow("Huge Hollow","Acidwire",	"https://i.postimg.cc/13Zs3R6c/Huge-Hollow-2.png",300,2,1,3,4),
		new Hollow("Huge Hollow","Shrieker",	"https://i.postimg.cc/nhYZj8G8/Huge-Hollow-3.png",400,2,1,3,2),
		new Hollow("Huge Hollow","Grand Fisher","https://i.postimg.cc/7LH4SBK5/Huge-Hollow-4.png",600,1,1,4,2),
		new Hollow("Gillian",null,"https://i.postimg.cc/bJnBfdbb/Gillian-0.png",800,4,4,0,1),
		new Hollow("Gillian",null,"https://i.postimg.cc/tJHmqytc/Gillian-1.png",1000,4,4,0,1),
		new Hollow("Gillian","Nakeem Grindina","https://i.postimg.cc/fLf1BQ2Y/Gillian-2.png",1400,3,4,2,2),
		new Hollow("Adjuchas","Di Roy Rinker","https://i.postimg.cc/2Sg4GtZk/Adjuchas-0.png",1400,3,2,1,2),
		new Hollow("Adjuchas","Yylfordt Granz","https://i.postimg.cc/wj1Xhz34/Adjuchas-1.png",1800,2,3,2,2),
		new Hollow("Adjuchas","Edrad Liones","https://i.postimg.cc/GpmcRWbr/Adjuchas-2.png",2200,4,3,2,2),
		new Hollow("Adjuchas","Shawlong Koufang","https://i.postimg.cc/Mpx6Pc4n/Adjuchas-3.png",2500,3,2,4,3),
		new Hollow("Adjuchas","Grimmjow Jaegerjaquez","https://i.postimg.cc/BngqdS9M/Adjuchas-4.png",2800,4,3,2,4),
		new Hollow("Unique Hollow","White","https://i.postimg.cc/wM3jBkBZ/Unique-Hollow-White.png",3000,4,3,3,4),
		new Hollow("Vasto Lorde","Tier Harribel","https://i.postimg.cc/2SDC0pjQ/Vasto-Lorde-0.png",5800,4,2,2,3),
		new Hollow("Vasto Lorde","Baraggan Louisenbairn","https://i.postimg.cc/t42PSBNT/Vasto-Lorde-1.png",6000,3,2,3,2),
		new Hollow("Vasto Lorde","Ulquiorra Cifer","https://i.postimg.cc/VNsmcjC5/Vasto_Lorde_2.png",6000,3,2,3,3),
		new Hollow("Arrancar","Nakeem Grindina","https://i.postimg.cc/SxywXmNv/Arrancar-G0.png",2000,3,4,2,2),
		new Hollow("Arrancar","Di Roy Rinker","https://i.postimg.cc/BQ5rQjsF/Arrancar-G1.png",2200,4,2,2,3),
		new Hollow("Arrancar","Aaroniero Arruruerie","https://i.postimg.cc/6pYFnF85/Arrancar-G2.png",4000,2,2,4,2),
		new Hollow("Arrancar","Findorr Calius","https://i.postimg.cc/CMktmRR2/Arrancar-A0.png",2500,3,2,2,3),
		new Hollow("Arrancar","Abirama Redder","https://i.postimg.cc/jdT19RNS/Arrancar-A1.png",2700,3,3,2,3),
		new Hollow("Arrancar","Charlotte Chuhlhourne","https://i.postimg.cc/0QJ3gQcb/Arrancar-A2.png",2800,2,4,3,2),
		new Hollow("Arrancar","Cirucci Sanderwicci","https://i.postimg.cc/pdS718Qw/Arrancar-A3.png",3000,2,2,4,4),
		new Hollow("Arrancar","Yylfordt Granz","https://i.postimg.cc/GtF52f0R/Arrancar-A4.png",3000,3,3,2,3),
		new Hollow("Arrancar","Shawlong Koufang","https://i.postimg.cc/PJs7djRK/Arrancar-A5.png",3200,3,2,3,3),
		new Hollow("Arrancar","Edrad Liones","https://i.postimg.cc/5NmrWDqq/Arrancar-A6.png",3200,3,3,2,3),
		new Hollow("Arrancar","Gantenbainne Mosqueda","https://i.postimg.cc/BZXd4pQP/Arrancar-A7.png",4500,3,3,2,3),
		new Hollow("Arrancar","Luppi Antenor","https://i.postimg.cc/nVgNzmCR/Arrancar-A8.png",4500,3,2,3,3),
		new Hollow("Arrancar","Dordoni Alessandro Del Socaccio","https://i.postimg.cc/3JGVDwGK/Arrancar-A9.png",4800,2,3,3,3),
		new Hollow("Arrancar","Szayelaporro Granz","https://i.postimg.cc/mZSfdkD8/Arrancar-A10.png",5000,3,2,3,3,
			new Hollow("Resurreccion","Fornicarás","https://i.postimg.cc/FHqbQB3W/Arrancar-A10-R1.png",6000,3,2,3,3)),
		new Hollow("Arrancar","Zommari Rureaux","https://i.postimg.cc/Dw89yccM/Arrancar-A11.png",5000,2,3,4,3,
			new Hollow("Resurreccion","Brujería","https://i.postimg.cc/dQZxbx3L/Arrancar-A11-R1.png",6200,2,3,4,3)),
		new Hollow("Arrancar","Grimmjow Jaegerjaquez","https://i.postimg.cc/kX1rmHWS/Arrancar-A12.png",5500,4,3,2,4,
			new Hollow("Resurreccion","Pantera","https://i.postimg.cc/PrW1BGZX/Arrancar-A12-R1.png",7000,4,3,2,4)),
		new Hollow("Arrancar","Nnoitra Gilga","https://i.postimg.cc/DyxdxZZK/Arrancar-A13.png",6000,4,2,3,3,
			new Hollow("Resurreccion","Santa Teresa","https://i.postimg.cc/SxjL3h2Y/Arrancar-A13-R1.png",7500,4,2,3,3)),
		new Hollow("Arrancar","Yammy Llargo","https://i.postimg.cc/pd5TfC03/Arrancar-A14.png",5000,3,3,2,2,
			new Hollow("Resurreccion","Ira","https://i.postimg.cc/1txzJF2X/Arrancar-A14-R1.png",10000,4,4,2,1)),
		new Hollow("Arrancar","Ulquiorra Cifer","https://i.postimg.cc/L6kjf9qj/Arrancar-V0.png",7000,3,2,3,3,
			new Hollow("Resurreccion","Murcielago","https://i.postimg.cc/kXPxB65n/Arrancar-V0-R1.png",9000,3,2,3,4,
			new Hollow("Resurreccion","Murcielago — Segunda Etapa","https://i.postimg.cc/vZQftJ9k/Arrancar-V0-R2.png",12000,4,3,3,4))),
		new Hollow("Arrancar","Tier Harribel","https://i.postimg.cc/ZYd8bF47/Arrancar-V1.png",7000,4,2,2,3,
			new Hollow("Resurreccion","Tiburón","https://i.postimg.cc/26Pnd3F7/Arrancar-V1-R1.png",9000,4,3,2,3)),
		new Hollow("Arrancar","Baraggan Louisenbairn","https://i.postimg.cc/NFmmdYdq/Arrancar-V2.png",7500,3,3,4,2,
			new Hollow("Resurreccion","Arrogante","https://i.postimg.cc/gJwv63R0/Arrancar-V2-R1.png.png",9500,3,4,4,2)),
		new Hollow("Arrancar","Coyote Starrk","https://i.postimg.cc/Bb7GbLW4/Arrancar-V3.png",8000,3,2,4,3,
			new Hollow("Resurreccion","Los Lobos","https://i.postimg.cc/66MKYS8g/Arrancar-V3-R1.png",11000,4,2,4,3)),
		new Hollow("Not Hollow","Aizen","https://i.postimg.cc/76YwmcTL/Aizen-0.png",9000,2,2,4,3,
			new Hollow("Evolution","Hōgyoku Fusion","https://i.postimg.cc/nL1nKZr5/Aizen-1.png",10000,2,3,4,3,
			new Hollow("Evolution","Chrysalis","https://i.postimg.cc/xCxY7RK3/Aizen-2.png",13000,2,4,4,3,
			new Hollow("Evolution","Ascended","https://i.postimg.cc/tgbXV7qW/Aizen-3.png",15000,3,3,4,4,
			new Hollow("Evolution","Butterfly","https://i.postimg.cc/T1KGXD0M/Aizen-4.png",18000,4,4,4,4,
			new Hollow("Evolution","Monster","https://i.postimg.cc/3Nzhrxfy/Aizen-5.png",20000,4,4,2,4/*,
			new Hollow("Evolution","Chair-sama","https://i.postimg.cc/hvvbxz7r/Aizen-6.png",-1,2,2,2,2)*/))))))//,
		//new Hollow("Not Hollow","Chair-sama","https://i.postimg.cc/hvvbxz7r/Aizen-6.png",-1,2,2,2,2)
	);
	
	@Override
	public Hollow getRandomHollow(String channelId) {
		lastHollowSpawn.put(channelId, Timestamp.from(Instant.now().plus(5+(int)(Math.random() * 6), ChronoUnit.MINUTES)));
		
		cleanFighters(channelId);
		
		Integer selection = null;
		Double random = Math.random();
		
		if(List.of("1395792907533025360").contains(channelId)) { // Test
			selection = hollows.size()-1; 
			selection = 2;
		} else if(List.of("1348063240957067386").contains(channelId)) { // LS
			lastHollowSpawn.put(channelId, Timestamp.from(Instant.now().plus(5+(int)(Math.random() * 6), ChronoUnit.MINUTES)));
			selection = 16+(int)(Math.pow(Math.random(),0.8) * (hollows.size()-16));
		} else if(List.of("1337822765641760890").contains(channelId)) { // LS - Easy
			lastHollowSpawn.put(channelId, Timestamp.from(Instant.now().plus(5+(int)(Math.random() * 6), ChronoUnit.MINUTES)));
			selection = (int)(Math.random() * 16);
		} else if(List.of("1194282171146453083").contains(channelId)) { // Super Easy
			selection = (int)(Math.pow(Math.random(),2) * 8);
		} else if(List.of("1252713833420685322").contains(channelId)) { // Easy
			selection = 8 + (int)(Math.random() * 8);
		} else {
			if(random < 0.05) selection = 15 + (int)(Math.sqrt(Math.random()) * 4);
			else if(random < 0.1) selection = 15 + 4 + (int)(Math.random() * 10);
			else if(random > 0.95) selection = hollows.size() - 1;
			else selection = 15 + 4 + 10 + (int)(Math.sqrt(Math.random()) * (hollows.size()-15-4-10-1));
		}
		
		Hollow hollow = new Hollow(hollows.get(selection));
		Hollow hollowAux = hollow;
		while(hollowAux != null) {
			hollowAux.setReiatsu(hollowAux.getReiatsu() + (int)(Math.random() * hollowAux.getReiatsu()*0.1));
			hollowAux = hollowAux.getResurreccion();
		}
		
		return hollow;
	}

	private Boolean cleanFighters(String channelId) {
		Boolean hadToRemove = false;
		List<String> inBattle = shinigamisInBattle.keySet().stream().filter(k -> k.contains(":" + channelId + ":")).toList();
		for(String guildUser:inBattle) {
			hadToRemove = true;
			shinigamisInBattle.remove(guildUser);
		}
		return hadToRemove;
	}

	@Override
	public Boolean isUserInjured(String guildId, String userId) {
		Timestamp cd = getUser(guildId, userId).getInjureCd();
		if(cd==null) return false;
		return cd.after(Timestamp.from(Instant.now()));
	}

	@Override
	public Timestamp getUserInjureCd(String guildId, String userId) {
		return getUser(guildId, userId).getInjureCd();
	}

	@Override
	public Boolean registerShinigamiIntoBattle(String guildId, String channelId, String userId, String technique) {
		if(shinigamisInBattle.containsKey(guildId + ":" + channelId + ":" + userId)) return false;
		shinigamisInBattle.put(guildId + ":" + channelId + ":" + userId, technique);
		return true;
	}

	@Override
	public Map<String, List<String>> performBattle(String guildId, String channelId, Hollow hollow) {
		// Get user
		List<String> inBattle = shinigamisInBattle.keySet().stream()
			.filter(k -> k.startsWith(guildId + ":" + channelId + ":")).toList();
		
		// Scales
		Double [] scale = {0.5, 0.75,1.0,1.5,2.0};		
		Integer nShinigamis = inBattle.size();
		Double powerScale = 1.01;
		Double powerDescale = 0.95;
		
		// Hollow Stats
		Integer hollowPower = (int) (hollow.getReiatsu()!=-1? hollow.getReiatsu() * Math.pow(powerDescale,nShinigamis-1):60000);
		Integer ferocity = (int) (hollowPower * scale[hollow.getFerocity()]);
		Integer instinct = (int) (hollowPower * scale[hollow.getInstinct()]);
		Integer resilience = (int) (hollowPower * scale[hollow.getResilience()]);
		Integer agility = (int) (hollowPower * scale[hollow.getAgility()]);
		
		// Interactions - REDO if changing scales
		Integer pwrVsZan = (int) Math.max(resilience, instinct*2);
		Integer pwrVsHak = (int) Math.max(ferocity, resilience*2);
		Integer pwrVsKid = (int) Math.max(instinct, ferocity*2);
		
		// Resurreccion
		boolean isRes = false;
		boolean isEnd = false;
		if(hollow.getResurreccion()!=null) isRes =  ((int)(Math.random() * 20)) != 0;
		else if(inBattle.size()==1 && hollow.getReiatsu()>3000) {
			isEnd = ((int)(Math.random() * 10000)) == 0;
			isRes = isEnd;
		}
				
		// Get battle outcomes
		boolean isWin = false;
		boolean isAllDead = true;
		List<String> uZan = new ArrayList<>();
		List<String> uKid = new ArrayList<>();
		List<String> uHak = new ArrayList<>();
		List<String> uInj = new ArrayList<>();
		List<String> uDie = new ArrayList<>();
		List<String> uSlo = new ArrayList<>();
		List<String> uSpecial = new ArrayList<>();
		for(String guildUser:inBattle) {
			String technique = shinigamisInBattle.get(guildUser);
			String userId = guildUser.split(":")[2];
			shinigamisInBattle.remove(guildUser);
			
			BleachEventUser user = getUser(guildId, userId);
			// Check Speed
			String special = "";
			Boolean shunpo = false;
			Integer uSpd = (int) (user.getReiatsu() * Math.pow(powerScale, user.getHohoLv()-1));
			if(user.getHohoLv()==100 && uSpd < agility) {
				uSpd = (int)(uSpd * 1.5);
				shunpo = true;
				special = "*<@" + userId + "> disappears from sight, steps echoing with Shunpo.*";
			}
			// Check power
			Integer uPwr = user.getReiatsu();
			Integer hPwr = 0;
			//Add user technique
			if(technique.equals("zanjutsu")) {
				uZan.add(userId);
				uPwr = (int) (uPwr * Math.pow(powerScale, user.getZanjutsuLv()-1));
				hPwr = pwrVsZan;
			} else if(technique.equals("kido")) {
				uKid.add(userId);
				uPwr = (int) (uPwr * Math.pow(powerScale, user.getKidoLv()-1));
				hPwr = pwrVsKid;
			} else {
				uHak.add(userId);
				uPwr = (int) (uPwr * Math.pow(powerScale, user.getHakudaLv()-1));
				hPwr = pwrVsHak;
			}
			// Correct
			if(uPwr<hPwr) {
				if(technique.equals("zanjutsu")) {
					Integer uPwrShikai = (int)(uPwr * 1.25);
					if(user.getBankai()!=null && uPwrShikai<hPwr) {
						uPwr = (int)(uPwr * 1.5);
						special = "*<@" + userId + "> whispers... **BANKAI — "+ user.getBankai() + "***";
						if(shunpo) special = "*<@" + userId + "> whispers... **BANKAI — "+ user.getBankai() + "** as they flash forward with Shunpo.*"; 
					} else if(user.getShikai()!=null) {
						uPwr = uPwrShikai;
						special = "*<@" + userId + "> unleashes their Shikai: **" + user.getShikai() + "**.*";
						if(shunpo) special = "*<@" + userId + "> unleashes their Shikai: **" + user.getShikai() + "**, then blurs forward with Shunpo.*";
					}
				} else if(technique.equals("kido") && user.getKidoLv()==100) {
					uPwr = (int)(uPwr * 1.5);
					special = "*<@" + userId + "> chants: **Hadō #99 — Goryūtenmetsu!***";
					if(shunpo) special = "*<@" + userId + "> chants: **Hadō #99 — Goryūtenmetsu!**, weaving the incantation mid-Shunpo.*";
				} else if(technique.equals("hakuda") && user.getHakudaLv()==100){
					uPwr = (int)(uPwr * 1.5);
					special = "*<@" + userId + "> envelops their body in Shunkō.*";
					if(shunpo) special = "*<@" + userId + "> ignites Shunkō, their form vanishing instantly with Shunpo.*";
				}
			}
			if(!special.isBlank()) uSpecial.add(special);
			// Result
			Boolean uWin = false;
			Boolean uInjured = false;
			Boolean uDied = false;
			if(uSpd>=agility && uPwr>=hPwr) {
				uWin = true;
			}else if(uSpd>=agility && uPwr<hPwr){
				uInjured = true;
				uInj.add(userId);
			}else if(uSpd<agility && uPwr<hPwr) {
				uDied = true;
				uDie.add(userId);
			}else {
				uSlo.add(userId);
			}
			try {
				if(uDied){
					user.setMortalInjuries(user.getMortalInjuries()+1);
					Integer injureLv = hollow.getReiatsu()/100;
					Integer minutes = (int)(120 * Math.pow(1.01, injureLv));
					if(hollow.getReiatsu()==-1) minutes = 4*24*60;
					user.setInjureCd(Timestamp.from(Instant.now().plus(minutes, ChronoUnit.MINUTES)));
				}else if(uInjured){
					user.setInjuries(user.getInjuries()+1);
					Integer injureLv = hollow.getReiatsu()/100;
					Integer minutes = (int)(30 * Math.pow(1.01, injureLv));
					if(hollow.getReiatsu()==-1) minutes = 1*24*60;
					user.setInjureCd(Timestamp.from(Instant.now().plus(minutes, ChronoUnit.MINUTES)));
				}else if(isRes) {
					isAllDead = false;
					if(uWin) user.setReiatsu(user.getReiatsu()+(int)(hollow.getReiatsu()*25*user.getScaling())/user.getReiatsu());
					registerShinigamiIntoBattle(guildId, channelId, userId, technique);
				}else if(uWin) {
					isWin = true;
					user.setReiatsu(user.getReiatsu()+(int)(hollow.getReiatsu()*25*user.getScaling()/user.getReiatsu()));
					user.increaseKills(hollow.getTpye());
				}
			}catch(Exception e) {
				System.out.println("Error while updating user " + user.getUser() + " with scaling " + user.getScaling());
			}
			bleachEventUserRepository.save(user);
		}
		
		if(isAllDead) {
			isRes = false;
			isEnd = false;
		}
		
		Map<String,List<String>> ret = Map.of(
			"uZan", uZan,
			"uKid", uKid,
			"uHak", uHak,
			"uInj", uInj,
			"uDie", uDie,
			"uSlo", uSlo,
			"uSpecial", uSpecial,
			"info", List.of(isEnd?"END":(isRes?"RES":(isWin?"WIN":"LOSE")), ""+nShinigamis)
		);
		
		return ret;
	}
	
	private static Map<String,Timestamp> lastHollowSpawn = new HashMap<>();
	private static Map<String,String> shinigamisInBattle = new HashMap<>();

	@Override
	public Boolean nameZanpakuto(String guildId, String userId, String ascend, String name) {
		List<BleachEventUser> users = bleachEventUserRepository.findByServerAndUser(guildId, userId);
		if(users.isEmpty()) return false;
		
		BleachEventUser user = users.get(0);
		
		if(ascend.equals("shikai")) {
			if(bleachEventUserRepository.findByShikai(name).isEmpty()) {
				user.setShikai(name);
				bleachEventUserRepository.save(user);
				return true;
			}
		} else {
			if(bleachEventUserRepository.findByBankai(name).isEmpty()) {
				user.setBankai(name);
				bleachEventUserRepository.save(user);
				return true;
			}
		}
		return false;
	}

	@Override
	public Boolean isUserInHealCd(String guildId, String userId) {
		BleachEventUser user = getUser(guildId,userId);
		if(user.getHealCd()==null) return false;
		return user.getHealCd().after(Timestamp.from(Instant.now()));
	}
	
	@Override
	public Timestamp getUserHealCd(String guildId, String userId) {
		BleachEventUser user = getUser(guildId,userId);
		return user.getHealCd();
	}	

	@Override
	public Pair<Timestamp, Integer> healShinigami(String guildId, String userId, String targetId) {
		BleachEventUser healer = getUser(guildId,userId);
		BleachEventUser target = getUser(guildId,targetId);
		
		if(healer.getKidoLv() < 5) return null;
		
		Integer healingRange = (int)(44 + 16.3 * Math.pow(1.04, healer.getKidoLv()-5));
		Integer healingDone  = (int)(10 + 5.55 * Math.pow(1.04, healer.getKidoLv()-5));
		Boolean reward = true;
		
		if(target.getInjureCd().toInstant().minus(healingRange, ChronoUnit.MINUTES).isAfter(Instant.now())) { // Check range
			healingDone = (int) (Math.sqrt(healingDone/4.0)*4);
			reward = false;
		}
		
		Timestamp newCd = Timestamp.from(target.getInjureCd().toInstant().minus(healingDone, ChronoUnit.MINUTES)); // Update
		target.setInjureCd(newCd);
		bleachEventUserRepository.save(target);
		if(reward) healer.setReiatsu(healer.getReiatsu() + 1 + 450/(healer.getHealings()+healer.getFullHealings()+50));
		else healer.setReiatsu(healer.getReiatsu() + 1);
		healer.setHealCd(Timestamp.from(Instant.now().plus((int)(20+40*Math.pow(0.96, healer.getKidoLv()-5)), ChronoUnit.MINUTES)));
		if(newCd.after(Timestamp.from(Instant.now()))) healer.setHealings(healer.getHealings()+1);
		else healer.setFullHealings(healer.getFullHealings()+1);
		bleachEventUserRepository.save(healer);
		Pair<Timestamp,Integer> healing = Pair.of(newCd, healingDone);
		return healing;
	}

	@Override
	public void forceHeal(String guildId, String userId) {
		BleachEventUser user = getUser(guildId,userId);
		user.setInjureCd(null);
		bleachEventUserRepository.save(user);
	}

	@Override
	public Boolean isChannelNotAllowed(String channelId) {
		List<String> channels = List.of(
			"1326619189468729466","1265469543149600881","1194278207684358154",
			"1350819844949413938","1249815063926276147","1351695940435841024",
			"1360786736200028231","1264356548436824126","1264356649993502861",
			"1303458366688985108");
		return channels.contains(channelId);
	}

	@Override
	public String updateUser(String type, String guildId, String targetId, String stat, String value) {
		try {
			BleachEventUser target = getUser(guildId,targetId);
			if(stat.equals("bankai")) {
				target.setBankai(value);
			} else if(stat.equals("shikai")) {
				target.setShikai(value);
			} else if(stat.equals("scaling")) {
				Double valueAux = Double.parseDouble(value);
				target.setScaling(valueAux);
			} else {
				Integer valueAux = Integer.parseInt(value);
				if(stat.equals("reiatsu")) {
					target.setReiatsu(valueAux);
				} else if(stat.equals("zanjutsu")) {
					target.setZanjutsuLv(valueAux);
				} else if(stat.equals("kido")) {
					target.setKidoLv(valueAux);
				} else if(stat.equals("hakuda")) {
					target.setHakudaLv(valueAux);
				} else if(stat.equals("hoho")) {
					target.setHohoLv(valueAux);
				} else {
					return "Invalid stat. Valid stats are: scaling, reiatsu, bankai, shikai, zanjutsu, kido, hakuda, hoho";
				}
			}
			bleachEventUserRepository.save(target);
			return "Stat updated.";
		} catch(Exception e) {
			return "Invalid value.";
		}
	}


	
}
