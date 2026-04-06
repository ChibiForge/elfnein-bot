package com.chibiforge.elfnein.controller;

import discord4j.core.object.entity.Message;
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
import reactor.core.publisher.Mono;

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
		@RequestParam("message") String msg,
		@RequestParam("channelId") String channelID,
		@RequestParam(name = "replyMessageId", defaultValue="") String replyMsgId
	) {
		
		Mono<? extends Message> action;
		
		if(replyMsgId.isBlank())
			action = client.getChannelById(Snowflake.of(channelID)).ofType(MessageChannel.class)
			.flatMap(channel -> channel.createMessage(msg)); // TODO check for errors
		else
			action = client.getChannelById(Snowflake.of(channelID)).ofType(MessageChannel.class)
			.flatMap(channel -> channel.createMessage(msg)); // TODO add reply
		
		action.subscribe();
		
		return "OK";
	}
}