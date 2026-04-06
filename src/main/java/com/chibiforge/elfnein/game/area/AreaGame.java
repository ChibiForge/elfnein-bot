package com.chibiforge.elfnein.game.area;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import discord4j.core.object.component.Button;
import discord4j.core.object.component.Container;
import discord4j.core.object.component.MediaGallery;
import discord4j.core.object.component.MediaGalleryItem;
import discord4j.core.object.component.Separator;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.component.UnfurledMediaItem;

public class AreaGame {
	private String gameId;
	private int [] size;
	private int [][] board;
	private String [] players;
	private int turn;
	private int maxColors;
	private int cellSize;
	
	public AreaGame() {	}
	
	public AreaGame(String gameId, Map<String, String> metadata) {
		this.gameId = gameId;
		size = new int[]{40, 30};
		players = new String[] {metadata.get("p1"), metadata.get("p2")};
		turn = 1;
		maxColors = 6;
		cellSize = 15;
		generateBoard();
	}
	
	public AreaGame(AreaState state) {
		this.gameId = state.gameId;
		this.size = state.size;
		this.board = state.board;
		this.players = state.players;
		this.turn = state.turn;
		this.maxColors = state.maxColors;
		this.cellSize = state.cellSize;
	}
	
	public void updateState(AreaState state) {
		this.gameId = state.gameId;
		this.size = state.size;
		this.board = state.board;
		this.players = state.players;
		this.turn = state.turn;
		this.maxColors = state.maxColors;
		this.cellSize = state.cellSize;
	}
	
	public String gameId() {
		return gameId;
	}
	
	public String turn() {
		return "t"+turn;
	}
	
	public List<Button> getOptions(String stateFile) {
		List<Button> buttons = new ArrayList<Button>();
		if(maxColors>0 && board[0][size[1]-1]!=0 && board[size[0]-1][0]!=0) 
			buttons.add(Button.secondary("area:color:0:t=" + (turn % 2 == 0 ? players[0] : players[1]) + "&f=" + stateFile, colorNames.get(0)));
		if(maxColors>1 && board[0][size[1]-1]!=1 && board[size[0]-1][0]!=1)
			buttons.add(Button.secondary("area:color:1:t=" + (turn % 2 == 0 ? players[0] : players[1])+ "&f=" + stateFile, colorNames.get(1)));
		if(maxColors>2 && board[0][size[1]-1]!=2 && board[size[0]-1][0]!=2) 
			buttons.add(Button.secondary("area:color:2:t=" + (turn % 2 == 0 ? players[0] : players[1])+ "&f=" + stateFile, colorNames.get(2)));
		if(maxColors>3 && board[0][size[1]-1]!=3 && board[size[0]-1][0]!=3) 
			buttons.add(Button.secondary("area:color:3:t=" + (turn % 2 == 0 ? players[0] : players[1])+ "&f=" + stateFile, colorNames.get(3)));
		if(maxColors>4 && board[0][size[1]-1]!=4 && board[size[0]-1][0]!=4) 
			buttons.add(Button.secondary("area:color:4:t=" + (turn % 2 == 0 ? players[0] : players[1])+ "&f=" + stateFile, colorNames.get(4)));
		if(maxColors>5 && board[0][size[1]-1]!=5 && board[size[0]-1][0]!=5) 
			buttons.add(Button.secondary("area:color:5:t=" + (turn % 2 == 0 ? players[0] : players[1])+ "&f=" + stateFile, colorNames.get(5)));
		if(maxColors>6 && board[0][size[1]-1]!=6 && board[size[0]-1][0]!=6) 
			buttons.add(Button.secondary("area:color:6:t=" + (turn % 2 == 0 ? players[0] : players[1])+ "&f=" + stateFile, colorNames.get(6)));
		return buttons;
	}

	public Container getContainer(String imgFile) {
		Container body = Container.of(
			discord4j.rest.util.Color.SEA_GREEN,
			TextDisplay.of("**Area Game** - Classic - Turn " + turn),
			TextDisplay.of("*Turn of <@" + (turn % 2 == 0 ? players[0] : players[1]) + ">*"),
			Separator.of(),
			MediaGallery.of(
				MediaGalleryItem.of(UnfurledMediaItem.of("attachment://"+imgFile))
			)	
		);
		return body;
	}

