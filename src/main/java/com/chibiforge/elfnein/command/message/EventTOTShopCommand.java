package com.chibiforge.elfnein.command.message;

import java.util.List;

import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.core.object.component.Container;
import discord4j.core.object.component.Section;
import discord4j.core.object.component.Separator;
import discord4j.core.object.component.Separator.SpacingSize;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.component.Thumbnail;
import discord4j.core.object.component.UnfurledMediaItem;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class EventTOTShopCommand extends MessageCommand {
	
	public EventTOTShopCommand(){
		commandNames.add("shop");
		commandId = Global.eventTOTBase;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve user id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();
		
		if(!message.getGuildId().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve guild id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();		
		
		return message.getAuthorAsMember().flatMap(member -> {
			if(!Service.eventTOTService.isUserInEvent(member.getGuildId().asString(),member.getId().asString())) {
				return Util.replyToMessage(message,"You are not a member of a house yet!\n-# Ask someone to invite you to their house doing `e.house invite` or do `e.house create` to create your own.").then();
			}
			
			List<String> parameter = getParameters(message);
			if(parameter.size()==0)	{
				return Util.replyToMessageSilent(message,shopContainer()).then();
			}else if(parameter.size()>0 && parameter.get(0).toLowerCase().equals("buy")) {
				String item = parameter.get(1);
				String result = Service.eventTOTService.buy(member.getGuildId().asString(), member.getId().asString(), item);
				return Util.replyToMessage(message,result).then();
			}
			
			return Util.replyToMessage(message,"Invalid command\n-# Valid shop commands are: `shop` and `shop buy`.").then();
			
		});
		
	}
	
	private Container shopContainer() {
		Container shopContainer = Container.of(
			Color.BLACK,
			TextDisplay.of("# 🎃 Halloween shop 🎃"),
			Separator.of(true, SpacingSize.of(2)),
			Section.of(
				Thumbnail.of(UnfurledMediaItem.of("https://i.postimg.cc/DzdbpX9d/candy.png")),
				TextDisplay.of("## Bag of candies"),
				TextDisplay.of("*A bag of candies containing 12 candies.*"),
				TextDisplay.of("-# **Price:** 1 coin\n-# **Buy cmd:** `e.shop buy candy`")
			),
			Separator.of(false, SpacingSize.of(1)),
			Section.of(
				Thumbnail.of(UnfurledMediaItem.of("https://i.postimg.cc/Bvc17KrH/egg.jpg")),
				TextDisplay.of("## Egg carton"),
				TextDisplay.of("*An Egg Carton containing 6 eggs.*"),
				TextDisplay.of("-# **Price:** 1 coin\n-# **Buy cmd:** `e.shop buy eggs`")
			),
			Separator.of(false, SpacingSize.of(1)),
			Section.of(
				Thumbnail.of(UnfurledMediaItem.of("https://i.postimg.cc/GpJsgyWv/rolls.jpg")),
				TextDisplay.of("## Toilet paper roll pack"),
				TextDisplay.of("*A pack of 4 toilet paper rolls.*"),
				TextDisplay.of("-# **Price:** 1 coin\n-# **Buy cmd:** `e.shop buy rolls`")
			)
		);
		
		return shopContainer;		
	}
	
}
