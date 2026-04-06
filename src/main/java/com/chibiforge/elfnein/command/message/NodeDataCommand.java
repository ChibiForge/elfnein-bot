package com.chibiforge.elfnein.command.message;


import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.chibiforge.elfnein.model.NodeInfo;
import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Locator;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;
import com.chibiforge.elfnein.util.Locator.Location;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class NodeDataCommand extends MessageCommand {
	
	Boolean update;
	
	public NodeDataCommand() {
		commandId = Global.cmdIdNodes;
		update = false;
	}
	
	public NodeDataCommand(boolean update) {
		commandId = 0;
		this.update = update;
	}

	@Override
	public Boolean check(Message message) {
		// Check if message has author
		if(!message.getAuthor().isPresent()) return false;
		// Check if message is from Karuta
		if(!message.getAuthor().get().getId().asString().equals(Global.KarutaID)) return false;
		
		// Check kna trigger
		if(!update && Locator.get(message, Global.knaL).startsWith(Global.knaT)) return true;
		// Check knd trigger
		if(!update && Locator.get(message, Global.kndL).startsWith(Global.kndT)) return true;
		// Check kni trigger
		if(!update && Locator.get(message, Global.kniL).startsWith(Global.kniT)) return true;
		// Check kn trigger
		if(Locator.get(message, Global.knL).startsWith(Global.knT)) return true;
		
		return false;
	}
	
	
	@Override
	public Mono<Void> execute(Message message) {
		// Check kna trigger
		if(Locator.get(message, Global.knaL).startsWith(Global.knaT)) return handleKNA(message);
		if(Locator.get(message, Global.kndL).startsWith(Global.kndT)) return handleKND(message);
		if(Locator.get(message, Global.kniL).startsWith(Global.kniT)) return handleKNI(message);
		if(Locator.get(message, Global.knL).startsWith(Global.knT)) return handleKN(message);
		
		return Mono.empty();
	}
	
	private Mono<Void> handleKNA(Message message) {
		Location [] body = {Location.EMBED,Location.DESCRIPTION};
		String info = Locator.get(message, body);
		
		String node = info.split("`")[1];
		String holder = info.split(">")[1].split("<@")[1];
		
		NodeInfo nodeInfo = Service.karutaService.getNodeInfo(node);
			
		nodeInfo.setHolder(holder);
		nodeInfo.setUpdated(Timestamp.from(Instant.now()));
		
		Integer powerUsed = Integer.valueOf(Util.substring(info.split("spending")[1], 3, -9).replace(",", ""));
		Integer powerDealt = Integer.valueOf(Util.substring(info.split("\n")[7].split("spending")[0], 5, -4).replace(",", ""));
		
		Integer grace = Math.round((1-((float)powerDealt)/((float)powerUsed))*100);
		
		nodeInfo.setGrace(grace);
		
		Service.karutaService.updateNodeInfo(nodeInfo);
		
		return Mono.empty();
	}

	private Mono<Void> handleKND(Message message) {
		Location [] body = {Location.EMBED,Location.DESCRIPTION};
		String info = Locator.get(message, body);
		
		String node = info.split("`")[1];
		String holder = info.split(">")[1].split("<@")[1];
		
		NodeInfo nodeInfo = Service.karutaService.getNodeInfo(node);
		if(!nodeInfo.getHolder().equals(holder)) {
			nodeInfo.setHolder(holder);
			nodeInfo.setUpdated(Timestamp.from(Instant.now()));
			Service.karutaService.updateNodeInfo(nodeInfo);
		}
		
		return Mono.empty();
	}

	private Mono<Void> handleKNI(Message message) {
		String node = Locator.get(message, Global.kniL).split(":")[1].strip().toLowerCase();
		if(!node.equals("gold")) {
			// Get Holder
			Location [] locHolder = {Location.EMBED,Location.DESCRIPTION};
			String holder = Locator.get(message, locHolder).split("<@")[1].split(">")[0];
			
			String info = message.getEmbeds().get(0).getFields().get(1).getValue();
			Integer grace = 0;
			if(info.split("\n").length == 2) grace = Integer.valueOf(info.split("`")[1].replace("%", ""));
			Integer decay = Integer.valueOf(info.split("decays by")[1].split("`")[1].replace(",", ""));
			
			NodeInfo nodeInfo = Service.karutaService.getNodeInfo(node);
			nodeInfo.setHolder(holder);
			nodeInfo.setDecay(decay);
			nodeInfo.setGrace(grace);
			nodeInfo.setUpdated(Timestamp.from(Instant.now()));
			Service.karutaService.updateNodeInfo(nodeInfo);
		}
		
		return Mono.empty();
	}

	private Mono<Void> handleKN(Message message) {
		Location [] body = {Location.EMBED,Location.DESCRIPTION};
		String [] lines = Locator.get(message, body).split("\n");
		Map<String, String> newHolders = new HashMap<>();
		for(String line:lines) {
			String node = line.split("`")[1];
			if(!node.equals("gold")) {
				String holder = Util.substring(line.split("<@")[1], 0, -1);
				newHolders.put(node, holder);
			}
		}
		
		Service.karutaService.updateNodeHolders(newHolders);
		
		return Mono.empty();
	}
}
