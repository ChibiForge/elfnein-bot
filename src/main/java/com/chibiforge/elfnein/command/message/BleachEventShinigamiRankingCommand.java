package com.chibiforge.elfnein.command.message;

import java.util.List;

import com.chibiforge.elfnein.model.BleachEventUser;
import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.core.object.component.Container;
import discord4j.core.object.component.Separator;
import discord4j.core.object.component.Separator.SpacingSize;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class BleachEventShinigamiRankingCommand extends MessageCommand {
	
	public BleachEventShinigamiRankingCommand(){
		commandNames.add("shinigami lb");
		commandNames.add("shinigami leaderboard");
		commandNames.add("shinigami ranking");
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
			
			List<String> arg = getParameters(message);
			String order = arg.size()>0? arg.get(0):"reiatsu";
			
			return member.getGuild().flatMap(guild -> Util.replyToMessageSilent(message,rankingContainer(guild,member,order)).then());
		});
		
	}

	private Container rankingContainer(Guild guild, Member member, String order) {
		List<BleachEventUser> users = Service.bleachEventService.getUsers(member.getGuildId().asString(),order);
		
		
		String top10users = "";
		Integer found = 0;
		int i = 0;
		while(i<users.size() && (found == 0 || i<10)) {
			BleachEventUser user = users.get(i);
			if(i<10) {
				top10users = top10users + (i==0?"":"\n") + rankingLine(user,i,order);
			}
			if(user.getUser().equals(member.getId().asString())) found = i;
			i++;
		}
		
		Container c = null;

		if(found < 10) {
			c = Container.of(
				Color.BLACK,
				TextDisplay.of("# Shinigami Leaderboard"),
				TextDisplay.of(top10users)
			);
		}else {
			c = Container.of(
					Color.BLACK,
					TextDisplay.of("# Shinigami Leaderboard"),
					TextDisplay.of(top10users),
					Separator.of(true, SpacingSize.of(1)),
					TextDisplay.of(rankingLine(users.get(found),found,order))
				);
		}
		
		return c;
	}
	
	
	private String medal(int i) {
		String medal = "" + (i+1) + ".";
		switch(i) {
			case 0: medal = "🥇"; break;
			case 1: medal = "🥈"; break;
			case 2: medal = "🥉"; break;
		}
		return medal;
	}
	
	private String rankingLine(BleachEventUser user, int position, String type) {
		if(type.equals("healing")) {
			return medal(position) + " <@" + user.getUser() + "> — " + (user.getHealings() + user.getFullHealings()) + " healings";
		}
		return medal(position) + " <@" + user.getUser() + "> — " + user.getReiatsu() + " reiatsu";
		
	}
	
}
