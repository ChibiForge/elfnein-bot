package com.arracso.ElfneinBot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arracso.ElfneinBot.service.CommandService;

import jakarta.annotation.PostConstruct;

@Component
public class Service {

	public static CommandService commandService;
	
	@Autowired
	private CommandService commandServiceWired;
	
	@PostConstruct
	private void initStaticDao() {
		commandService = this.commandServiceWired;
	}
	
}
