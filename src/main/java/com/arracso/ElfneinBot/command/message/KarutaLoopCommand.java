package com.arracso.ElfneinBot.command.message;


import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class KarutaLoopCommand extends MessageCommand {
	
	@Value("${discord.bot.developer.id}")
	private String developerID;
	
	public KarutaLoopCommand(){
		commandNames.add("loop");
		commandNames.add("loopsize");
		commandNames.add("setloopsize");
		commandId = Global.cmdIdLoop;
	}
	
	
	@Override
	public Mono<Void> execute(Message message) {
		String commandName = getCommandName(message);
		if(commandName.equals("loop")) {
			return executeLoop(message);
		}else if(commandName.equals("loopsize") || commandName.equals("setloopsize")) {
			return executeLoopSize(message);
		}
		
		return Util.replyToMessage(message, "Something went wrong! Please tell <@278957461120090113> to fix me!").then();
		
	}
	
	
	public Mono<Void> executeLoopSize(Message message) {
		// Get parameters
		List<String> parameters = getParameters(message);
		if(parameters.isEmpty())
			return Util.replyToMessage(message, "*Command usage:* `loopsize size`\nUse this command to setup the default size of the loop when runing the loop command.").then();
		
		// Get loop size
		Integer loopSize = Integer.valueOf(parameters.get(0));
		
		// Get author
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve user id. Please tell <@278957461120090113> to fix me!").then();
		
		String userId = message.getAuthor().get().getId().asString();
		
		Service.userService.setUserLoopSize(userId, loopSize);
		
		return Util.replyToMessage(message, "Set your default loop size to " + loopSize).then();
	}
	
	
	public Mono<Void> executeLoop(Message message) {
		// Get author
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve user id. Please tell <@" + developerID + "> to fix me!").then();
		String userId = message.getAuthor().get().getId().asString();
		
		// Check refMessage
		if(!message.getReferencedMessage().isPresent())
			return Util.replyToMessage(message, "You need to reply to node details.").then();
		
		Message refMessage = message.getReferencedMessage().get();

		if(refMessage.getEmbeds().size()==0
			|| !refMessage.getEmbeds().get(0).getTitle().isPresent()
			|| !refMessage.getEmbeds().get(0).getTitle().get().startsWith("Node Details"))
			return Util.replyToMessage(message, "You need to reply to node details.").then();
		
		// Get node
		String node = refMessage.getEmbeds().get(0).getTitle().get().split(": ")[1].toLowerCase();
		
		// Get command parameters
		List<String> parameters = getParameters(message);
		if(parameters.isEmpty())
			return Util.replyToMessage(message, "*Command usage:* `loop captureMessageID s:loopSize p:powerLeft t:timeDiff`\nBy default `loopSize` is 900 and `powerLeft` is 0.\nYou can setup your own loop size by using `loopsize` command.").then();
		
		// Get Capture Time
		Long capMsgId = Long.valueOf(parameters.get(0));
		Long capTime = (capMsgId/4194304)/1000 + 1420070400;
		
		// Set Default Parameters
		Integer loopSize = Service.userService.getUserLoopSize(userId);
		if(loopSize == null) loopSize = 900;
		Integer powerLeft = 0;
		Integer timeDiff = 0;
		
		// Retrieve parameters
		int pos = 0;
		for(String param:parameters) {
			if(param.startsWith("s:")) loopSize = Integer.valueOf(param.split(":")[1]);
			else if(param.startsWith("p:")) powerLeft = Integer.valueOf(param.split(":")[1]);
			else if(param.startsWith("t:")) timeDiff = Integer.valueOf(param.split(":")[1]);
			else if(pos == 1) loopSize = Integer.valueOf(param);
			else if(pos == 2) powerLeft = Integer.valueOf(param);
			else if(pos == 3) timeDiff = Integer.valueOf(param);
			pos++;
		}
		
		// Get Node Info
		String info = refMessage.getEmbeds().get(0).getFields().get(1).getValue();
		Integer power = Integer.valueOf(Util.substring(refMessage.getEmbeds().get(0).getDescription().get().split("\n")[4].split("/")[0].split(" ")[1], 2, -2).replace(",", "")) - powerLeft;
		Integer decay = Integer.valueOf(info.split("decays by")[1].split("`")[1].replace(",", ""));
		Integer holds = 1;
		String [] holdsLine = info.split("\n")[0].split("`");
		if(holdsLine.length == 5) holds = Integer.valueOf(holdsLine[3]);
		
		// Get Duration
		Float duration = (Instant.now().getEpochSecond()-capTime)/3600f;
		duration = duration - timeDiff/60f;
		if(duration>6f) return Util.replyToMessage(message, "Message id is from a message too old.").then();
		
		// Get Combined Size
		Integer combinedSize = loopSize + decay;
		if(combinedSize<600) return Util.replyToMessage(message, "Combined size is too low to calc.").then();
		if(combinedSize>=5000) return Util.replyToMessage(message, "Combined size is too big to calc.").then();
		
		// Get Index
		int i = 0;
		while(sizes[i]<combinedSize && i<sizes.length) i++;
		
		Float index = (float)(indexes[i-1] - (indexes[i-1]-indexes[i])*(combinedSize-sizes[i-1])/(sizes[i]-sizes[i-1]));

		// Calculate attack needed
		Float attack = (float)(9*power/((1.5+(duration-6)*index)*duration));
		Float holdsMult = 4f/(holds+3f);
		attack = attack * holdsMult;
		
		/*
		if(holds > 1) attack = attack * 0.8f;
		if(holds > 2) attack = attack * 0.833f; // 0.8*0.833333 = 0.666666
		if(holds > 3) attack = attack * 0.858f; // 2529132 to 2168267 (10M)
		if(holds > 4) attack = attack * 0.876f; // 2040015 to 1785416 (10M)
		if(holds > 5) attack = attack * 0.89f; // 2019999 to 1797655 (10M)
		if(holds > 6) attack = attack * 0.9f; // 3265020 to 2939324 (10M) 
		if(holds > 7) attack = attack * 0.909f; // Extrapolation 
		if(holds > 8) attack = attack * 0.917f; // 75212 to 68954 (1M)
		*/
		
		Integer finalAttack = Math.round(attack);
		
		String strPL = "";
		String strTD = "";
		if(powerLeft != 0) strPL = "\n**Power Left:** "+powerLeft;
		if(timeDiff != 0) strTD = "\n**Time Diff:** "+timeDiff;
		EmbedCreateSpec embed = EmbedCreateSpec.builder()
				.color(Color.SEA_GREEN)
				.title("LOOP ATTACK")
				.description("**Capture Time:** <t:" + capTime + ":t>\n**Loop Size:** " + loopSize + strPL + strTD 
					+ "\n\n**Exact value:** " + finalAttack 
					+ "\n**Round value:** " + (finalAttack/100 * 100 + 100)
					+ "```kna " + node + " " + finalAttack + "```"
					+ "" + "_[Support server](https://discord.gg/2Ke6E4jQrg)_")
				.build();
		
		EmbedCreateSpec embed2 = EmbedCreateSpec.builder()
				.color(Color.SEA_GREEN)
				.description("kna " + node + " " + finalAttack)
				.build();
		
		return Util.replyToMessage(message, embed).then(message.getChannel().block().createMessage(embed2).then()).then();
	}
	
	private static int[] sizes = {
		600, 650, 700, 750, 800,
		850, 865, 875, 900, 925,
		950, 975, 1000, 1025, 1050,
		1075, 1100, 1125, 1150, 1175,
		1200, 1225,1615, 1900, 10000
	};
	private static double[] indexes = {
		0.1700, 0.1550, 0.1400, 0.1250, 0.0980,
		0.0840, 0.0800, 0.0780, 0.0725, 0.0675,
		0.0620, 0.0575, 0.0535, 0.0495, 0.0460,
		0.0425, 0.0390, 0.0360, 0.0340, 0.0320,
		0.0290, 0.0270, 0.0085, 0.0050, 0.0050
	};
	
}
