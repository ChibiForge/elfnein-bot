package com.chibiforge.elfnein.util.DateSolver;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.chibiforge.elfnein.util.Position;

public class EmojiPattern {
	private char name;
	private int[][] pattern;
	
	public EmojiPattern(char name, int[][] pattern) {
		this.name = name;
		this.pattern = pattern;
	}
	
	public char getName() {
		return this.name;
	}
	
	public Boolean checkArea(BufferedImage image, Position position, int extend) {
		// Get Area to search
		int xMin = position.x;
		int yMin = position.y;
		
		int xMax = position.x + extend;
		int yMax = position.y + extend;
		if(yMax>=image.getHeight()) yMax = image.getHeight()-1;
		
		// Search
		int y = yMin;
		Boolean found = false;

		while(!found && y<=yMax) {
			int x = xMin;
			while(!found && x<=xMax) {
				found = check(image,new Position(x,y));
				x++;
			}
			y++;
		}
		
		return found;
	}
	
	public Boolean check(BufferedImage image, Position position) {
		Map<Integer,Integer> colorMap = new HashMap<Integer,Integer>();
		Set<Integer> colorSet = new HashSet<Integer>();
		Boolean match = true;
		int y = 0;
		while(match && y<pattern.length) {
			int x = 0;
			while(match && x<pattern[0].length) {
				int color1 = pattern[y][x];
				int color2 = image.getRGB(position.x+x, position.y+y);
				if(color1 != 0) {
					if(colorMap.containsKey(color1)) {
						color1 = colorMap.get(color1);
						match = color1 == color2;
					}else if(!colorSet.contains(color2)) {
						colorMap.put(color1,color2);
						colorSet.add(color2);
					}else {
						match = false;
					}
				}
				x++;
			}
			y++;
		}
		return match;
	}
}
