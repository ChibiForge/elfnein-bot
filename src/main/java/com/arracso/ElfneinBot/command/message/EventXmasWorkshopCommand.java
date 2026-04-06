package com.arracso.ElfneinBot.command.message;

import java.time.LocalDate;
import java.util.List;

import com.arracso.ElfneinBot.model.EventXmasUser;
import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

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

public class EventXmasWorkshopCommand extends MessageCommand {
	
	public EventXmasWorkshopCommand(){
		commandNames.add("workshop");
		commandNames.add("ws");
		commandId = Global.eventXmasBase;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve user id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();
		
		return message.getAuthorAsMember().flatMap(member -> {
			List<String> parameter = getParameters(message);
			
			if(parameter.size()==0)	{
				if(!Service.eventXmasService.isUserInEvent(member.getId().asString()))
					return Util.replyToMessage(message,"You don't have a workshop yet!\n-# Get some materials by droping cards to adquire your workshop.").then();
				return Util.replyToMessageSilent(message,workshopContainer(member)).then();
			} else if(parameter.get(0).toLowerCase().equals("of")) {
				if(parameter.size()<2) return Util.replyToMessage(message,"Your have to specify a user.").then();
				String targetId = parseId(parameter.get(1));
				if(!Service.eventXmasService.isUserInEvent(targetId)) {
					return Util.replyToMessage(message,"This user doesn't have a workshop yet.").then();
				}
				return member.getGuild().flatMap(guild -> 
					guild.getMemberById(Snowflake.of(targetId)).flatMap(targetMember -> 
						Util.replyToMessageSilent(message,workshopContainer(targetMember)).then()
					)
				);
				
			} else if(parameter.get(0).toLowerCase().equals("craft")) {
				LocalDate now = LocalDate.now();
				if(now.getMonthValue() != 12 && (now.getMonthValue() != 1 || now.getDayOfMonth() > 5))
					return Util.replyToMessage(message,"The workshop is closed. You cannot craft toys till next xmas.").then();
				
				if(!Service.eventXmasService.isUserInEvent(member.getId().asString()))
					return Util.replyToMessage(message,"You don't have a workshop yet!\n-# Get some materials by droping cards to adquire your workshop.").then();
				EventXmasUser user = Service.eventXmasService.getUser(member.getId().asString());
				if(parameter.size()<2) return Util.replyToMessage(message, craftOptionsContainer()).then();
				if(parameter.get(1).toLowerCase().equals("view")) {
					if(user.getCraftingToy() == 0) {
						return Util.replyToMessage(message,"You are not crafting any toy at the moment.").then();
					} else {
						if(message.getChannelId().asString().equals("1326619189468729466")) return Mono.empty();
						return Util.replyToMessage(message, viewCraftingToy(user)).then();
					}
				}
				else if(parameter.get(1).toLowerCase().equals("car")) return Util.replyToMessage(message,startCraftingToy(user.getUser(),"car")).then();
				else if(parameter.get(1).toLowerCase().equals("doll")) return Util.replyToMessage(message,startCraftingToy(user.getUser(),"doll")).then();
				else if(parameter.get(1).toLowerCase().equals("marble")) return Util.replyToMessage(message,startCraftingToy(user.getUser(),"marble")).then();
				
			} else if(parameter.get(0).toLowerCase().equals("ranking") || parameter.get(0).toLowerCase().equals("lb")) {
				if(!Service.eventXmasService.isUserInEvent(member.getId().asString()))
					return Util.replyToMessage(message,"You don't have a workshop yet!\n-# Get some materials by droping cards to adquire your workshop.").then();
				String order = parameter.size()>1 ? parameter.get(1) : "";
				return Util.replyToMessageSilent(message,rankingContainer(member,order)).then();
			} else if(parameter.get(0).toLowerCase().equals("casino")) {
				LocalDate now = LocalDate.now();
				if(now.getMonthValue() != 12 && (now.getMonthValue() != 1 || now.getDayOfMonth() > 5))
					return Util.replyToMessage(message,"The casino is closed!").then();
				
				if(message.getChannelId().asString().equals("1326619189468729466")) return Mono.empty();
				if(!Service.eventXmasService.isUserInEvent(member.getId().asString()))
					return Util.replyToMessage(message,"You don't have a workshop yet!\n-# Get some materials by droping cards to adquire your workshop.").then();
				return goToCasino(message,member.getId().asString(),parameter);
			}
			return Util.replyToMessage(message,"Invalid usage\n-# Valid usages are: `workshop`, `workshop craft`, `workshop ranking`, and `workshop of [userId]`.").then();
			
		});
		
	}
	
