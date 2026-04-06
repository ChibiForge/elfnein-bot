package com.chibiforge.elfnein.command.message;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.data.util.Pair;

import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.core.object.component.Container;
import discord4j.core.object.component.Section;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.component.Thumbnail;
import discord4j.core.object.component.UnfurledMediaItem;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class BleachEventShinigamiHealCommand extends MessageCommand {
	
	public BleachEventShinigamiHealCommand(){
		commandNames.add("heal");
		commandNames.add("shinigami heal");
		commandId = Global.bleachEventBase;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve user id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();
		
		if(!message.getGuildId().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve guild id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();
		
		String userId = message.getAuthor().get().getId().asString();
		String guildId = message.getGuildId().get().asString();
		
		if(!Service.bleachEventService.isUserInEvent(guildId,userId))
			return Util.replyToMessage(message,"You haven't graduated from the shinigami academy yet!\n-# Do `e.academy` to join the shinigami academy.").then();
		
		String targetId = "";
		List<String> parameter = getParameters(message);
		if(!parameter.isEmpty()) {
			if(parameter.get(0).startsWith("<@"))
				targetId = parameter.get(0).split("@")[1].split(">")[0];
			else targetId = parameter.get(0);
		} else if(userId.equals("696347459751903293")) {
			Service.bleachEventService.forceHeal(guildId,userId);
			return Util.replyToMessage(message, "You healed yourself.").then();
		}else {
			return Util.replyToMessage(message, "You need to tag an injured Shinigami.").then();
		}
		
		if(Service.bleachEventService.isUserInHealCd(guildId,userId)) {
			Timestamp cd = Service.bleachEventService.getUserHealCd(guildId,userId);
			return Util.replyToMessage(message,"**You are on cooldown!**\n*Cooldown finish <t:" + cd.getTime()/1000 + ":R>*").then();
		}
		
		if(Service.bleachEventService.isUserInjured(guildId,userId)) {
			Timestamp cd = Service.bleachEventService.getUserInjureCd(guildId,userId);
			return Util.replyToMessage(message,"**You cannot heal somebody else while you are injured!**\n*You will be fully healed <t:" + cd.getTime()/1000 + ":R>*").then();
		}
		
		if(!Service.bleachEventService.isUserInEvent(guildId,targetId))
			return Util.replyToMessage(message,"Cannot find this Shinigami.").then();
		
		if(!Service.bleachEventService.isUserInjured(guildId,targetId))
			return Util.replyToMessage(message,"This Shinigami is not in need of medical attention.").then();
		
		return Util.replyToMessage(message,healContainer(guildId,userId,targetId)).then();
	}
	
    
	private Container healContainer(String guildId, String userId, String targetId) {
		Pair<Timestamp,Integer> healing = Service.bleachEventService.healShinigami(guildId,userId,targetId);
		
		String text = "You chant healing Kidō upon <@" + targetId + ">...\n ...*it doesn't seem to make any effect*";
		String text2 = "-# Train your Kidō to be able to heal.";
		if(healing != null) {
			Integer heal = healing.getSecond();
			Timestamp injure = healing.getFirst();
			if(injure.before(Timestamp.from(Instant.now())))
				text = "You chant healing Kidō upon <@" + targetId + ">...\n ...*the Shinigami has been fully healed*";
			else if(injure.before(Timestamp.from(Instant.now().plus(heal*3, ChronoUnit.MINUTES)))) {
				text = "You chant healing kido upon <@" + targetId + ">...\n ...*the Shinigami will be fully healed <t:" + injure.getTime()/1000 + ":R>*";
			}else {
				text = "You chant healing kido upon <@" + targetId + ">...\n  ...*it doesn't seem to make much effect*\n*The Shinigami will be fully healed <t:" + injure.getTime()/1000 + ":R>*";
			}
			text2 = "-# Healing done: "+ heal +" minutes.";
		}
		
		return Container.of(
			Color.BLACK,
			Section.of(
				Thumbnail.of(UnfurledMediaItem.of("https://giffiles.alphacoders.com/133/13371.gif")),
				TextDisplay.of("# Shinigami Healing"),
				TextDisplay.of(text)
			),
			TextDisplay.of(text2)
		);
	}
	
}
