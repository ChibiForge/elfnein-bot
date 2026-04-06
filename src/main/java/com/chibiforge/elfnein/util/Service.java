package com.chibiforge.elfnein.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chibiforge.elfnein.service.BleachEventService;
import com.chibiforge.elfnein.service.CommandService;
import com.chibiforge.elfnein.service.EventTOTService;
import com.chibiforge.elfnein.service.EventXmasService;
import com.chibiforge.elfnein.service.KarutaService;
import com.chibiforge.elfnein.service.UserInventoryService;
import com.chibiforge.elfnein.service.UserService;

import discord4j.core.GatewayDiscordClient;
import jakarta.annotation.PostConstruct;

@Component
public class Service {

	public static CommandService commandService;
	public static UserService userService;
	public static KarutaService karutaService;
	public static GatewayDiscordClient client;
	public static Properties properties;
	public static PaginationService paginationService;
	public static BleachEventService bleachEventService;
	public static EventTOTService eventTOTService;
	public static EventXmasService eventXmasService;
	public static UserInventoryService userInventoryService;
	
	@Autowired
	private CommandService commandServiceWired;
	
	@Autowired
	private UserService userServiceWired;
	
	@Autowired
	private KarutaService karutaServiceWired;
	
	@Autowired
	private GatewayDiscordClient clientWired;
	
	@Autowired
	Properties propertiesWired;
	
	@Autowired
    private PaginationService paginationServiceWired;
	
	@Autowired
    private BleachEventService bleachEventServiceWired;
	
	@Autowired
    private EventTOTService eventTOTServiceWired;
	
	@Autowired
    private EventXmasService eventXmasServiceWired;
	
	@Autowired
	private UserInventoryService userInventoryServiceWired;
	
	@PostConstruct
	private void initStaticDao() {
		commandService = this.commandServiceWired;
		userService = this.userServiceWired;
		karutaService = this.karutaServiceWired;
		client = this.clientWired;
		properties = this.propertiesWired;
		paginationService = this.paginationServiceWired;
		bleachEventService = this.bleachEventServiceWired;
		eventTOTService = this.eventTOTServiceWired;
		eventXmasService = this.eventXmasServiceWired;
		userInventoryService = this.userInventoryServiceWired;
	}
	
}
