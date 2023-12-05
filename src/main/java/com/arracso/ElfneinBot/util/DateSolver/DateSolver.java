package com.arracso.ElfneinBot.util.DateSolver;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.arracso.ElfneinBot.util.Position;

public class DateSolver {
	
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
	
	public static char[][] readMap(BufferedImage image) {
		// Get colors from image
		int[] roadColors = new int[3];
		int[] nonRoadColors = new int[3];
		roadColors[0] = image.getRGB(80,593);
		roadColors[1] =  image.getRGB(83,591);
		roadColors[2] =  image.getRGB(85,591);
		
		nonRoadColors[0] = image.getRGB(109,568);
		nonRoadColors[1] =  image.getRGB(109,566);
		nonRoadColors[2] =  image.getRGB(111,564);
		
		// Get map from image
		char[][] map = new char[15][11];
		for(int i=0; i<checkPositionsMap.length;i++) {
			for(int j=0; j<checkPositionsMap[0].length;j++) {
				Position pos = checkPositionsMap[i][j];
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
				actions.add(new Candidate(map[westPos.y][westPos.x],westPos));
			
			Position eastPos = new Position(carPos.x+1,carPos.y);
			if(eastPos.x >= 0 && eastPos.x <= 10 && map[eastPos.y][eastPos.x] != 'G' && !timers.containsKey(eastPos))
				actions.add(new Candidate(map[eastPos.y][eastPos.x],eastPos));
		}else {
			Position northPos = new Position(carPos.x,carPos.y-1);
			if(northPos.y >= 0 && northPos.y <= 14 && map[northPos.y][northPos.x] != 'G' && !timers.containsKey(northPos)) 
				actions.add(new Candidate(map[northPos.y][northPos.x],northPos));
			Position southPos = new Position(carPos.x,carPos.y+1);
			if(southPos.y >= 0 && southPos.y <= 14 && map[southPos.y][southPos.x] != 'G' && !timers.containsKey(southPos))
				actions.add(new Candidate(map[southPos.y][southPos.x],southPos));
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
		for(char action:actions) res = res + emojis.get(action);
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
		Position position;
		
		public Candidate(char action,Position position) {
			this.action = action;
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
		
		statsActions.put('H',new DateStats(0,-4,-6,-8,0,0,false,true)); // Home
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
	
	private static final Position[][] checkPositionsMap = {
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
			{9,7,0,3,3,5,0,3},
			{0,8,0,3,3,3,3,3},
			{0,8,0,0,0,0,3,3},
			{0,7,0,0,0,0,3,3},
		};
		emojiPatterns.add(new EmojiPattern('K',tacoPattern));
		int[][] tacoPattern2 = {
			{1,1,1,1,0,0,2,3},
			{1,8,9,1,1,0,0,4},
			{1,0,9,1,1,0,6,5},
			{1,1,1,1,1,0,7,0},
			{1,1,0,0,0,0,7,0},
			{1,1,0,0,0,0,6,0}
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
