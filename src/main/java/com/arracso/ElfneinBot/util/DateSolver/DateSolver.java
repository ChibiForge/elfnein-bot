package com.arracso.ElfneinBot.util.DateSolver;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import com.arracso.ElfneinBot.model.DateSolution;
import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Position;
import com.arracso.ElfneinBot.util.Service;

import discord4j.core.object.component.ActionRow;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class DateSolver {	
	
	public static Solution solveFast(char[][] map, Position carPos, Character carDir, DateStats stats, Map<Position,Integer> timers) {
		Set<Long> states = new HashSet<Long>();
		return solve3(0,states,null,null,map, carPos, carDir, stats, timers);
	}
	
	private static Solution solve3(int lv, Set<Long> states, Set<Long> states2, Set<Long> states3, char[][] map, Position carPos, Character carDir, DateStats stats, Map<Position,Integer> timers){
		Integer lvLim = 11;
		Integer lvP1 = 4;
		Integer lvP2 = 7;
		
		// Check state
		Long state = getStateHash(lv,carPos,carDir,stats,timers);
		if(states.contains(state)) return null;
		states.add(state);
		
		if(lv>=lvP1 && states2 == null) states2 = new HashSet<Long>();
		if(states2 != null) states2.add(getStateHash(lv-lvP1,carPos,carDir,stats,timers));
		
		if(lv>=lvP2 && states3 == null) states3 = new HashSet<Long>();
		if(states3 != null) states3.add(getStateHash(lv-lvP2,carPos,carDir,stats,timers));
		
		Solution sol = null;
		List<Candidate> candidates = getCandidates(carPos,carDir,map,timers);
		for(Candidate candidate:candidates) {
			DateStats newStats = stats.add(candidate.getStats());
			Solution newSol = null;
			if(newStats.isSolution()) {
				newSol = new Solution(newStats);
			}else if(!newStats.isOver()) {
				Map<Position,Integer> newTimers = updateTimers(timers,candidate);
				if(lv < lvLim) newSol = solve3(lv+1,states,states2,states3,map,candidate.getCarPos(carPos),candidate.getCarDir(carPos,carDir),newStats,newTimers);
				else newSol = solve3(lv-lvP1+1,states2,states3,null,map,candidate.getCarPos(carPos),candidate.getCarDir(carPos,carDir),newStats,newTimers);
			}
			
			if(newSol!=null) {
				if(candidate.action=='.') 
					newSol.push(candidate.getCarDir(carPos,carDir));
				else if(candidate.action=='E')
					newSol.push(candidate.relativePosition);
				else 
					newSol.push(candidate.action);
				if(sol == null) 
					sol = newSol;
				else if(sol.getPuntuation() < newSol.getPuntuation())
					sol = newSol;
			}
		}
		
		return sol;
	}
	
	public static Solution solve(char[][] map, Position carPos, Character carDir, DateStats stats, Map<Position,Integer> timers){
		Solution sol = null;
		List<Candidate> candidates = getCandidates(carPos,carDir,map,timers);
		for(Candidate candidate:candidates) {
			DateStats newStats = stats.add(candidate.getStats());
			Solution newSol = null;
			if(newStats.isSolution()) {
				newSol = new Solution(newStats);
			}else if(!newStats.isOver()) {
				Map<Position,Integer> newTimers = updateTimers(timers,candidate);
				newSol = solve(map,candidate.getCarPos(carPos),candidate.getCarDir(carPos,carDir),newStats,newTimers);
			}
			
			if(newSol!=null) {
				if(candidate.action=='.') 
					newSol.push(candidate.getCarDir(carPos,carDir));
				else if(candidate.action=='E')
					newSol.push(candidate.relativePosition);
				else
					newSol.push(candidate.action);
				if(sol == null) 
					sol = newSol;
				else if(sol.getPuntuation() < newSol.getPuntuation())
					sol = newSol;
			}
		}
		
		return sol;
	}
	
	public static DateStats readStats(BufferedImage image) {
		DateStats stats = new DateStats();
		// TODO
		return stats;
	}
	
	public static char[][] readMap(BufferedImage image) {
		// Get event
		String event = getEvent(image); ;
		// Get colors from image
		int[] roadColors = getColorPositions(image,"road",event);
		int[] nonRoadColors = getColorPositions(image,"nonRoad",event);
		// Get map from image
		Position[][] mapPositions = getMapPositions(event);
		char[][] map = new char[15][11];
		for(int i=0; i<mapPositions.length;i++) {
			for(int j=0; j<mapPositions[0].length;j++) {
				Position pos = mapPositions[i][j];
				Character emoji = null;
				if(i%2==1 && j%2 ==1) {
					if(pos != null) {
						// Get emoji
						Iterator<EmojiPattern> it = emojiPatterns.iterator();
						while(emoji == null && it.hasNext()) {
							EmojiPattern entry = it.next();
							if(entry.checkArea(image,pos,30)) {
								emoji = entry.getName();
							}
						}
						if(emoji == null) emoji = 'X';
					}
				}else if(i%2==0 && j%2==0) {
					emoji = '.';
				}else {
					// Check if road or not
					if(pos != null && !checkIfRoad(image.getRGB(pos.x, pos.y),roadColors,nonRoadColors)) emoji = 'G';
					else emoji = '.';
				}
				map[i][j] = emoji;
			}
		}
		
		return map;
	}
	
	
	public static EmbedCreateSpec getSolutionEmbed(Solution sol) {
		EmbedCreateSpec embed = EmbedCreateSpec.builder()
				.color(Color.SEA_GREEN)
				.title("Date Solution")
				.description(sol.getActions()+ "\n`" + sol.getRealPoints() + "`\n" +
						//"**WARNING**: During events map might not be read properly. You can check if map is being read properly by doing e.map on the date. If the map is not read correctly please tell support to fix it.\n" +
						"_[Support server](https://discord.gg/2Ke6E4jQrg)_")
				.build();
		return embed;
	}
	
	@SuppressWarnings("deprecation")
	public static Mono<? extends Void> executeSolve(Message message, Message messageRes) {
		try {			
			// Get direction
			char carDir = '>';
			if(((ActionRow) message.getComponents().get(1)).getChildren().get(0).getData().disabled().isAbsent()) {
				carDir = '<';
			}
			
			// Check map
			URL url = URI.create(message.getEmbeds().get(0).getImage().get().getUrl()).toURL();
		    BufferedImage image = ImageIO.read(url);
		    char[][] map = DateSolver.readMap(image);
		    
		    // Check stats
		    DateStats dateStats = DateSolver.readStats(image);
		    
		    // Solution
		    Solution sol = DateSolver.solveFast(map,new Position(5,14),carDir,dateStats,new HashMap<Position,Integer>());
			if(sol != null) {
				DateSolver.saveSolution(message, sol);
				return messageRes.edit(MessageEditSpec.builder().content("").addEmbed(DateSolver.getSolutionEmbed(sol)).build()).then();
			}
			else return messageRes.edit(MessageEditSpec.builder().content("Impossible Board :(").build()).then();
		}catch(Exception e) {
			System.out.println("ERROR on DATE SOLVER");
			e.printStackTrace();
		}
		return messageRes.edit(MessageEditSpec.builder().content("Something went wrong. Tell <@278957461120090113> to fix me.").build()).then();
	}
	
	public static void saveSolution(Message message, Solution solution) {
		// Get user id
		String userId = message.getEmbeds().get(0).getDescription().get().split("@")[1].split(">")[0].strip(); // TODO
		String u = "UN";
		if(userId.length()>1) u = userId.substring(userId.length()-2, userId.length());
		// Get Card Code
		String cardCode = message.getEmbeds().get(0).getDescription().get().split("`")[1].strip();
		
		DateSolution dateSolution = new DateSolution(u+cardCode,solution.stats,solution.actions,new Timestamp(System.currentTimeMillis()));
		
		Service.karutaService.saveDateSolution(dateSolution);
	}
	
	public static Instant getUptime(Message message) {
		// Get user id
		String userId = message.getEmbeds().get(0).getDescription().get().split("@")[1].split(">")[0].strip();
		String u = "UN";
		if(userId.length()>1) u = userId.substring(userId.length()-2, userId.length());
		
		// Get Card Code
		String cardCode = message.getEmbeds().get(0).getDescription().get().split("Character")[1].split("`")[1].strip();
		
		return Service.karutaService.getDateSolveUptime(u+cardCode);
	}
	
	public static Solution getSavedSolution(Message message) {
		// Get user id
		String userId = message.getEmbeds().get(0).getDescription().get().split("@")[1].split(">")[0].strip();
		String u = "UN";
		if(userId.length()>1) u = userId.substring(userId.length()-2, userId.length());
		
		// Get Card Code
		String cardCode = message.getEmbeds().get(0).getDescription().get().split("Character")[1].split("`")[1].strip();
		
		DateSolution dateSolution = Service.karutaService.getSavedDateSolution(u+cardCode);
		
		Instant sevenHoursAgo = Instant.now().minus(7, ChronoUnit.HOURS);
		if(dateSolution == null || dateSolution.getUpdated() == null || !dateSolution.getUpdated().toInstant().isAfter(sevenHoursAgo)) return null;
		
		Solution solution = new Solution(dateSolution.getStats());
		solution.actions = dateSolution.getActionsList();
		
		return solution;
	}
	
	//////////////////////
	// Helper functions //
	//////////////////////
	
	private static String getEvent(BufferedImage image) {
		if(getColorDist(image.getRGB(75, 590),-2970938) <= 5) return "spring";
		else if(getColorDist(image.getRGB(75, 590),-9051) <= 5) return "summer";
		else if(getColorDist(image.getRGB(75, 590),-6362885) <= 5) return "winter";
		else if(getColorDist(image.getRGB(75, 590),-1843239) <= 5) return "valentine";
		
		return "none";
	}
	
	private static int[] getColorPositions(BufferedImage image, String type, String event) {
		int[] colorPositions;
		
		if(event.equals("spring")) {
			if(type.equals("road")) {
				colorPositions = new int[5];
				colorPositions[0] = image.getRGB(80,578);
				colorPositions[1] =  image.getRGB(89,578);
				colorPositions[2] =  image.getRGB(80,581);
				colorPositions[3] =  image.getRGB(81,580);
				colorPositions[4] =  image.getRGB(89,581);
			} else {
				colorPositions = new int[7];
				colorPositions[0] = image.getRGB(135,557);
				colorPositions[1] =  image.getRGB(141,557);
				colorPositions[2] =  image.getRGB(143,557);
				colorPositions[3] =  image.getRGB(160,557);
				colorPositions[4] =  image.getRGB(165,557);
				colorPositions[5] =  image.getRGB(158,557);
				colorPositions[6] =  image.getRGB(172,557);
			}
		} else if(event.equals("summer")) {
			if(type.equals("road")) {
				colorPositions = new int[5];
				colorPositions[0] = image.getRGB(80,578);
				colorPositions[1] =  image.getRGB(89,578);
				colorPositions[2] =  image.getRGB(80,581);
				colorPositions[3] =  image.getRGB(81,580);
				colorPositions[4] =  image.getRGB(89,581);
			} else {
				colorPositions = new int[6];
				colorPositions[0] = image.getRGB(135,557);
				colorPositions[1] =  image.getRGB(141,557);
				colorPositions[2] =  image.getRGB(143,557);
				colorPositions[3] =  image.getRGB(160,557);
				colorPositions[4] =  image.getRGB(165,557);
				colorPositions[5] =  image.getRGB(158,557);
			}
		} else if(event.equals("winter")) {
			if(type.equals("road")) {
				colorPositions = new int[7];
				colorPositions[0] = image.getRGB(80,593);
				colorPositions[1] =  image.getRGB(83,591);
				colorPositions[2] =  image.getRGB(85,591);
				colorPositions[3] =  image.getRGB(74,591);
				colorPositions[4] =  image.getRGB(75,591);
				colorPositions[5] =  image.getRGB(76,591);
				colorPositions[6] =  image.getRGB(81,592);
			}else {
				colorPositions = new int[8];
				colorPositions[0] = image.getRGB(109,568);
				colorPositions[1] =  image.getRGB(109,566);
				colorPositions[2] =  image.getRGB(111,564);
				colorPositions[3] =  image.getRGB(112,568);
				colorPositions[4] =  image.getRGB(112,566);
				colorPositions[5] =  image.getRGB(112,564);
				colorPositions[6] =  image.getRGB(109,569);
				colorPositions[7] =  image.getRGB(128,558);
			}
		}else if(event.equals("valentine")){
			if(type.equals("road")) {
				colorPositions = new int[6];
				colorPositions[0] = image.getRGB(87,590);
				colorPositions[1] = image.getRGB(83,590);
				colorPositions[2] = image.getRGB(89,590);
				colorPositions[3] = image.getRGB(87,589);
				colorPositions[4] = image.getRGB(89,589);
				colorPositions[5] = image.getRGB(83,589);
			}else {
				colorPositions = new int[6];
				colorPositions[0] = image.getRGB(130,562);
				colorPositions[1] =  image.getRGB(130,563);
				colorPositions[2] =  image.getRGB(130,564);
				colorPositions[3] =  image.getRGB(125,554);
				colorPositions[4] =  image.getRGB(125,555);
				colorPositions[5] =  image.getRGB(125,556);
				colorPositions[5] =  image.getRGB(125,562);
			}
		}else{
			if(type.equals("road")) {
				colorPositions = new int[4];
				colorPositions[0] = image.getRGB(87,590);
				colorPositions[1] =  image.getRGB(88,590);
				colorPositions[2] =  image.getRGB(89,590);
				colorPositions[3] =  image.getRGB(90,590);
			}else {
				colorPositions = new int[6];
				colorPositions[0] = image.getRGB(120,557);
				colorPositions[1] =  image.getRGB(120,554);
				colorPositions[2] =  image.getRGB(120,559);
				colorPositions[3] =  image.getRGB(118,559);
				colorPositions[4] =  image.getRGB(128,560);
				colorPositions[5] =  image.getRGB(124,557);
			}
		}
		
		return colorPositions;	
	}
	
	private static Position[][] getMapPositions(String event) {
		Position[][] mapPositions;
		
		if(event.equals("spring")) {
			Position[][] mapPositionsAux = {
				{null,null,null,null,null,null,null,null,null,null,null},
				{null,new Position(262,150),new Position(314,174),new Position(320,150),new Position(373,174),new Position(382,150),new Position(426,174),new Position(442,150),new Position(485,174),new Position(504,150),null},
				{null,new Position(294,197),null,new Position(357,197),null,new Position(378,197),null,new Position(441,197),null,new Position(503,197),null},
				{null,new Position(250,184),new Position(307,204),new Position(316,184),new Position(371,204),new Position(382,184),new Position(433,204),new Position(448,184),new Position(497,204),new Position(515,184),null},
				{null,new Position(285,232),null,new Position(353,232),null,new Position(377,232),null,new Position(445,232),null,new Position(514,232),null},
				{null,new Position(238,225),new Position(298,240),new Position(309,225),new Position(367,240),new Position(382,225),new Position(432,240),new Position(456,225),new Position(501,240),new Position(526,225),null},
				{null,new Position(273,273),null,new Position(348,273),null,new Position(374,273),null,new Position(449,273),null,new Position(525,273),null},
				{null,new Position(223,272),new Position(279,284),new Position(303,272),new Position(356,284),new Position(382,272),new Position(443,284),new Position(462,272),new Position(520,284),new Position(541,272),null},
				{null,new Position(258,325),null,new Position(342,325),null,new Position(370,325),null,new Position(456,325),null,new Position(539,325),null},
				{null,new Position(203,333),new Position(265,336),new Position(291,333),new Position(351,336),new Position(382,333),new Position(448,336),new Position(471,333),new Position(534,336),new Position(561,333),null},
				{null,new Position(243,387),null,new Position(336,387),null,new Position(368,387),null,new Position(463,387),null,new Position(555,387),null},
				{null,new Position(181,407),new Position(257,403),new Position(281,407),new Position(355,403),new Position(382,407),new Position(451,403),new Position(482,407),new Position(548,403),new Position(583,407),null},
				{null,new Position(219,469),null,new Position(328,470),null,new Position(364,469),null,new Position(471,469),null,new Position(578,469),null},
				{null,new Position(147,505),new Position(225,490),new Position(265,505),new Position(336,490),new Position(382,505),new Position(464,490),new Position(500,505),new Position(575,490),new Position(619,505),null},
				{null,null,null,null,null,null,null,null,null,null,null}
			};
			mapPositions = mapPositionsAux;
		}else if(event.equals("valentine")) {
			Position[][] mapPositionsAux = {
				{null,null,null,null,null,null,null,null,null,null,null},
				{null,new Position(262,150),new Position(306,179),new Position(320,150),new Position(366,179),new Position(382,150),new Position(426,179),new Position(442,150),new Position(485,179),new Position(504,150),null}, // FET
				{null,new Position(290,197),null,new Position(355,197),null,new Position(378,197),null,new Position(441,197),null,new Position(503,197),null}, // 
				{null,new Position(250,184),new Position(306,206),new Position(316,184),new Position(371,206),new Position(382,184),new Position(428,205),new Position(448,184),new Position(492,205),new Position(515,184),null},
				{null,new Position(281,232),null,new Position(309,232),null,new Position(377,232),null,new Position(445,232),null,new Position(514,232),null},
				{null,new Position(238,225),new Position(297,242),new Position(309,225),new Position(365,243),new Position(382,225),new Position(432,241),new Position(456,225),new Position(501,241),new Position(526,225),null},
				{null,new Position(267,273),null,new Position(342,273),null,new Position(417,273),null,new Position(492,273),null,new Position(568,273),null},
				{null,new Position(223,272),new Position(272,313),new Position(303,272),new Position(353,313),new Position(382,272),new Position(434,313),new Position(462,272),new Position(518,313),new Position(541,272),null}, // FET
				{null,new Position(253,323),null,new Position(338,323),null,new Position(421,323),null,new Position(502,323),null,new Position(586,323),null}, // FET
				{null,new Position(203,333),new Position(276,341),new Position(291,333),new Position(363,341),new Position(382,333),new Position(449,341),new Position(471,333),new Position(535,341),new Position(561,333),null}, // FET
				{null,new Position(235,387),null,new Position(328,387),null,new Position(423,387),null,new Position(517,387),null,new Position(610,387),null}, // FET
				{null,new Position(181,407),new Position(239,453),new Position(281,407),new Position(340,453),new Position(382,407),new Position(445,453),new Position(482,407),new Position(550,453),new Position(583,407),null}, // FET
				{null,new Position(210,468),null,new Position(317,468),null,new Position(424,468),null,new Position(532,468),null,new Position(639,468),null}, // FET
				{null,new Position(147,505),new Position(239,497),new Position(265,505),new Position(350,497),new Position(382,505),new Position(464,497),new Position(500,505),new Position(575,497),new Position(619,505),null}, // FET
				{null,null,null,null,null,null,null,null,null,null,null}
			};
			mapPositions = mapPositionsAux;
		}else if(event.equals("summer")) {
			Position[][] mapPositionsAux = {
				{null,null,null,null,null,null,null,null,null,null,null},
				{null,new Position(262,150),new Position(312,178),new Position(320,150),new Position(371,177),new Position(382,150),new Position(426,174),new Position(442,150),new Position(485,174),new Position(504,150),null},
				{null,new Position(292,197),null,new Position(355,197),null,new Position(378,197),null,new Position(441,197),null,new Position(503,197),null},
				{null,new Position(250,184),new Position(306,206),new Position(316,184),new Position(371,206),new Position(382,184),new Position(428,205),new Position(448,184),new Position(492,205),new Position(515,184),null},
				{null,new Position(285,233),null,new Position(350,232),null,new Position(377,233),null,new Position(445,232),null,new Position(514,232),null},
				{null,new Position(238,225),new Position(297,242),new Position(309,225),new Position(365,243),new Position(382,225),new Position(432,241),new Position(456,225),new Position(501,241),new Position(526,225),null},
				{null,new Position(269,277),null,new Position(348,273),null,new Position(374,273),null,new Position(449,273),null,new Position(525,273),null},
				{null,new Position(223,272),new Position(279,284),new Position(303,272),new Position(356,284),new Position(382,272),new Position(433,284),new Position(462,272),new Position(511,284),new Position(541,272),null},
				{null,new Position(258,325),null,new Position(342,325),null,new Position(379,325),null,new Position(456,325),null,new Position(539,325),null},
				{null,new Position(203,333),new Position(265,337),new Position(291,333),new Position(351,337),new Position(382,333),new Position(448,337),new Position(471,333),new Position(528,340),new Position(561,333),null},
				{null,new Position(242,394),null,new Position(336,397),null,new Position(368,387),null,new Position(463,387),null,new Position(555,387),null},
				{null,new Position(181,407),new Position(247,404),new Position(281,407),new Position(344,404),new Position(382,407),new Position(455,404),new Position(482,407),new Position(552,404),new Position(583,407),null},
				{null,new Position(216,482),null,new Position(327,481),null,new Position(364,469),null,new Position(471,469),null,new Position(578,469),null},
				{null,new Position(147,505),new Position(225,490),new Position(265,505),new Position(336,490),new Position(382,505),new Position(464,490),new Position(500,505),new Position(575,490),new Position(619,505),null},
				{null,null,null,null,null,null,null,null,null,null,null}
			};
			mapPositions = mapPositionsAux;
		}else if(event.equals("winter")){
			Position[][] mapPositionsAux = {
					{null,null,null,null,null,null,null,null,null,null,null},
					{null,new Position(262,150),new Position(314,174),new Position(320,150),new Position(373,174),new Position(382,150),new Position(426,174),new Position(442,150),new Position(485,174),new Position(504,150),null},
					{null,new Position(294,197),null,new Position(357,197),null,new Position(378,197),null,new Position(441,197),null,new Position(503,197),null},
					{null,new Position(250,184),new Position(307,205),new Position(316,184),new Position(371,205),new Position(382,184),new Position(428,205),new Position(448,184),new Position(492,205),new Position(515,184),null},
					{null,new Position(285,232),null,new Position(353,232),null,new Position(377,232),null,new Position(445,232),null,new Position(514,232),null},
					{null,new Position(238,225),new Position(298,241),new Position(309,225),new Position(367,241),new Position(382,225),new Position(432,241),new Position(456,225),new Position(501,241),new Position(526,225),null},
					{null,new Position(273,273),null,new Position(348,273),null,new Position(374,273),null,new Position(449,273),null,new Position(525,273),null},
					{null,new Position(223,272),new Position(279,284),new Position(303,272),new Position(356,284),new Position(382,272),new Position(443,284),new Position(462,272),new Position(520,284),new Position(541,272),null},
					{null,new Position(258,325),null,new Position(342,325),null,new Position(370,325),null,new Position(456,325),null,new Position(539,325),null},
					{null,new Position(203,333),new Position(265,337),new Position(291,333),new Position(351,337),new Position(382,333),new Position(448,337),new Position(471,333),new Position(534,337),new Position(561,333),null},
					{null,new Position(243,387),null,new Position(336,387),null,new Position(368,387),null,new Position(463,387),null,new Position(555,387),null},
					{null,new Position(181,407),new Position(247,404),new Position(281,407),new Position(344,404),new Position(382,407),new Position(455,404),new Position(482,407),new Position(552,404),new Position(583,407),null},
					{null,new Position(219,469),null,new Position(328,469),null,new Position(364,469),null,new Position(471,469),null,new Position(578,469),null},
					{null,new Position(147,505),new Position(225,490),new Position(265,505),new Position(336,490),new Position(382,505),new Position(464,490),new Position(500,505),new Position(575,490),new Position(619,505),null},
					{null,null,null,null,null,null,null,null,null,null,null}
				};
				mapPositions = mapPositionsAux;
		}else{
			Position[][] mapPositionsAux = {
				{null,null,null,null,null,null,null,null,null,null,null},
				{null,new Position(262,150),new Position(314,174),new Position(320,150),new Position(373,174),new Position(382,150),new Position(426,174),new Position(442,150),new Position(485,174),new Position(504,150),null},
				{null,new Position(294,197),null,new Position(357,197),null,new Position(378,197),null,new Position(441,197),null,new Position(503,197),null},
				{null,new Position(250,184),new Position(307,205),new Position(316,184),new Position(371,205),new Position(382,184),new Position(428,205),new Position(448,184),new Position(492,205),new Position(515,184),null},
				{null,new Position(285,232),null,new Position(353,232),null,new Position(377,232),null,new Position(445,232),null,new Position(514,232),null},
				{null,new Position(238,225),new Position(298,241),new Position(309,225),new Position(367,241),new Position(382,225),new Position(432,241),new Position(456,225),new Position(501,241),new Position(526,225),null},
				{null,new Position(273,273),null,new Position(348,273),null,new Position(374,273),null,new Position(449,273),null,new Position(525,273),null},
				{null,new Position(223,272),new Position(279,284),new Position(303,272),new Position(356,284),new Position(382,272),new Position(443,284),new Position(462,272),new Position(520,284),new Position(541,272),null},
				{null,new Position(258,325),null,new Position(342,325),null,new Position(370,325),null,new Position(456,325),null,new Position(539,325),null},
				{null,new Position(203,333),new Position(265,337),new Position(291,333),new Position(351,337),new Position(382,333),new Position(448,337),new Position(471,333),new Position(534,337),new Position(561,333),null},
				{null,new Position(243,387),null,new Position(336,387),null,new Position(368,387),null,new Position(463,387),null,new Position(555,387),null},
				{null,new Position(181,407),new Position(247,404),new Position(281,407),new Position(344,404),new Position(382,407),new Position(455,404),new Position(482,407),new Position(552,404),new Position(583,407),null},
				{null,new Position(219,469),null,new Position(328,469),null,new Position(364,469),null,new Position(471,469),null,new Position(578,469),null},
				{null,new Position(147,505),new Position(225,490),new Position(265,505),new Position(336,490),new Position(382,505),new Position(464,490),new Position(500,505),new Position(575,490),new Position(619,505),null},
				{null,null,null,null,null,null,null,null,null,null,null}
			};
			mapPositions = mapPositionsAux;
		}
		
		return mapPositions;
	}
	
	private static Long getStateHash(int lv, Position carPos, Character carDir, DateStats stats, Map<Position,Integer> timers) {
		Long hash = 0L;
		 // Lv - 3 bits
		hash = hash * 25 + lv;
		// Car direction - 1 bits
		if(carDir == '^') hash = hash * 2 + 0;
		else if(carDir == 'v') hash = hash * 2 + 1;
		else if(carDir == '>') hash = hash * 2 + 0;
		else if(carDir == '<') hash = hash * 2 + 1;
		// Car position
		hash = hash * 11 + carPos.x;
		hash = hash * 15 + carPos.y;
		// Stats
		hash = hash * 101 + stats.drink;
		hash = hash * 101 + stats.entertainment;
		hash = hash * 101 + stats.food;
		hash = hash * 11 + stats.fuel/10;
		hash = hash * 2 + (stats.ring?1:0);
		hash = hash * 3 + (stats.AP==0?0:(stats.AP==30?1:2));
		// Timers
		Integer hash2 = 0;
		for (Map.Entry<Position, Integer> entry : timers.entrySet()) {
			Position pos = entry.getKey();
			Integer cd = entry.getValue();
			
			Integer x = pos.x-carPos.x;
			if(x<0) x = -x;
			Integer y = pos.y-carPos.y;
			if(y<0) y = -y;
			
			if(x+y<cd*2+1) {
				if(cd>10) cd = 10;
	
				Integer h = 0;
				h = h * 5 + (pos.x-1)/2;
				h = h * 7 + (pos.y-1)/2;
				h = h * 11 + cd;
				hash2 = hash2 + h^2;
			}
		}
		hash = hash*1000000 + hash2;
		
		return hash;
	}
	
	public static String showMap(char[][] map) {
		String mapStr = "";
		for(int i = 0; i<map.length;i++) {
			for(int j = 0; j<map[0].length;j++) {
				mapStr = mapStr + emojis.get(map[i][j]);
			}
			mapStr = mapStr + "\n";
		}
		
		return mapStr;
	}
	
	private static boolean checkIfRoad(int targetColor, int[] roadColors, int[] nonRoadColors) {
		Double minDist = null;
		for(int color:roadColors) {
			Double dist = getColorDist(targetColor,color);
			if(minDist == null || dist < minDist) minDist = dist;
		}
		for(int color:nonRoadColors) {
			Double dist = getColorDist(targetColor,color);
			if(minDist == null || dist < minDist) return false;
		}
		return true;
	}
	
 	private static Double getColorDist(int targetColor, int color) {
 		int colorRGB[] = {(color & 0xff0000) >> 16,(color & 0xff00) >> 8,color & 0xff};
 		int targetColorRGB[] = {(targetColor & 0xff0000) >> 16,(targetColor & 0xff00) >> 8,targetColor & 0xff};
 		
 		int sum = 0;
 		for(int i = 0; i<3;i++) {
 			int dist = colorRGB[i]-targetColorRGB[i];
 			if(dist<0) dist = -dist;
 			sum = sum + dist;
 		}
 		
 		return (double) sum;
	}

	private static List<Candidate> getCandidates(Position carPos, char carDir, char[][] map,Map<Position,Integer> timers){
		
		List<Candidate> actions = new ArrayList<Candidate>();
		
		// Get laterals
		if(carDir == '^' || carDir == 'v') {
			Position westPos = new Position(carPos.x-1,carPos.y);
			if(westPos.x >= 0 && westPos.x <= 10 && map[westPos.y][westPos.x] != 'G' && !timers.containsKey(westPos))
				actions.add(new Candidate(map[westPos.y][westPos.x],'w',westPos));
			
			Position eastPos = new Position(carPos.x+1,carPos.y);
			if(eastPos.x >= 0 && eastPos.x <= 10 && map[eastPos.y][eastPos.x] != 'G' && !timers.containsKey(eastPos))
				actions.add(new Candidate(map[eastPos.y][eastPos.x],'e',eastPos));
		}else {
			Position northPos = new Position(carPos.x,carPos.y-1);
			if(northPos.y >= 0 && northPos.y <= 14 && map[northPos.y][northPos.x] != 'G' && !timers.containsKey(northPos)) 
				actions.add(new Candidate(map[northPos.y][northPos.x],'n',northPos));
			Position southPos = new Position(carPos.x,carPos.y+1);
			if(southPos.y >= 0 && southPos.y <= 14 && map[southPos.y][southPos.x] != 'G' && !timers.containsKey(southPos))
				actions.add(new Candidate(map[southPos.y][southPos.x],'s',southPos));
		}
		
		// Check directions
		Position northPos = new Position(carPos.x,carPos.y-2);
		if(northPos.y >= 0 && map[northPos.y][northPos.x] == '.' && carDir=='^') actions.add(new Candidate('.',northPos));
		
		Position southPos = new Position(carPos.x,carPos.y+2);
		if(southPos.y <= 14 && map[southPos.y][southPos.x] == '.' && carDir=='v') actions.add(new Candidate('.',southPos));
		
		Position eastPos = new Position(carPos.x+2,carPos.y);
		if(eastPos.x <= 10 && map[eastPos.y][eastPos.x] == '.' && carDir=='>') actions.add(new Candidate('.',eastPos));
		
		Position westPos = new Position(carPos.x-2,carPos.y);
		if(westPos.x >= 0 && map[westPos.y][westPos.x] == '.' && carDir=='<') actions.add(new Candidate('.',westPos));
		
		Position northestPos = new Position(carPos.x+1,carPos.y-1);
		if(northestPos.x <= 10 && northestPos.y >= 0 && map[northestPos.y][northestPos.x] == '.' && (carDir=='^' || carDir=='>')) actions.add(new Candidate('.',northestPos));
		
		Position southestPos = new Position(carPos.x+1,carPos.y+1);
		if(southestPos.x <= 10 && southestPos.y <= 14 && map[southestPos.y][southestPos.x] == '.' && (carDir=='v' || carDir=='>')) actions.add(new Candidate('.',southestPos));
		
		Position southwestPos = new Position(carPos.x-1,carPos.y+1);
		if(southwestPos.x >= 0  && southwestPos.y <= 14 && map[southwestPos.y][southwestPos.x] == '.' && (carDir=='v' || carDir=='<')) actions.add(new Candidate('.',southwestPos));
		
		Position northwestPos = new Position(carPos.x-1,carPos.y-1);
		if(northwestPos.x >= 0  && northwestPos.y >= 0 && map[northwestPos.y][northwestPos.x] == '.' && (carDir=='^' || carDir=='<')) actions.add(new Candidate('.',northwestPos));
		
		return actions;
	}

	private static Map<Position, Integer> updateTimers(Map<Position, Integer> timers, Candidate candidate) {
		Map<Position, Integer> newTimers = new HashMap<Position, Integer>();
		for(Map.Entry<Position, Integer> entry:timers.entrySet()) {
			int cd = entry.getValue()-1;
			if(cd > 0) newTimers.put(entry.getKey(), cd);			
		}
		if(candidate.action != '.') newTimers.put(candidate.position, statsActions.get(candidate.action).cd); 
		return newTimers;
	}

	private static String actionsToEmoji(List<Character> actions) {
		String res = "";
		for(char action:actions) {
			res = res + emojis.get(action);
		}
		return res;
	}

	private static List<Character> reverse(List<Character> list){
		List<Character> revList = new ArrayList<Character>();
		for(int i = list.size()-1;i>=0;i--) revList.add(list.get(i));
		return revList;
	}
	
	////////////////////
	// Helper Classes //
	////////////////////
	
	public static class Candidate {		
		char action;
		char relativePosition;
		Position position;
		
		
		public Candidate(char action,char relativePosition, Position position) {
			this.action = action;
			this.relativePosition = relativePosition;
			this.position = position;
		}
		
		public Candidate(char action,Position position) {
			this.action = action;
			this.relativePosition = ' ';
			this.position = position;
		}

		public Position getCarPos(Position carPos) {
			if(action == '.')
				return position;
			return carPos;
		}

		public Character getCarDir(Position carPos, Character carDir) {
			if(action == '.')
				if((carDir == '^' || carDir == 'v') && carPos.x < position.x) return '>';
				else if((carDir == '^' || carDir == 'v') && carPos.x > position.x) return '<';
				else if((carDir == '<' || carDir == '>') && carPos.y > position.y) return '^';
				else if((carDir == '<' || carDir == '>') && carPos.y < position.y) return 'v';
			return carDir;
		}

		public DateStats getStats() {
			return statsActions.get(action);
		}
	}
	
	public static class Solution {
		DateStats stats;
		List<Character> actions;

		public Solution(DateStats stats) {
			this.actions = new ArrayList<Character>();
			this.stats = stats;
		}

		public int getPuntuation() {
			return stats.getPuntuation();
		}
		
		public String getRealPoints() {
			return stats.getRealPoints();
		}

		public void push(char action) {
			this.actions.add(action);
		}
		
		public String getActions(){
			return actionsToEmoji(reverse(this.actions));
		}
	}
	
	///////////////
	// CONSTANTS //
	///////////////

	private static Map<Character,String> emojis = new HashMap<Character,String>();
	static {
		emojis.put('<', "◀️");
		emojis.put('v', "🔽");
		emojis.put('>', "▶️");
		emojis.put('^', "🔼");
		
		emojis.put('X', "❌");
		emojis.put('.', "◼️");
		emojis.put('G', "🌲");
		
		emojis.put('H', "🏡");
		emojis.put('A', "✈️");
		emojis.put('M', "🛍️");
		emojis.put('R', "💍");
		emojis.put('E', "⛽");
		
		emojis.put('n', Global.gas_north);
		emojis.put('s', Global.gas_south);
		emojis.put('e', Global.gas_east);
		emojis.put('w', Global.gas_west);
		
		emojis.put('K', "🌮");
		emojis.put('P', "🍝");
		emojis.put('C', "☕");
		emojis.put('J', "🧃");
		emojis.put('S', "🥪");
		
		emojis.put('B', "🍹");
		emojis.put('f', "🌼");
		emojis.put('F', "🎡");
		emojis.put('T', "🎭");
		emojis.put('D', "💃");
	}
	
	private static Map<Character,DateStats> statsActions = new HashMap<Character,DateStats>();
	static {
		statsActions.put('X',new DateStats(0,-4,-6,-8,-4,-100,false,false,100)); // X
		statsActions.put('.',new DateStats(-10,-4,-6,-8,-4,0,false,false)); // MOVE
		
		statsActions.put('H',new DateStats(0,-4,-6,-8,-4,0,false,true)); // Home
		statsActions.put('A',new DateStats(0,-100,0,0,-4,0,false,false,100)); // Airplane
		statsActions.put('M',new DateStats(0,-4,-6,-8,-4,30,false,false,100)); // Mall
		statsActions.put('R',new DateStats(0,-4,-6,-8,-4,0,true,false,100)); // Ring
		statsActions.put('E',new DateStats(100,-4,-6,-8,-4,0,false,false)); // Gas Station
		
		statsActions.put('K',new DateStats(0,60-4,-6,-8,-4,0,false,false)); // Taco Stand
		statsActions.put('P',new DateStats(0,60-4,-6,-8,-4,0,false,false)); // Italian Restaurant
		statsActions.put('C',new DateStats(0,-4,60-6,-8,-4,0,false,false)); // Coffeehouse
		statsActions.put('J',new DateStats(0,-4,60-6,-8,-4,0,false,false)); // Juice Bar
		statsActions.put('S',new DateStats(0,40-4,20-6,-8,-4,0,false,false)); // Sandwich Shop
		
		statsActions.put('B',new DateStats(0,-4,40-6,40-8,-4,0,false,false)); // Nightclub
		statsActions.put('f',new DateStats(0,-4,-6,100-8,-4,0,false,false,100)); // Flower Garden
		statsActions.put('T',new DateStats(0,-4,-6,60-8,-4,0,false,false)); // Theater
		statsActions.put('F',new DateStats(0,20-4,20-6,40-8,-4,0,false,false)); // Fair
		statsActions.put('D',new DateStats(0,-10-4,-15-6,100-8,-4,0,false,false)); // Ballroom
	}
	
	private static List<EmojiPattern> emojiPatterns = new ArrayList<EmojiPattern>(); 
	static {
		int[][] threePattern = {
			{0,8,8,8,8,0},
			{8,8,0,0,8,8},
			{1,2,3,3,2,1},
			{3,3,3,3,3,3},
			{3,3,4,4,3,3},
			{3,5,0,0,5,3},
			{7,8,8,8,8,7},
			{8,8,8,8,8,8},
			{8,8,8,8,8,8},
			{9,9,9,9,9,9},
			{9,9,9,9,9,9}
		};
		emojiPatterns.add(new EmojiPattern('G',threePattern));
		
		int[][] threePattern2 = {
			{1,1,1,1,2,0,1},
			{0,0,0,1,0,1,1},
			{5,5,5,0,4,4,4},
			{5,5,5,5,5,5,5},
			{5,5,0,6,5,5,5},
			{0,0,0,0,0,5,5},
			{0,0,0,7,7,0,5},
		};
		emojiPatterns.add(new EmojiPattern('G',threePattern2));
		
		int[][] threePattern3 = {
			{1,0,2,1,1,1,1},
			{1,1,0,1,0,0,0},
			{4,4,4,0,5,5,5},
			{5,5,5,5,5,5,5},
			{5,5,5,6,0,5,5},
			{5,5,0,0,0,0,0},
			{5,0,7,7,0,0,0},
		};
		emojiPatterns.add(new EmojiPattern('G',threePattern3));
		
		int[][] threePattern4 = {
			{0,0,0,0,3,0,2,2},
			{0,3,3,3,0,0,2,2},
			{0,0,0,0,4,0,0,5},
			{1,1,0,4,0,4,4,6},
			{1,1,0,0,0,0,0,0}
		};
		emojiPatterns.add(new EmojiPattern('G',threePattern4));
		
		int[][] threePattern5 = {
			{2,2,0,3,0,0,0,0},
			{2,2,0,0,3,3,3,0},
			{5,0,0,4,0,0,0,0},
			{6,4,4,0,4,0,1,1},
			{0,0,0,0,0,0,1,1}
		};
		emojiPatterns.add(new EmojiPattern('G',threePattern5));
		
		int[][] homePattern = {
			{1,1,0,2,2,0,0,2,2,0,1,1,1},
			{1,1,0,2,2,0,0,2,2,0,1,1,1},
			{0,0,0,2,2,0,0,2,2,0,0,0,0},
			{2,2,2,2,2,0,0,2,2,2,2,2,2},
			{2,2,2,2,2,0,0,2,2,2,2,2,2},
			{2,2,2,2,2,0,0,2,2,2,2,2,2},
			{0,0,0,2,2,0,0,2,2,0,0,0,0},
			{1,1,0,2,2,0,0,2,2,0,3,3,3},
			{1,1,0,2,2,0,0,2,2,0,3,3,3},
			{1,1,0,2,2,0,0,2,2,0,3,3,3}
		};
		emojiPatterns.add(new EmojiPattern('H',homePattern));
		
		int[][] homePattern2 = {
			{1,1,1,0,2,2,0,0,2,2,0,1,1},
			{1,1,1,0,2,2,0,0,2,2,0,1,1},
			{0,0,0,0,2,2,0,0,2,2,0,0,0},
			{2,2,2,2,2,2,0,0,2,2,2,2,2},
			{2,2,2,2,2,2,0,0,2,2,2,2,2},
			{2,2,2,2,2,2,0,0,2,2,2,2,2},
			{0,0,0,0,2,2,0,0,2,2,0,0,0},
			{3,3,3,0,2,2,0,0,2,2,0,1,1},
			{3,3,3,0,2,2,0,0,2,2,0,1,1},
			{3,3,3,0,2,2,0,0,2,2,0,1,1}
		};
		emojiPatterns.add(new EmojiPattern('H',homePattern2));
			
		int[][] fairPattern = {
			{0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0},
			{2,2,2,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,2,2,2},
			{3,3,3,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,3,3,3},
			{4,4,4,0,0,0,0,0,0,0,0,0,5,5,0,0,0,0,0,0,0,0,0,4,4,4},
			{0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,7,7,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,6,6,0,0,0},
			{0,0,0,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,9,9,0,0,0},
			{0,0,0,10,10,10,0,0,0,0,0,0,0,0,0,0,0,0,0,0,11,11,11,0,0,0},
			{0,0,0,10,10,10,0,0,0,0,0,0,0,0,0,0,0,0,0,0,11,11,11,0,0,0},
		};
		emojiPatterns.add(new EmojiPattern('F',fairPattern));
		
		int[][] theaterPattern = {
			{0,0,0,1,1,1,1,1,1,1,1,1},
			{0,0,0,1,1,0,0,2,2,0,0,1},
			{0,0,0,1,1,0,2,0,0,2,0,1},
			{0,0,0,1,1,0,3,1,1,3,0,1},
			{0,0,0,0,1,1,1,1,1,1,1,1},
			{0,0,0,0,1,1,1,1,1,1,1,1},
			{0,0,0,0,1,1,0,0,1,1,1,1},
			{0,0,0,0,1,1,0,0,0,1,1,1},
			{0,0,0,0,4,4,4,0,0,0,0,0},
			{4,4,4,4,4,4,4,4,0,0,0,0},
			{4,4,4,4,4,4,4,4,4,0,0,0},
			{4,4,4,4,4,4,4,4,4,0,0,0},
			{4,0,0,0,0,0,0,4,4,0,0,0},
			{4,0,0,0,0,0,0,0,4,0,0,0},
			{4,0,0,4,4,0,0,0,4,0,0,0}
		};
		emojiPatterns.add(new EmojiPattern('T',theaterPattern));
		
		int[][] theaterPattern2 = {
			{1,1,1,1,1,1,1,1,1,0,0,0},
			{1,0,0,2,2,0,0,1,1,0,0,0},
			{1,0,2,0,0,2,0,1,1,0,0,0},
			{1,0,3,1,1,3,0,1,1,0,0,0},
			{1,1,1,1,1,1,1,1,0,0,0,0},
			{1,1,1,1,1,1,1,1,0,0,0,0},
			{1,1,1,1,0,0,1,1,0,0,0,0},
			{1,1,1,0,0,0,1,1,0,0,0,0},
			{0,0,0,0,0,4,4,4,0,0,0,0},
			{0,0,0,0,4,4,4,4,4,4,4,4},
			{0,0,0,4,4,4,4,4,4,4,4,4},
			{0,0,0,4,4,4,4,4,4,4,4,4},
			{0,0,0,4,4,0,0,0,0,0,0,4},
			{0,0,0,4,0,0,0,0,0,0,0,4},
			{0,0,0,4,0,0,0,4,4,0,0,4}
		};
		emojiPatterns.add(new EmojiPattern('T',theaterPattern2));
		
		
		int[][] flowerPattern = {
			{0,1,1,1,0,2,2,2,2,0},
			{0,0,1,1,0,0,2,2,0,0},
			{0,0,0,0,0,0,0,0,0,1},
			{0,0,0,3,3,3,0,0,0,1},
			{0,0,3,3,3,3,3,0,0,1},
			{0,3,3,3,3,3,3,3,0,0},
			{0,0,3,3,3,3,3,0,0,2},
			{0,0,0,3,3,3,3,0,0,2},
			{0,0,0,0,0,0,0,0,0,2},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,2,2,0,1,1,1,0,0}
		};
		emojiPatterns.add(new EmojiPattern('f',flowerPattern));
		
		int[][] fuelPattern = {
			{1,1,1,1,1,1,1,1,1,1},
			{1,0,7,7,7,7,7,7,7,0},
			{1,0,6,6,6,6,6,6,6,0},
			{1,0,5,5,5,5,5,5,5,0},
			{1,1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1,1},
			{0,2,2,2,2,2,2,2,2,2},
			{3,3,3,3,3,3,3,3,3,3},
			{4,4,4,4,4,4,4,4,4,4},
			{4,4,4,4,4,4,4,4,4,4}
		};
		emojiPatterns.add(new EmojiPattern('E',fuelPattern));
		
		int[][] barPattern = {
			{0,0,0,0,1,1,1,1,1,1,1},
			{0,0,0,0,0,1,1,1,1,1,1},
			{2,2,2,0,0,0,0,1,1,1,1},
			{2,2,2,2,2,0,0,0,0,1,1},
			{0,3,3,3,3,0,0,0,0,0,0}
		};
		emojiPatterns.add(new EmojiPattern('B',barPattern));
		int[][] barPattern2 = {
			{1,1,1,1,1,1,1,0,0,0,0},
			{1,1,1,1,1,1,0,0,0,0,0},
			{1,1,1,1,1,0,0,2,2,2,2},
			{1,1,1,0,0,0,2,2,2,2,2},
			{1,1,0,0,0,2,2,2,2,2,0},
			{0,0,3,3,3,3,3,3,3,3,3}
		};
		emojiPatterns.add(new EmojiPattern('B',barPattern2));
		
		int[][] airplanePattern = {
			{1,1,1,1,1,1,1,1,2,3,3,3,3,3},
			{1,1,1,1,1,1,1,2,3,3,3,3,3,3},
			{1,1,1,1,1,1,4,3,3,3,3,3,3,3},
			{1,1,1,1,1,4,0,3,3,3,3,3,3,3},
			{0,0,0,1,0,0,3,3,3,3,3,3,3,3},
			{0,0,0,0,0,3,3,3,3,3,3,3,3,0},
			{0,0,0,0,3,3,3,3,3,3,3,0,0,1},
			{0,0,0,3,3,3,3,3,3,3,0,0,1,1},
			{0,0,3,3,3,3,3,3,3,0,0,1,1,1},
			{0,3,3,3,3,3,3,3,0,0,1,1,1,1},
			{3,3,3,3,3,3,3,0,0,1,1,1,1,1}
			
		};
		emojiPatterns.add(new EmojiPattern('A',airplanePattern));
		
		int[][] airplanePattern2 = {
			{1,1,1,1,1,2,3,3,3,3,3,3,3,3},
			{1,1,1,1,1,1,2,3,3,3,3,3,3,3},
			{1,1,1,1,1,1,1,4,3,3,3,3,3,3},
			{1,1,1,1,1,1,1,0,4,3,3,3,3,3},
			{1,1,1,1,1,1,1,1,0,0,3,0,0,0},
			{0,1,1,1,1,1,1,1,1,0,0,0,0,0},
			{3,0,0,1,1,1,1,1,1,1,0,0,0,0},
			{3,3,0,0,1,1,1,1,1,1,1,0,0,0},
			{3,3,3,0,0,1,1,1,1,1,1,1,0,0},
			{3,3,3,3,0,0,1,1,1,1,1,1,1,0},
			{3,3,3,3,3,0,0,1,1,1,1,1,1,1},
		};
		emojiPatterns.add(new EmojiPattern('A',airplanePattern2));
		
		int[][] dancePattern = {
			{0,1,1,0,0,0,0,0,0,0},
			{0,0,0,2,0,3,0,4,0,0},
			{0,0,5,0,3,3,3,0,0,0},
			{0,0,0,0,3,3,3,0,0,0},
			{0,0,0,0,3,3,3,0,0,0},
			{0,6,0,0,3,3,3,0,0,0},
			{0,0,0,0,3,3,3,0,0,0},
			{3,3,3,3,3,3,3,0,0,0},
			{3,3,3,3,3,3,3,0,0,0}
		};
		emojiPatterns.add(new EmojiPattern('D',dancePattern));
		int[][] dancePattern2 = {
			{0,0,0,0,0,0,0,1,1,0},
			{0,0,2,0,3,0,4,0,0,0},
			{0,0,0,3,3,3,0,5,0,0},
			{0,0,0,3,3,3,0,0,0,0},
			{0,0,0,3,3,3,0,0,0,0},
			{0,0,0,3,3,3,0,0,6,0},
			{0,0,0,3,3,3,0,0,0,0},
			{0,0,0,3,3,3,3,3,3,3},
			{0,0,0,3,3,3,3,3,3,3}
		};
		emojiPatterns.add(new EmojiPattern('D',dancePattern2));

		int[][] juicePattern = {
			{1,2,3,3,3,3,3,3,3,3,3},
			{1,2,3,3,3,3,3,3,3,3,3},
			{1,2,3,3,3,3,3,3,0,4,4},
			{1,2,3,3,3,3,0,0,5,5,5},
			{1,2,3,0,0,6,6,6,0,0,5}

		};
		emojiPatterns.add(new EmojiPattern('J',juicePattern));
		int[][] juicePattern2 = {
			{1,1,1,1,1,1,1,1,1,2,3},
			{1,1,1,1,1,1,1,1,1,2,3},
			{4,4,0,1,1,1,1,1,1,2,3},
			{5,5,5,0,0,1,1,1,1,2,3},
			{5,0,0,6,6,6,0,0,1,2,3}
		};
		emojiPatterns.add(new EmojiPattern('J',juicePattern2));
		
		int[][] coffePattern = {
			{1,1,0,2,0,1,1,1,0,0,0,1,1,1},
			{1,1,0,2,0,1,1,0,2,0,1,1,1,1},
			{1,1,0,2,0,1,1,0,2,0,1,1,1,1},
			{1,1,1,0,2,0,1,0,2,0,1,1,1,1},
			{1,1,1,1,0,0,1,1,0,0,0,1,1,1},
			{1,1,1,1,1,1,1,1,0,0,0,1,1,1},
			{1,1,1,1,1,1,1,1,1,0,1,1,1,1}
		};
		emojiPatterns.add(new EmojiPattern('C',coffePattern));
		int[][] coffePattern2 = {
			{1,1,1,0,0,0,1,1,1,0,2,0,1,1},
			{1,1,1,1,0,2,0,1,1,0,2,0,1,1},
			{1,1,1,1,0,2,0,1,1,0,2,0,1,1},
			{1,1,1,1,0,2,0,1,0,2,0,1,1,1},
			{1,1,1,0,0,0,1,1,0,0,1,1,1,1},
			{1,1,1,0,0,0,1,1,1,1,1,1,1,1},
			{1,1,1,1,0,1,1,1,1,1,1,1,1,1}
		};
		emojiPatterns.add(new EmojiPattern('C',coffePattern2));
		
		int[][] tacoPattern = {
			{1,2,0,0,3,3,3,3},
			{4,0,0,3,3,5,6,3},
			{9,0,0,3,3,5,0,3},
			{0,8,0,3,3,3,3,3},
			{0,8,0,0,0,0,3,3},
			{0,0,0,0,0,0,3,3},
		};
		emojiPatterns.add(new EmojiPattern('K',tacoPattern));
		int[][] tacoPattern2 = {
			{1,1,1,1,0,0,2,3},
			{1,8,9,1,1,0,0,4},
			{1,0,9,1,1,0,0,5},
			{1,1,1,1,1,0,7,0},
			{1,1,0,0,0,0,7,0},
			{1,1,0,0,0,0,0,0}
		};
		emojiPatterns.add(new EmojiPattern('K',tacoPattern2));
		
		int[][] pastaPattern = {
			{0,0,0,0,0,1,2,3},
			{0,0,0,0,1,1,2,3},
			{1,1,1,1,1,0,0,3},
			{1,1,1,1,1,0,3,3},
			{0,1,1,0,0,3,3,0},
			{0,0,0,0,3,3,0,0},
			{0,3,3,3,3,0,0,0},
			{3,3,3,0,0,0,0,0}
		};
		emojiPatterns.add(new EmojiPattern('P',pastaPattern));
		int[][] pastaPattern2 = {
			{3,2,1,0,0,0,0,0},
			{3,2,1,1,0,0,0,0},
			{3,0,0,1,1,1,1,1},
			{3,3,0,1,1,1,1,1},
			{0,3,3,0,0,1,1,0},
			{0,0,3,3,0,0,0,0},
			{0,0,0,3,3,3,3,0},
			{0,0,0,0,0,3,3,3}
		};
		emojiPatterns.add(new EmojiPattern('P',pastaPattern2));
		
		int[][] sandwitchPattern = {
			{1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1},
			{2,0,1,1,1,0,3},
			{5,0,1,1,1,0,6},
			{0,0,0,0,0,0,0},
			{4,4,0,0,0,4,4},
			{0,4,4,4,4,4,0}
		};
		emojiPatterns.add(new EmojiPattern('S',sandwitchPattern));
		
		int[][] mallPattern = {
			{1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{1,1,1,1,1,1,1,1,1,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{1,1,1,1,1,1,1,1,1,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
			{1,1,1,1,1,1,1,1,1,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
			{1,1,1,1,1,1,1,1,1,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
			{1,1,1,1,1,1,1,1,1,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
			{1,1,1,1,1,1,1,1,1,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
			{1,1,1,1,1,1,1,1,1,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
			{1,1,1,1,1,1,1,1,1,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
			{1,1,1,1,1,1,1,1,1,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
		};
		emojiPatterns.add(new EmojiPattern('M',mallPattern));
		
		int[][] mallPattern2 = {
			{0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,1,1,1,1,1,1,1,1,1},
			{3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,1,1,1,1,1,1,1,1,1},
			{3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,1,1,1,1,1,1,1,1,1},
			{3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,1,1,1,1,1,1,1,1,1},
			{3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,1,1,1,1,1,1,1,1,1},
			{3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,1,1,1,1,1,1,1,1,1},
			{3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,1,1,1,1,1,1,1,1,1},
			{3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,1,1,1,1,1,1,1,1,1},
			{3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,1,1,1,1,1,1,1,1,1},
		};
		emojiPatterns.add(new EmojiPattern('M',mallPattern2));
		
		
		int[][] ringPattern = {
			{0,0,1,1,1,1,1,1,2,2,2,2,2,2,0,0},
			{0,0,1,1,1,1,1,1,2,2,2,2,2,2,0,0},
			{0,1,1,1,1,1,1,1,2,2,2,2,2,2,2,0},
			{1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2},
			{3,3,3,3,3,3,3,3,4,4,4,4,4,4,4,4},
			{0,2,2,2,2,2,2,2,1,1,1,1,1,1,1,0},
			{0,0,0,2,2,2,2,2,1,1,1,1,1,0,0,0},
			{0,0,0,0,2,2,2,2,1,1,1,1,0,0,0,0},
			{0,0,0,0,0,5,5,5,6,6,6,0,0,0,0,0},
			{0,0,0,7,7,7,7,7,7,7,7,7,7,0,0,0},
			{0,0,0,7,7,7,7,7,7,7,7,7,7,0,0,0},
			{0,0,0,7,7,7,7,7,7,7,7,7,7,0,0,0},
			{0,0,0,7,7,7,7,7,7,7,7,7,7,0,0,0}
		};
		emojiPatterns.add(new EmojiPattern('R',ringPattern));
	}
	
}
