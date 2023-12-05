package com.arracso.ElfneinBot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping(value = "/test")
public class TestController {
	
	@Autowired
	GatewayDiscordClient client;
	
	@GetMapping("/ok")
	public @ResponseBody String ok() { return "OK"; }
	

	@PostMapping("/say")
	public @ResponseBody String say(
		@RequestParam("message") String message,
		@RequestParam("channelId") String channelID,
		@RequestParam(name = "replyMessageId", defaultValue="") String replyMessageId
	) {
		if(replyMessageId.isBlank())
			client.getChannelById(Snowflake.of(channelID)).ofType(MessageChannel.class)
			.flatMap(channel -> channel.createMessage(message)).subscribe();
		else
			client.getChannelById(Snowflake.of(channelID)).ofType(MessageChannel.class)
			.flatMap(channel -> channel.createMessage(message).withMessageReference(Snowflake.of(replyMessageId))).subscribe();
		
		return "OK";
	}
}