package com.arracso.ElfneinBot.command.message;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import com.arracso.ElfneinBot.model.BleachEventUser;
import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

import discord4j.common.util.Snowflake;
import discord4j.core.object.component.Container;
import discord4j.core.object.component.Section;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.component.Thumbnail;
import discord4j.core.object.component.UnfurledMediaItem;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class BleachEventShinigamiCooldownsCommand extends MessageCommand {
	
	public BleachEventShinigamiCooldownsCommand(){
		commandNames.add("cooldown");
		commandNames.add("cd");
		commandId = Global.bleachEventBase;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve user id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();
		
		if(!message.getGuildId().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve guild id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();		
		
		return message.getAuthorAsMember().flatMap(member -> {
			if(!Service.bleachEventService.isUserInEvent(member.getGuildId().asString(),member.getId().asString()))
				return Util.replyToMessage(message,"You haven't graduated from the shinigami academy yet!\n-# Do `e.academy` to join the shinigami academy.").then();
			
			String targetId = null;
			List<String> parameter = getParameters(message);
			if(!parameter.isEmpty()) {
				if(parameter.get(0).startsWith("<@"))
					targetId = parameter.get(0).split("@")[1].split(">")[0];
				else targetId = parameter.get(0);
			}
			
			if(targetId != null) {
				String targetIdAux = targetId;
				if(!Service.bleachEventService.isUserInEvent(member.getGuildId().asString(),targetId))
					return Util.replyToMessage(message,"Cannot find this Shinigami.").then();
				
				return member.getGuild().flatMap(guild -> guild.getMemberById(Snowflake.of(targetIdAux))
					.flatMap(targetMember -> Util.replyToMessage(message,cdContainer(targetMember)).then())
					.onErrorResume(e->Util.replyToMessage(message,"This Shinigami left the battlefield.").then())
				);
			}
			
			return Util.replyToMessage(message,cdContainer(member)).then();
		});
		
	}

	private Container cdContainer(Member member) {
		BleachEventUser user = Service.bleachEventService.getUser(member.getGuildId().asString(),member.getId().asString());
		
		return Container.of(
			Section.of(
				Thumbnail.of(UnfurledMediaItem.of(member.getAvatarUrl())),
				TextDisplay.of("# Shinigami Cooldowns"),
				TextDisplay.of("-# Cooldowns of **" + member.getDisplayName() + "**")
			),
			TextDisplay.of("💠 **Training** " + (isPast(user.getTrainCd())? "is ready" : ("<t:" + user.getTrainCd().getTime()/1000 + ":R>"))),
			TextDisplay.of("💖 **Healing** " + (isPast(user.getHealCd())? "is ready" : ("<t:" + user.getHealCd().getTime()/1000 + ":R>"))),
			TextDisplay.of(isPast(user.getInjureCd())? "🛡️ You are not **Injured**" : ("🤕 You will stop being **Injured** <t:" + user.getInjureCd().getTime()/1000 + ":R>"))
		);
	}
	
	private boolean isPast(Timestamp time) {
		if(time == null) return true;
		return time.before(Timestamp.from(Instant.now()));
	}
	
}
