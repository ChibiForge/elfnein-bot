package com.arracso.ElfneinBot.listener;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.arracso.ElfneinBot.command.message.*;
import com.arracso.ElfneinBot.util.Global;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MessageCreateListener implements EventListener<MessageCreateEvent> {
	
	@SuppressWarnings("unused")
	private static final Set<MessageCommand> commandsSolve = Set.of(
		// Solve commands - ID 1
		new KarutaDateCommand(),
		new KarutaSolveDateCommand(),
		new KarutaMapDateCommand()
	);
	
	private static final Set<MessageCommand> commandsGeneral = Set.of(
		// Base Commands - ID 0
		new HelpCommand(),
		new PingCommand("test"),
		// Admin commands - ID +100
		new ListServersCommand(),
		new DropServerCommand(),
		new GetInviteServerCommand(),
		new GetServerInfoCommand(),
		new SetPermissionCommand(),
		new AdmCommand(),
		// Inventory commands
		new CoinsCommand(),
		new InventoryCommand(),
		// Event
		new EventCommand(),
		// Shogun commands - ID 4 & +40
		new KarutaLoopTimerCommand(),
		new KarutaLoopCommand(),
		new KarutaNodesCommand(),
		new KarutaSetShogunCommand(),
		new KarutaSetNodeCommand(),
		// Data collection
		new NodeDataCommand(),
		// Utils - ID + 100
		new SayCommand(),
		new MathCommand(),
		new RepeatCommand(),
		// Activity
		new RankingCommand()
	);
	
	private static final Set<MessageCommand> commands = commandsGeneral;
	
    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }
    
    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.just(event.getMessage())
        	.flatMap(message -> Flux.fromIterable(commands)
        		.filter(command -> command.isActive(message))
        		.filter(command -> command.check(message))
        		.flatMap(command -> command.execute(message))
        		.next())
        	.onErrorResume(error -> {
        		System.out.println(event.getMessage().getTimestamp().toString() + ": ERROR [Server|" + event.getGuildId().map(Snowflake::asString).orElse("unkown") + "] [Channel|" + event.getMessage().getChannelId().asString() + "] [Message|" + event.getMessage().getId().asString() + "]");
        		if(error.getMessage().contains("MESSAGE_CONTENT intent is required")) System.out.println(" - MESSAGE_CONTENT intent is required");
        		else error.printStackTrace();
        		return Mono.empty();
        	})
        	.onErrorComplete().then();

    }
    
	@SuppressWarnings("unused")
	private static final Set<MessageCommand> commandsTestOld = Set.of(	
			// Normal Commands - ID 0
			new TestCommand(),
			// Trigger Message Commands - ID 2
			new TriggerMessageCommand(Global.gnT,Global.gnA,Global.gnC),
			new TriggerMessageCommand(Global.gmT,Global.gmA,Global.gmC),
			new TriggerMessageCommand(Global.bonkT,Global.bonkA,Global.bonkC),
			new TriggerMessageCommand(Global.madT,Global.madA,Global.madC),
			new TriggerMessageCommand(Global.fightT,Global.fightA,Global.fightC),
			new TriggerMessageCommand(Global.danceT,Global.danceA,Global.danceC),
			new TriggerMessageCommand(Global.loveT,Global.loveA,Global.loveC),
			new TriggerMessageCommand(Global.chisteT,Global.chisteA,Global.chisteC,true,0),
			new TriggerMessageCommand(Global.voteT,Global.voteA,Global.voteC,true,0),
			// Activity Commands - ID 3
			new RegisterRankCommand(),
			new RankFixCommand(),
			// Shogun commands - ID 4 & +40
			new TrackMembersCommand(),
			new KarutaMySlotsCommand(),
			// Analysis commands - ID +50
			new KarutaDyeAnalysisCommand(),
			// Games - ID + 200
			new AreaGameCommand(),
			// Activity Commands - ID 3
			//new TriggerActivityCommand(),
			//new RankCommand(),
			//new RankingCommand(),
			// Helpers
			//new KarutaListenCommand(),	
			// TODO
			/* e.scold
			 * e.hug
			 * e.pat
			 * e.marry
			 * e.kiss - locked to marry command
			 */
			new EventXmasGrabCommand(),
			new EventXmasWorkshopCommand(),
			new EventActivityCommand()
		);
	
	@SuppressWarnings("unused")
	private static final Set<MessageCommand> commandsTest = Set.of(	
		new KarutaMapDateDevCommand()
	);
	
	@SuppressWarnings("unused")
	private static final Set<MessageCommand> commandsBleachEvent = Set.of(
		new PingCommand("bleachEvent"),
		new BleachEventShinigamiSetCommand(),
		new BleachEventShinigamiAcademyCommand(),
		new BleachEventShinigamiStatsCommand(),
		new BleachEventShinigamiTrainCommand(),
		new BleachEventShinigamiCooldownsCommand(),
		new BleachEventShinigamiHealCommand(),
		//new BleachEventShinigamiDuelCommand(),
		new BleachEventShinigamiRankingCommand(),
		new BleachEventShinigamiSpawnCommand()
	);
	
	@SuppressWarnings("unused")
	private static final Set<MessageCommand> commandsEventTOT = Set.of(
		new EventTOTAdmCommand(),
		new EventTOTCoinActivityCommand(),
		new EventTOTHouseCommand(),
		new EventTOTShopCommand(),
		new EventTOTCommand()
	);
}