	private Mono<Void> goToCasino(Message message, String userId, List<String> parameter) {
		String cfrules = "## Coin Flip\n" +
			"Place your bet, call the outcome, and let fate decide.\n" +
			"**Odds**\n" +
			"> -# Heads/Tails → 2:1\n" +
			"> -# Side → 45:1\n" + //48:1
			"**Probabilities**\n" +
			"> -# Heads → 49%\n" +
			"> -# Tails → 49%\n" +
			"> -# Side → 2%\n" +
			"**How to play**\n" +
			"> -# `e.workshop casino coinflip [heads|tails|side] AMOUNT`";
		if(parameter.size()==1) return Util.replyToMessage(message, 
			"# <:grinch:1454979532825297120> Grinch Casino <:grinch:1454979532825297120>\n" + 
			"Welcome to the **Grinch Casino** — risk it all, or walk away poorer.\n" + 
			cfrules
		).then();
		
		if(parameter.get(1).toLowerCase().equals("coinflip") || parameter.get(1).toLowerCase().equals("cf")) {
			if(parameter.size() == 4) {
				try { return Util.replyToMessage(message, Service.eventXmasService.casinoCF(userId,parameter.get(2),Integer.parseInt(parameter.get(3)))).then();} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			return Util.replyToMessage(message, cfrules).then();
		}
		
		return Util.replyToMessage(message,"Invalid game\n-# Valid games are: `coinflip`.").then();
	}

	private String parseId(String par) {
		String id = par;
		if(par.startsWith("<@")) id = par.split("@")[1].split(">")[0];
		return id;
	}
	
	private Container workshopContainer(Member member) {
		EventXmasUser user = Service.eventXmasService.getUser(member.getId().asString());
		
		Section userInfo = Section.of(
			Thumbnail.of(UnfurledMediaItem.of(member.getAvatarUrl())),
			TextDisplay.of("# Workshop of **" + member.getDisplayName() + "**"),
			TextDisplay.of(
				"🪙 Gold coins — ** " + user.getCoins() + "**\n" +
				"🛠️ Crafted toys — ** " + user.getCraftedToys() + "**"
			)
		);
		
		Container workshopContainer = Container.of(
			Color.GREEN,
			userInfo
		);
		
		TextDisplay workshopUtilities = user.getTotalUtilities() == 0? null : TextDisplay.of(
			(user.getWood() >0 ? "🪵 Wooden log — **" + user.getWood() + "**\n" : "") +
			(user.getPaint()>0 ? "💧 Paint bottle — **" + user.getPaint() + "**\n" : "")
		);
		
		if(workshopUtilities != null) {
			workshopContainer = workshopContainer.withAddedComponents(
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("## ✦ Utilities"),
				workshopUtilities
			);
		}
	
		return workshopContainer;		
	}
	

	private Container craftOptionsContainer() {
		Container craftContainer = Container.of(
				Color.GREEN,
				TextDisplay.of("# 🎄 Xmas workshop 🎄"),
				Separator.of(true, SpacingSize.of(2)),
				Section.of(
					Thumbnail.of(UnfurledMediaItem.of("https://i.postimg.cc/QdcHvhvR/car.png")),
					TextDisplay.of("## Wooden car"),
					//TextDisplay.of("*A car made out of wood by an elf.*"),
					TextDisplay.of("-# **Materials:** 1 wooden log\n-# **Time needed:** " + Service.eventXmasService.getCraftDuration(1) + " minutes\n-# **Craft cmd:** `e.workshop craft car`") // 2 coin
				),
				Separator.of(false, SpacingSize.of(1)),
				Section.of(
					Thumbnail.of(UnfurledMediaItem.of("https://i.postimg.cc/wjD7ngnP/doll.png")),
					TextDisplay.of("## Wooden doll"),
					//TextDisplay.of("*A doll made out of wood by an elf.*"),
					TextDisplay.of("-# **Materials:** 1 wooden log + 2 paint bottles\n-# **Time needed:** " + Service.eventXmasService.getCraftDuration(2) + " minutes\n-# **Craft cmd:** `e.workshop craft doll`") // 12 coins
				),
				Separator.of(false, SpacingSize.of(1)),
				Section.of(
					Thumbnail.of(UnfurledMediaItem.of("https://i.postimg.cc/XY9pt4tt/marble.png")),
					TextDisplay.of("## Wooden marble run"),
					//TextDisplay.of("*A marble run made out of wood by an elf.*"),
					TextDisplay.of("-# **Materials:** 2 wooden logs + 1 paint bottle\n-# **Time needed:** " + Service.eventXmasService.getCraftDuration(3) + " minutes\n-# **Craft cmd:** `e.workshop craft marble`") // 8 coins
				)
			);
			
			return craftContainer;	
	}
	
	private Container viewCraftingToy(EventXmasUser user) {
		String toy = user.getCraftingToy() == 1? "wooden car" :(user.getCraftingToy() == 2? "wooden doll":"woodem marble run");
		Integer duration = Service.eventXmasService.getCraftDuration(user.getCraftingToy());
		
		Container viewCraftContainer = Container.of(
				Color.GREEN,
				Section.of(
					user.getCraftingToy() == 1?
						Thumbnail.of(UnfurledMediaItem.of("https://i.postimg.cc/QdcHvhvR/car.png"))
					:(user.getCraftingToy() == 2?
						Thumbnail.of(UnfurledMediaItem.of("https://i.postimg.cc/wjD7ngnP/doll.png"))
					:
						Thumbnail.of(UnfurledMediaItem.of("https://i.postimg.cc/XY9pt4tt/marble.png"))
					),
					TextDisplay.of("# 🎄 Xmas workshop 🎄"),
					TextDisplay.of("*You are crafting a " + toy + "*"),
					TextDisplay.of("-# **Progression:** " + user.getCraftingProgression() + "/" + duration) // 2 coin
				)
			);
			
			return viewCraftContainer;	
	}
	
	private String startCraftingToy(String userId, String toy) {
		Integer result = Service.eventXmasService.startCraftingToy(userId,toy);
		if(result == 0) return "You are already crafting a toy.";
		else if(result == 1) return "You started crafting a wooden car.";
		else if(result == 2) return "You started crafting a wooden doll.";
		else if(result == 3) return "You started crafting a wooden marble run.";
		return "You don't have enought materials to craft this toy.";
	}
	
	private Container rankingContainer(Member member, String order) {
		List<EventXmasUser> users = Service.eventXmasService.getUsers(order);
		
		String top10users = "";
		Integer found = 0;
		int i = 0;
		while(i<users.size() && (found == 0 || i<10)) {
			EventXmasUser user = users.get(i);
			if(i<10) {
				top10users = top10users + (i==0?"":"\n") + rankingLine(user,i,order);
			}
			if(user.getUser().equals(member.getId().asString())) found = i;
			i++;
		}
		
		Container c = Container.of(
				Color.GREEN,
				TextDisplay.of("# Workshops Leaderboard"),
				TextDisplay.of(top10users)
			);;

		if(found >= 10) {
			c = c.withAddedComponents(
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
	
	private String rankingLine(EventXmasUser user, int position, String type) {
		if(type.equals("coins")) {
			return medal(position) + " <@" + user.getUser() + "> — " + user.getCoins() + " gold coins";
		}
		
		return medal(position) + " <@" + user.getUser() + "> — " + user.getCoins() + " gold coins";
	}
}