	private void generateBoard() {
		board = new int[size[0]][size[1]];
		for(int x = 0; x < size[0]; x++) {
			for(int y = 0; y < size[1]/2+1; y++) {
				int color1 = (int)(Math.random() * maxColors);
				int color2 = (color1 + 1) % maxColors;
				board[x][y] = color1;
				board[size[0]-x-1][size[1]-y-1] = color2;
			}
		}
		/*
		board[0][0] = 0;
		board[size[0]-1][size[1]-1] = 1;*/
	}

	public AreaState getState() {
		AreaState state = new AreaState();
		state.gameId = this.gameId;
		state.size = this.size;
		state.board = this.board;
		state.players = this.players;
		state.turn = this.turn;
		state.maxColors = this.maxColors;
		state.cellSize = this.cellSize;
		return state;
	}
	
	public byte[]  generateImage() {
		int w = size[0] * cellSize;
		int h = size[1] * cellSize;
		BufferedImage img = new BufferedImage(w+20, h+20, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		try {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			for (int y = 0; y < size[1]; y++) {
				for (int x = 0; x < size[0]; x++) {
					g.setColor(colors.get(this.board[x][y]));
					g.fillRect(x * cellSize+10, y * cellSize+10, cellSize, cellSize);
				}
			}
		} finally {
			g.dispose();
		}
		return toPngBytes(img);
	}

	private static byte[] toPngBytes(BufferedImage img) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			if (!ImageIO.write(img, "png", baos)) {
				throw new IOException("No PNG writer available");
			}
			return baos.toByteArray();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private static final List<Color> colors = List.of(
		    new Color(255, 51, 51),    // Red
		    new Color(0, 255, 255),    // Cyan
		    new Color(0, 102, 255),    // Blue
		    new Color(255, 153, 51),   // Orange
		    new Color(153, 51, 255),   // Purple
		    new Color(255, 255, 51),   // Yellow
		    new Color(0, 204, 51),     // Green
		    new Color(255, 100, 200)     // Pink
	);
	
	private static final List<String> colorNames = List.of(
		"RED",
		"CYAN",
		"BLUE",
		"ORANGE",
		"PURPLE",
		"YELLOW",
		"GREEN",
		"PINK"
		);

	public boolean hasState() {
		// TODO Auto-generated method stub
		return gameId != null;
	}

	public void playColor(int colorSwap) {
		int [][] exploredBoard = new int[size[0]][size[1]];
		int pos [] = getStartPosition();
		int colorTarget = board[pos[0]][pos[1]];
		
		List<int[]> positions = new ArrayList<int[]>();
		positions.add(pos);
		int i = 0;
		while(i < positions.size()) {
			int posAux [] = positions.get(i);
			if(colorTarget == board[posAux[0]][posAux[1]]) {
				board[posAux[0]][posAux[1]] = colorSwap;
				exploredBoard[posAux[0]][posAux[1]] = 1;
				getAdjacents(posAux).forEach(posAdj -> {
					if(exploredBoard[posAdj[0]][posAdj[1]] != 1) positions.add(posAdj);
				});
			}
			i++;
		}
		
		turn++;
	}


	private List<int[]> getAdjacents(int[] pos) {
		List<int[]> positions = new ArrayList<int[]>();
		int[] posNorth = new int [] {pos[0], pos[1]-1};
		int[] posSouth = new int [] {pos[0], pos[1]+1};
		int[] posWest = new int [] {pos[0]-1, pos[1]};
		int[] posEast = new int [] {pos[0]+1, pos[1]};
		if(posNorth[1]>=0) positions.add(posNorth);
		if(posSouth[1]<size[1]) positions.add(posSouth);
		if(posWest[0]>=0) positions.add(posWest);
		if(posEast[0]<size[0]) positions.add(posEast);
		return positions;
	}

	private int [] getStartPosition() {
		int player = turn % players.length;
		if(player == 0) return new int[] {0,size[1]-1};
		else if (player == 1) return new int[] {size[0]-1,0};
		else if (player == 2) return new int[] {0,0};
		
		return new int[] {size[0]-1,size[1]-1};
	}

	public boolean isOver() {
		// TODO Auto-generated method stub
		return false;
	}
	
}