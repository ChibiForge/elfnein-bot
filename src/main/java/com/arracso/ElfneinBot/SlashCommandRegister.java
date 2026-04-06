package com.arracso.ElfneinBot;

import java.util.List;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import jakarta.annotation.PostConstruct;

//@Component
public class SlashCommandRegister {
	 private final GatewayDiscordClient client;
	 
	public SlashCommandRegister(GatewayDiscordClient client) {
		this.client = client;
	}
	
	@PostConstruct
	public void registerCommands() {
		long applicationId = client.getRestClient().getApplicationId().block();
		
		List<ApplicationCommandRequest> commands = List.of(
			ApplicationCommandRequest.builder()
				.name("ping")
				.description("Replies with pong")
				.build(),
			ApplicationCommandRequest.builder()
				.name("checkperms")
				.description("Check a user's guild permissions")
				.addOption(
					ApplicationCommandOptionData.builder()
					.name("user")
					.description("User to check (default: yourself)")
					.type(ApplicationCommandOption.Type.USER.getValue())
					.required(false)
					.build()
				)
				.build()
		);
		    
		///////client.getRestClient().getApplicationService().createGlobalApplicationCommand(applicationId, pingCommand).subscribe();
	    
		client.getRestClient().getApplicationService().bulkOverwriteGlobalApplicationCommand(applicationId,commands).subscribe();
	}
}
