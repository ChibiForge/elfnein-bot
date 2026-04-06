package com.chibiforge.elfnein.command.message;

import java.util.List;

import com.chibiforge.elfnein.model.BleachEventUser;
import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.common.util.Snowflake;
import discord4j.core.object.component.Container;
import discord4j.core.object.component.Section;
import discord4j.core.object.component.Separator;
import discord4j.core.object.component.Separator.SpacingSize;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.component.Thumbnail;
import discord4j.core.object.component.UnfurledMediaItem;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class BleachEventShinigamiStatsCommand extends MessageCommand {
	
	public BleachEventShinigamiStatsCommand(){
		commandNames.add("stats");
		commandNames.add("shinigami stats");
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
			
			if(Service.bleachEventService.isChannelNotAllowed(message.getChannelId().asString()))
				return Util.replyToMessage(message,"**You cannot do this here!**\n*Use this command in another channel like <#1387118821151604746> or <#1220596977713287209>*").then();
			
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
					.flatMap(targetMember -> Util.replyToMessage(message,statsContainer(targetMember)).then())
					.onErrorResume(e->Util.replyToMessage(message,"This Shinigami left the battlefield.").then())
				);
			}
			
			return Util.replyToMessage(message,statsContainer(member)).then();
		});
		
	}

	private Container statsContainer(Member member) {
		BleachEventUser user = Service.bleachEventService.getUser(member.getGuildId().asString(),member.getId().asString());
		Container c = null;
		
		Section base = Section.of(
			Thumbnail.of(UnfurledMediaItem.of(member.getAvatarUrl())),
			TextDisplay.of("# Shinigami Status"),
			TextDisplay.of(
				"-# Record of **" + member.getDisplayName() + "**" + 
				(user.getBankai() != null ? "\n-# Bankai — **" + user.getBankai() + "**" : 
				(user.getShikai() != null ? "\n-# Shikai — **" + user.getShikai() + "**" : ""))
			),
			TextDisplay.of("💠 Reiatsu — **" + user.getReiatsu() + "**")
		);
		
		TextDisplay trainRecords = TextDisplay.of(
			"🗡️ **Zanjutsu** — Lv." + user.getZanjutsuLv() + (user.getBankai()!=null?" — *Bankai unlocked*":(user.getShikai()!=null?" — *Shikai awakened*":"")) + "\n" +
			"🔮 **Kidō** — Lv." + user.getKidoLv() + (user.getKidoLv()==100?" — *Hadō #99 learned*":"") + "\n" +
			"🥋 **Hakuda** — Lv." + user.getHakudaLv() + (user.getHakudaLv()==100?" — *Shunko mastered*":"") + "\n" +
			"🪽 **Hohō** — Lv." + user.getHohoLv() + (user.getHohoLv()==100?" — *Shunpo acquired*":"")
		);
		
		TextDisplay battleRecords = user.getTotalHollowKills() == 0? null : TextDisplay.of(
			(user.getDemiHollowKills()>0 ? "🕳️ Demi-Hollow — **" + user.getDemiHollowKills() + "**\n" : "") +
			(user.getHugeHollowKills()>0 ? "👺 Huge Hollow — **" + user.getHugeHollowKills() + "**\n" : "") +
			(user.getGillianKills()>0 ? "👹 Gillian — **" + user.getGillianKills() + "**\n" : "") +
			(user.getAdjuchasKills()>0 ? "🐉 Adjuchas — **" + user.getAdjuchasKills() + "**\n" : "") +
			(user.getVastoLordeKills()>0 ? "👑 Vasto Lorde — **" + user.getVastoLordeKills() + "**\n" : "") +
			(user.getArrancarKills()>0 ? "⚔️ Arrancar — **" + user.getArrancarKills() + "**\n" : "")
		);
		
		TextDisplay medicalRecords = (!user.hasMedicalRecord())? null : TextDisplay.of(
			(user.getInjuries()>0 ? "🤕 Injuries — **" + user.getInjuries() + "**\n" : "") +
			(user.getMortalInjuries()>0 ? "☠️ Mortal Injuries — **" + user.getMortalInjuries() + "**\n" : "") +
			(user.getHealings()>0 ? "🩹 Healings — **" + user.getHealings() + "**\n" : "") +
			(user.getFullHealings()>0 ? "💖 Full Healings — **" + user.getFullHealings() + "**\n" : "")
		);
		
		if(battleRecords == null && medicalRecords == null) {
			c = Container.of(
				Color.BLACK, base,
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("## ✦ Training Record"),
				trainRecords
			);
		}else if(medicalRecords == null){
			c = Container.of(
				Color.BLACK, base,
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("## ✦ Training Record"),
				trainRecords,
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("## ✦ Battle Record"),
				battleRecords
			);
		}else if(battleRecords == null){
			c = Container.of(
				Color.BLACK, base,
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("## ✦ Training Record"),
				trainRecords,
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("## ✦ Medical Record"),
				medicalRecords
			);
		} else {
			c = Container.of(
				Color.BLACK, base,
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("## ✦ Training Record"),
				trainRecords,
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("## ✦ Medical Record"),
				medicalRecords,
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("## ✦ Battle Record"),
				battleRecords
			);
		}
		
		return c;
	}
	
}
