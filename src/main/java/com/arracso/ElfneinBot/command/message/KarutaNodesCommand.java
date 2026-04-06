package com.arracso.ElfneinBot.command.message;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.arracso.ElfneinBot.model.NodeInfo;
import com.arracso.ElfneinBot.model.ShogunInfo;
import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class KarutaNodesCommand extends MessageCommand {

	public KarutaNodesCommand(){
		commandNames.add("nodes");
		commandNames.add("n");
		commandNames.add("nudes");
		commandId = Global.cmdIdNodes;
	}
	
	@SuppressWarnings({ "deprecation" })
	@Override
	public Mono<Void> execute(Message message) {
		List<NodeInfo> nodesInfo = Service.karutaService.getAllNodesOrderedByGrace();
		
		String nodesFFA = "";
		String nodesManaged = "";
		//String nodesSniped = "";
		for(NodeInfo nodeInfo:nodesInfo) {
			//String space = "";
			String node = nodeInfo.getNode();
			
			node = "✦ "  + Global.nodes.get(node) + " **" +  node + "** ➛ "+  nodeInfo.getGrace() + "%";
			
			ShogunInfo shogunInfo = Service.karutaService.getShogunInfo(nodeInfo.getHolder());
			if(shogunInfo != null && shogunInfo.getSide() == 1) node = node + " ➛ <:loop_society_logo:1483186789651972278>";
			
			Instant now = Instant.now();
			Instant timestamp = nodeInfo.getUpdated().toInstant();
			if(timestamp.isBefore(now.minus(Duration.ofMinutes(30)))) node = node + " ➛ ⏳";
			if(timestamp.isBefore(now.minus(Duration.ofMinutes(45)))) node = node + "❘";
			if(timestamp.isBefore(now.minus(Duration.ofMinutes(60)))) node = node + "❙";
			if(timestamp.isBefore(now.minus(Duration.ofMinutes(75)))) node = node + "❚";
			if(timestamp.isBefore(now.minus(Duration.ofMinutes(90)))) node = node + "❚";
			
			
			
			
			/*if(shogunInfo != null && shogunInfo.getSide() == 2) nodesSniped = nodesSniped + node + "\n";
			else */if(nodeInfo.getManaged()) nodesManaged = nodesManaged + node + "\n";
			else nodesFFA = nodesFFA + node + "\n";
			
			
		}
		
		final String nodesFFAfinal = nodesFFA;
		final String nodesManagedfinal = nodesManaged;
		//final String nodesSnipedfinal = nodesSniped;
		final Boolean showWarning = message.getContent().toLowerCase().contains("nudes");
		
		return message.getChannel().block()
				.createMessage(spec -> {
					if(showWarning) {
						spec.setContent("# Did you mean \"nodes\"? " + Global.elf_huh);
					}
					/*if(!nodesSnipedfinal.isEmpty()) {
						spec.addEmbed(e -> {
							 e.setColor(Color.RUBY);
							 e.setTitle("⋆｡°✩    Sniped Nodes   ✩°｡⋆");
							 e.setDescription(nodesSnipedfinal);
						 });
					}*/
					if(!nodesManagedfinal.isEmpty()) {
						spec.addEmbed(e -> {
							e.setColor(Color.SEA_GREEN);
							e.setTitle("⋆｡°✩ Managed Nodes ✩°｡⋆");
							e.setDescription(nodesManagedfinal);
						});
					}
					spec.addEmbed(e -> {
						e.setColor(Color.SEA_GREEN);
						e.setTitle("⋆｡°✩      FFA Nodes      ✩°｡⋆");
						e.setDescription(nodesFFAfinal);
					});
					spec.setMessageReference(message.getId());
			    })
				.then();
	}
	
}
