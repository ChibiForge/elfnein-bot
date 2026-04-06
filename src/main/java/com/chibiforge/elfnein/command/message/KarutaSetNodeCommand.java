package com.chibiforge.elfnein.command.message;

import java.util.List;

import com.chibiforge.elfnein.model.NodeInfo;
import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class KarutaSetNodeCommand extends MessageCommand {

	public KarutaSetNodeCommand(){
		commandNames.add("setnode");
		commandNames.add("sn");
		commandId = Global.cmdIdNodeInfo;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		List<String> parameter = getParameters(message);

		if(parameter.size() != 2) return Util.replyToMessage(message, "Usage: e.setnode node managed").then(); 
		
		String node = parameter.get(0);
		
		String status = parameter.get(1);
		Boolean managed = false;
		if(status.equals("1") || status.equals("managed")) managed = true;
		if(status.equals("0") || status.equals("ffa")) managed = false;
		
		
		NodeInfo nodeInfo = Service.karutaService.getNodeInfo(node);
		if(nodeInfo == null) return Util.replyToMessage(message, "Specify a correct node.").then();
		
		nodeInfo.setManaged(managed);
		
		Service.karutaService.updateNodeInfo(nodeInfo);
		
		return Util.replyToMessage(message, "Node info registered!").then();

	}
	
}
