package com.arracso.ElfneinBot;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.arracso.ElfneinBot.listener.EventListener;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;


@Configuration
public class BotConfiguration {

    @Value("${discord.bot.token}")
    private String token;

    @Bean
    <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListeners) {
    	GatewayDiscordClient client = DiscordClientBuilder.create(token)
          .build()
          .login()
          .block();
        
        for(EventListener<T> listener : eventListeners) {
            client.on(listener.getEventType())
              .flatMap(listener::execute)
              .onErrorResume(listener::handleError)
              .subscribe();
        }
        
        //client.onDisconnect().block();
        
        return client;
    }
}