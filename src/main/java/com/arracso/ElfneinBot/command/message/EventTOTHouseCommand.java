package com.arracso.ElfneinBot.command.message;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.arracso.ElfneinBot.model.EventTOTHouse;
import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.Container;
import discord4j.core.object.component.MediaGallery;
import discord4j.core.object.component.MediaGalleryItem;
import discord4j.core.object.component.Section;
import discord4j.core.object.component.Separator;
import discord4j.core.object.component.Separator.SpacingSize;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.component.Thumbnail;
import discord4j.core.object.component.UnfurledMediaItem;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class EventTOTHouseCommand extends MessageCommand {
	
	public EventTOTHouseCommand(){
		commandNames.add("house");
		commandNames.add("home");
		commandId = Global.eventTOTBase;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve user id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();
		
		if(!message.getGuildId().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve guild id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();		
		
		return message.getAuthorAsMember().flatMap(member -> {
			List<String> parameter = getParameters(message);
			
			if(parameter.size()==0)	{
				if(!Service.eventTOTService.isUserInEvent(member.getGuildId().asString(),member.getId().asString())) {
					return Util.replyToMessage(message,"You are not a member of a house yet!\n-# Ask someone to invite you to their house doing `e.house invite` or do `e.house create` to create your own.").then();
				}
				return Util.replyToMessageSilent(message,houseContainer(member)).then();
			}else if(parameter.size()>0 && parameter.get(0).toLowerCase().equals("create")) {
				if(Service.eventTOTService.isUserInEvent(member.getGuildId().asString(),member.getId().asString())) {
					return Util.replyToMessage(message,"You are already a member of a house!").then();
				}
				Service.eventTOTService.registerHouse(member.getGuildId().asString(), member.getId().asString(), (int)(Math.random()*18+1));
				return Util.replyToMessage(message,"You created your own house!\n-# Check your house with `e.house`.").then();
			}else if(parameter.get(0).toLowerCase().equals("invite")) {
				if(!Service.eventTOTService.isUserInEvent(member.getGuildId().asString(),member.getId().asString()))
					return Util.replyToMessage(message,"You are not a member of a house yet!\n-# Ask someone to invite you to their house doing `e.house invite` or do `e.house create` to create your own.").then();
				if(Service.eventTOTService.houseIsFull(member.getGuildId().asString(),member.getId().asString()))
					return Util.replyToMessage(message,"Your house is at full capacity.").then();
				if(parameter.size()<2) 
					return Util.replyToMessage(message,"You have to specify a user.").then();
				String targetId = parseId(parameter.get(1));
				if(Service.eventTOTService.isUserInEvent(member.getGuildId().asString(),targetId)) {
					return Util.replyToMessage(message,"This user is already a member of a house.").then();
				}
				return Util.replyToMessage(message, inviteContainer(member.getGuildId().asString(),member.getId().asString(),targetId,"ask")).flatMap(msg -> inviteListener(msg));
			}else if(parameter.get(0).toLowerCase().equals("clean")) {
				if(!Service.eventTOTService.isUserInEvent(member.getGuildId().asString(),member.getId().asString())) {
					return Util.replyToMessage(message,"You are not a member of a house yet!\n-# Ask someone to invite you to their house doing `e.house invite` or do `e.house create` to create your own.").then();
				}
				String cleanResult = Service.eventTOTService.cleanHouse(member.getGuildId().asString(),member.getId().asString());
				return Util.replyToMessage(message, cleanResult).then();
			}else if(parameter.get(0).toLowerCase().equals("of")) {
				if(parameter.size()<2) return Util.replyToMessage(message,"Your have to specify a user.").then();
				String targetId = parseId(parameter.get(1));
				if(!Service.eventTOTService.isUserInEvent(member.getGuildId().asString(),targetId)) {
					return Util.replyToMessage(message,"This user is not a member of a house yet!\n-# You can invite them to your house doing `e.house invite`.").then();
				}
				return member.getGuild().flatMap(guild -> 
					guild.getMemberById(Snowflake.of(targetId)).flatMap(targetMember -> 
						Util.replyToMessageSilent(message,houseContainer(targetMember)).then()
					)
				);
				
			}else if(parameter.get(0).toLowerCase().equals("lb")) {
				if(!Service.eventTOTService.isUserInEvent(member.getGuildId().asString(),member.getId().asString())) {
					return Util.replyToMessage(message,"You are not a member of a house yet!\n-# Ask someone to invite you to their house doing `e.house invite` or do `e.house create` to create your own.").then();
				}
				String order = parameter.size()>1 ? parameter.get(1) : "";
				return member.getGuild().flatMap(guild -> Util.replyToMessageSilent(message,rankingContainer(guild,member,order)).then());
			}
			
			return Util.replyToMessage(message,"Invalid command\n-# Valid house commands are: `create`, `invite` and `clean`.").then();
			
		});
		
	}

	private String parseId(String par) {
		String id = par;
		if(par.startsWith("<@")) id = par.split("@")[1].split(">")[0];
		return id;
	}
	
	private static String [] gridURLs = {
		"https://i.postimg.cc/44KVsdh3/HexGrid0.png",
		"https://i.postimg.cc/kMVKq4tD/HexGrid1.png",
		"https://i.postimg.cc/tR1WpTVs/HexGrid2.png",
		"https://i.postimg.cc/7PC0w6TJ/HexGrid3.png",
		"https://i.postimg.cc/L4qL2XZf/HexGrid4.png",
		"https://i.postimg.cc/PfLYTx8m/HexGrid5.png",
		"https://i.postimg.cc/qgVkdQ6Q/HexGrid6.png",
		"https://i.postimg.cc/fknwh5S8/HexGrid7.png",
		"https://i.postimg.cc/23RzfTL2/HexGrid8.png",
		"https://i.postimg.cc/dDPqKnZ6/HexGrid9.png",
		"https://i.postimg.cc/G9wc1zTg/Hex-Grid10.png",
		"https://i.postimg.cc/PNstkKvF/Hex-Grid11.png",
		"https://i.postimg.cc/zDLK8BHs/Hex-Grid12.png",
		"https://i.postimg.cc/2jqhr6LP/Hex-Grid13.png",
		"https://i.postimg.cc/DfWLnZb3/Hex-Grid14.png",
		"https://i.postimg.cc/BZ8H461s/Hex-Grid15.png",
		"https://i.postimg.cc/jqWPt5Jt/Hex-Grid16.png",
		"https://i.postimg.cc/44KVsdhf/Hex-Grid17.png",
		"https://i.postimg.cc/xjkMnCbT/Hex-Grid18.png"
	};
	
	private static String [] houseURLs = {
		"https://i.postimg.cc/Twwv8nK9/house0.jpg",
		"https://i.postimg.cc/LsvF0x8g/house1.jpg",
		"https://i.postimg.cc/RFFxrwWf/house2.jpg",
		"https://i.postimg.cc/Gpb1QWdd/house3.jpg",
		"https://i.postimg.cc/yddCq0Dy/house4.jpg",
		"https://i.postimg.cc/KzzhXt1f/house5.jpg",
		"https://i.postimg.cc/qRRHf2NQ/house6.jpg",
		"https://i.postimg.cc/pXp8VdhN/house7.jpg",
		"https://i.postimg.cc/G22n0v4f/house8.jpg",
		"https://i.postimg.cc/RFFxrwWj/house9.jpg",
		"https://i.postimg.cc/9MMH3yD6/house10.jpg",
		"https://i.postimg.cc/gJJb93wb/house11.jpg",
		"https://i.postimg.cc/DZMk7mGD/house12.jpg",
		"https://i.postimg.cc/W3HLstrL/house13.jpg",
		"https://i.postimg.cc/HxhDTj5d/house14.jpg",
		"https://i.postimg.cc/sXNd31Sv/house15.jpg",
		"https://i.postimg.cc/wMRKMHc6/house16.jpg",
		"https://i.postimg.cc/4yHCyZbd/house17.jpg",
		"https://i.postimg.cc/pT6bPy8m/house18.jpg",
		"https://i.postimg.cc/tCYPRg1K/house19.jpg"
	};
	
	private Container houseContainer(Member member) {
		EventTOTHouse house = Service.eventTOTService.getHouse(member.getGuildId().asString(),member.getId().asString());
		
		Section houseInfo = Section.of(
			Thumbnail.of(UnfurledMediaItem.of(gridURLs[house.getPosition()])),
			TextDisplay.of("-# **Members of the house:**"),
			TextDisplay.of("-# ✦ <@" + house.getMembers().replace(",", ">\n-# ✦ <@") + ">")
		);
		
		Container homeContainer = Container.of(
			Color.BLACK,
			TextDisplay.of("# House of **" + member.getDisplayName() + "**"),
			houseInfo,
			Separator.of(true, SpacingSize.of(2)),
			MediaGallery.of(MediaGalleryItem.of(UnfurledMediaItem.of(houseURLs[house.getPosition()])))
		);

		if (house.getDirtyState() == 1) {
			Timestamp endDirty = house.getDirtyEnd();
			if(endDirty.after(Timestamp.from(Instant.now())))
				homeContainer = homeContainer.withAddedComponent(TextDisplay.of("*This house is being cleaned 🧹*\nTime till fully cleaned: <t:" + endDirty.getTime()/1000 + ":R>"));
		}
		if (house.getDirtyState() == 2)
		    homeContainer = homeContainer.withAddedComponent(TextDisplay.of("*There’s an egg splattered on the window 🥚*"));
		if (house.getDirtyState() == 3)
		    homeContainer = homeContainer.withAddedComponent(TextDisplay.of("*There’s toilet paper everywhere 🧻*"));
		
		TextDisplay homeInventory = house.getTotalInventory() == 0? null : TextDisplay.of(
				(house.getCoins()>0 ? "🪙 Coins — **" + house.getCoins() + "**\n" : "") +
				(house.getCandy()>0 ? "🍬 Candy — **" + house.getCandy() + "**\n" : "") +
			(house.getEggs()>0 ? "🥚 Eggs — **" + house.getEggs() + "**\n" : "") +
			(house.getToiletPaperRolls()>0 ? "🧻 Toilet Paper Rolls — **" + house.getToiletPaperRolls() + "**\n" : "")
		);
		
		if(homeInventory != null) {
			homeContainer = homeContainer.withAddedComponents(
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("## ✦ Inventory"),
				homeInventory
			);
		}
		
		TextDisplay homeTrash = house.getTotalTrash() == 0? null : TextDisplay.of(
				(house.getCandyWrappers()>0 ? "🎀 Candy Wrappers — **" + house.getCandyWrappers() + "**\n" : "") +
				(house.getEggshells()>0 ? "💥 Eggshells — **" + house.getEggshells() + "**\n" : "") +
				(house.getToiletPaperScraps()>0 ? "🚽 Toilet Paper Scraps — **" + house.getToiletPaperScraps() + "**\n" : "")
		);
		
		if(homeTrash != null) {
			homeContainer = homeContainer.withAddedComponents(
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("## ✦ Trash Can"),
				homeTrash
			);
		}
		
		TextDisplay homeActivities = house.getTotalActions() == 0? null : TextDisplay.of(
				(house.getCandyGiven()>0 ? "🎁 Candy Given — **" + house.getCandyGiven() + "**\n" : "") +
				(house.getEggThrows()>0 ? "☄️ Eggs Thrown — **" + house.getEggThrows() + "**\n" : "") +
				(house.getToiletPaperRollThrows()>0 ? "🎯 Toilet Paper Rolls Thrown — **" + house.getToiletPaperRollThrows() + "**\n" : "")
		);
		
		if(homeActivities != null) {
			homeContainer = homeContainer.withAddedComponents(
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("## ✦ Member Activities"),
				homeActivities
			);
		}
	
		return homeContainer;		
	}
	
	////////////
	// Invite //
	////////////
	
	private Container inviteContainer(String serverId, String userId, String targetId, String result) {
		Container c = Container.of(
			TextDisplay.of("# House Invite"),
			TextDisplay.of("<@" + targetId + "> you are getting invited to join the household of <@" + userId + ">."),
			Separator.of(true, SpacingSize.of(2)),
			TextDisplay.of("Do you accept the invitation?"),
			result.equals("no") ?
				ActionRow.of(Button.danger("tot:invite:no", "No").disabled(), Button.secondary("tot:invite:yes", "Yes").disabled())
			: ( result.equals("yes") ?
				ActionRow.of(Button.secondary("tot:invite:no", "No").disabled(), Button.success("tot:invite:yes", "Yes").disabled())
			: 
				ActionRow.of(Button.secondary("tot:invite:no:inviter="+userId+"&user="+targetId, "No"), Button.secondary("tot:invite:yes:inviter="+userId+"&user="+targetId, "Yes"))
			)	
		);
		
		if(result.equals("yes")) 
			c = c.withAddedComponents(
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("-# <@" + targetId + "> accepted the invitation.")
			);
		
		if(result.equals("no"))
			c = c.withAddedComponents(
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("-# <@" + targetId + "> rejected the invitation.")
			);
		
		return c;
	}
	
	private Mono<Void> inviteListener(Message msg) {
		return Service.client.on(ButtonInteractionEvent.class)
			.filter(e-> e.getMessageId().asString().equals(msg.getId().asString()))
			.filter(e -> e.getCustomId().startsWith("tot:invite:"))
			.flatMap(e -> {
				String userId = e.getUser().getId().asString();
				String [] args = e.getCustomId().split(":");
				Map<String,String> metadata = Util.parseButtonMetadata(args[3]);
				if(!metadata.get("user").equals(userId)) return e.reply().withContent("You cannot do that!").withEphemeral(true);
				Snowflake guildId = e.getInteraction().getGuildId().orElse(null);
				if(Service.eventTOTService.isUserInEvent(guildId.asString(), userId)) return e.reply().withContent("You already joined another house!").withEphemeral(true);
				
				String result = args[2];
				String inviterId = metadata.get("inviter");
				
				if(result.equals("yes")) Service.eventTOTService.joinHouse(guildId.asString(),inviterId,userId);
			
				return e.edit().withComponents(inviteContainer(guildId.asString(),inviterId,userId,result)).then();		
			})
			.timeout(Duration.ofMinutes(1))
			.onErrorResume(TimeoutException.class, e -> Mono.empty())
			.then();
	}
	
	/////////////
	// RANKING //
	/////////////
	
	private Container rankingContainer(Guild guild, Member member, String order) {
		List<EventTOTHouse> houses = Service.eventTOTService.getHouses(member.getGuildId().asString(),order);
		
		String top10houses = "";
		Integer found = 0;
		int i = 0;
		while(i<houses.size() && (found == 0 || i<10)) {
			EventTOTHouse house = houses.get(i);
			if(i<10) {
				top10houses = top10houses + (i==0?"":"\n") + rankingLine(house,i,order);
			}
			if(house.getMembers().contains(member.getId().asString())) found = i;
			i++;
		}
		
		Container c = Container.of(
				Color.BLACK,
				TextDisplay.of("# Houses Leaderboard"),
				TextDisplay.of(top10houses)
			);;

		if(found >= 10) {
			c = c.withAddedComponents(
				Separator.of(true, SpacingSize.of(1)),
				TextDisplay.of(rankingLine(houses.get(found),found,order))
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
	
	private String rankingLine(EventTOTHouse house, int position, String type) {
		if(type.equals("wrappers")) {
			return medal(position) + " <@" + house.getMembers().replace(",", ">|<@") + "> — " + house.getCandyWrappers() + " candy wrappers";
		}else if(type.equals("tricks")) {
			return medal(position) + " <@" + house.getMembers().replace(",", ">|<@") + "> — " + (house.getEggThrows() + house.getToiletPaperRollThrows()) + " tricks";
		}else if(type.equals("treats")) {
			return medal(position) + " <@" + house.getMembers().replace(",", ">|<@") + "> — " +  house.getCandyGiven() + " candy given";
		}else if(type.equals("cleaning")) {
			return medal(position) + " <@" + house.getMembers().replace(",", ">|<@") + "> — " + house.getDirtyTime() + " minutes dirty";
		}
		return medal(position) + " <@" + house.getMembers().replace(",", ">|<@") + "> — " + house.getCandyWrappers() + " candy wrappers, " + (house.getEggThrows() + house.getToiletPaperRollThrows()) + " tricks, " +  house.getCandyGiven() + " candy given, " + house.getDirtyTime() + " minutes dirty";
	}
	
}
