package com.chibiforge.elfnein.command.message;

import java.sql.Timestamp;
import java.util.List;

import com.chibiforge.elfnein.model.BleachEventUser;
import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.common.util.Snowflake;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.Container;
import discord4j.core.object.component.MediaGallery;
import discord4j.core.object.component.MediaGalleryItem;
import discord4j.core.object.component.Section;
import discord4j.core.object.component.Separator;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.component.Thumbnail;
import discord4j.core.object.component.UnfurledMediaItem;
import discord4j.core.object.component.Separator.SpacingSize;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class BleachEventShinigamiDuelCommand extends MessageCommand {
	
	public BleachEventShinigamiDuelCommand(){
		commandNames.add("duel");
		commandNames.add("shinigami duel");
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
		
		String targetIdAux = "";
		List<String> parameter = getParameters(message);
		if(!parameter.isEmpty()) {
			if(parameter.get(0).startsWith("<@"))
				targetIdAux = parameter.get(0).split("@")[1].split(">")[0];
			else targetIdAux = parameter.get(0);
		}
		String targetId = targetIdAux;
		
		if(Service.bleachEventService.isChannelNotAllowed(message.getChannelId().asString()))
			return Util.replyToMessage(message,"**You cannot do this here!**\n*Use this command in another channel like <#1387118821151604746> or <#1220596977713287209>*").then();

		if(targetId.isEmpty() || targetId.equals(userId)){
			return Util.replyToMessage(message, "You need to tag another Shinigami.").then();
		}
		
		if(Service.bleachEventService.isUserInjured(guildId,userId)) {
			Timestamp cd = Service.bleachEventService.getUserInjureCd(guildId,userId);
			return Util.replyToMessage(message,"**You cannot duel somebody while you are injured!**\n*You will be fully healed <t:" + cd.getTime()/1000 + ":R>*").then();
		}
		
		if(!Service.bleachEventService.isUserInEvent(guildId,targetId))
			return Util.replyToMessage(message,"Cannot find this Shinigami.").then();
		
		if(Service.bleachEventService.isUserInjured(guildId,targetId)) {
			Timestamp cd = Service.bleachEventService.getUserInjureCd(guildId,targetId);
			return Util.replyToMessage(message,"**You cannot duel somebody while they are injured!**\n*They will be fully healed <t:" + cd.getTime()/1000 + ":R>*").then();
		}
		
		return message.getGuild()
		.flatMap(guild -> guild.getMemberById(Snowflake.of(userId))
		.flatMap(memberA -> guild.getMemberById(Snowflake.of(targetId))
		.flatMap(memberB -> Util.replyToMessage(message,duelContainer(guild,memberA,memberB)).then())));
	}
	
	private Container duelContainer(Guild guild, Member memberA, Member memberB) {
		return duelContainer(guild, memberA, memberB, true);
	}
	
    
	private Container duelContainer(Guild guild, Member memberA, Member memberB, Boolean active) {
		BleachEventUser userA = Service.bleachEventService.getUser(guild.getId().asString(), memberA.getId().asString());
		BleachEventUser userB = Service.bleachEventService.getUser(guild.getId().asString(), memberB.getId().asString());
		
		TextDisplay descUserA = TextDisplay.of(
			"🗡️ **Zanjutsu** — Lv." + userA.getZanjutsuLv() + (userA.getBankai()!=null?" — *Bankai unlocked*":(userA.getShikai()!=null?" — *Shikai awakened*":"")) + "\n" +
			"🔮 **Kidō** — Lv." + userA.getKidoLv() + (userA.getKidoLv()==100?" — *Hadō #99 learned*":"") + "\n" +
			"🥋 **Hakuda** — Lv." + userA.getHakudaLv() + (userA.getHakudaLv()==100?" — *Shunko mastered*":"") + "\n" +
			"🪽 **Hohō** — Lv." + userA.getHohoLv() + (userA.getHohoLv()==100?" — *Shunpo acquired*":"")
		);
		
		TextDisplay descUserB = TextDisplay.of(
			"🗡️ **Zanjutsu** — Lv." + userB.getZanjutsuLv() + (userB.getBankai()!=null?" — *Bankai unlocked*":(userB.getShikai()!=null?" — *Shikai awakened*":"")) + "\n" +
			"🔮 **Kidō** — Lv." + userB.getKidoLv() + (userB.getKidoLv()==100?" — *Hadō #99 learned*":"") + "\n" +
			"🥋 **Hakuda** — Lv." + userB.getHakudaLv() + (userB.getHakudaLv()==100?" — *Shunko mastered*":"") + "\n" +
			"🪽 **Hohō** — Lv." + userB.getHohoLv() + (userB.getHohoLv()==100?" — *Shunpo acquired*":"")
		);
		
		return Container.of(
			Color.BLACK,
			TextDisplay.of("# Shinigami duel"),
			TextDisplay.of(memberA.getDisplayName() + " is challenging you to a duel."),
			Separator.of(true, SpacingSize.of(2)),
			TextDisplay.of("## " + memberA.getDisplayName()),
			Section.of(
				Thumbnail.of(UnfurledMediaItem.of(memberA.getAvatarUrl())),
				descUserA
			),
			Separator.of(false, SpacingSize.of(2)),
			MediaGallery.of(MediaGalleryItem.of(UnfurledMediaItem.of("https://i.postimg.cc/MKCJGp6S/vs.png"))),
			Separator.of(false, SpacingSize.of(2)),
			TextDisplay.of("## " + memberB.getDisplayName()),
			Section.of(
				Thumbnail.of(UnfurledMediaItem.of(memberB.getAvatarUrl())),
				descUserB
			),
			Separator.of(true, SpacingSize.of(2)),
			TextDisplay.of("Which discipline will you wield in duel?"),
			ActionRow.of(
				active?Button.secondary("bleach:duel:zanjutsu", "🗡️ Zanjutsu"):Button.secondary("bleach:duel:zanjutsu", "🗡️ Zanjutsu").disabled(),
				active?Button.secondary("bleach:duel:kido", "🔮 Kidō"):Button.secondary("bleach:duel:kido", "🔮 Kidō").disabled(),
				active?Button.secondary("bleach:duel:hakuda", "🥋 Hakuda"):Button.secondary("bleach:duel:hakuda", "🥋 Hakuda").disabled()
			)
		);
	}
	
}
