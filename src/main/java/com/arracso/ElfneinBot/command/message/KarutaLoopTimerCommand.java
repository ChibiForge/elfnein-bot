package com.arracso.ElfneinBot.command.message;


import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Locator;
import com.arracso.ElfneinBot.util.Locator.Location;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

public class KarutaLoopTimerCommand extends MessageCommand {
	
	private String trigger;
	private Location[] triggerLocation;
	private String authorID;
	
	public KarutaLoopTimerCommand(){
		this.trigger = Global.kcsT;
		this.triggerLocation = Global.kcsL;
		this.authorID = Global.KarutaID;
		this.commandId = Global.cmdIdLoopTimer;
	}
	
	@Override
	public Boolean check(Message message) {
		if(!authorID.isEmpty()) {
			// Check if message has author
			if(!message.getAuthor().isPresent()) return false;
			// Check if message is from Karuta
			if(!message.getAuthor().get().getId().asString().equals(authorID)) return false;
		}
		
		// Check trigger
		return Locator.get(message, triggerLocation).startsWith(trigger);
	}
	
	
	@Override
	public Mono<Void> execute(Message message) {
		
		// Get channel name and id
		String channelId = message.getChannelId().toString();
		String channelName = ((GuildChannel) message.getChannel().block()).getName();
		
		// Check if channel is a ticket
		if(!channelName.startsWith("ticket")) return Mono.empty();
		
		GatewayDiscordClient client = message.getClient();
		
		AtomicInteger kcbCount = new AtomicInteger(0);
		AtomicInteger knaCount = new AtomicInteger(0);
		AtomicReference<Instant> kcsTime = new AtomicReference<Instant>(null);
		AtomicReference<Instant> kcbTime = new AtomicReference<Instant>(null);
		AtomicReference<Instant> knaTime = new AtomicReference<Instant>(null);
		
		return Mono.create((MonoSink<Void> sink) -> 
		{
			final Disposable[] disposables = new Disposable[2];
			
			disposables[0] = client.on(MessageCreateEvent.class)
				.filter(e -> e.getMessage().getChannelId().toString().equals(channelId))
				.subscribe(e -> {
					Message m = e.getMessage();
					if(Locator.get(m, Global.kcbL).startsWith(Global.kcbT)) {
						int kcbCount_ = kcbCount.incrementAndGet();
						if(kcbCount_ >= 2) sink.success();
					}else if(Locator.get(m, Global.kcsL).startsWith(Global.kcsT)) {
						sink.success();
					}else if(Locator.get(m, Global.knaL).startsWith(Global.knaT)) {
						knaCount.incrementAndGet();
					}
				});
			
			 disposables[1] = client.on(MessageUpdateEvent.class)
				.flatMap(e -> e.getMessage())
				.filter(m -> m.getChannelId().toString().equals(channelId))
				.flatMap(m -> {
					Boolean check = false;
					if(Locator.get(m, Global.kcbL).startsWith(Global.kcbT)) {
						kcbTime.set(m.getEditedTimestamp().get());
						check = true;
					}else if(Locator.get(m, Global.kcsL).startsWith(Global.kcsT)) {
						kcsTime.set(m.getEditedTimestamp().get());
						check = true;
					}else if(Locator.get(m, Global.knaL).startsWith(Global.knaT)) {
						knaTime.set(m.getEditedTimestamp().get());
						//check = true;
					}
					
					if(check) { // TODO do the attack
						Instant kcsTime_ = kcsTime.get();
						Instant kcbTime_ = kcbTime.get();
						Instant knaTime_ = knaTime.get();
						
						Duration kcsToKcb = null;
						Duration kcsToKna = null;
						
						if(kcsTime_ != null && kcbTime_ != null) {
							kcsToKcb = Duration.between(kcsTime_,kcbTime_);
							
							String extra = "";
							if(knaTime_ != null) {
								kcsToKna = Duration.between(kcsTime_,knaTime_);
								extra = "\n**kcs** to **kna:** " + kcsToKna.toMillis()/1000f + " s";
							}
							
							EmbedCreateSpec embed = EmbedCreateSpec.builder()
								.color(Color.SEA_GREEN)
								.title("LOOP TIME")
								.description("**kcs** to **kcb:** " + kcsToKcb.toMillis()/1000f + " s" + extra)
								.build();
							return m.getChannel().flatMap(channel -> channel.createMessage(embed).doOnSuccess(sentMsg -> sink.success())).then();
						}
					}
				
					return Mono.empty();
				}).onErrorResume(t -> {
					System.out.println("ERROR LOOP TIMER");
					System.out.println(t.getMessage());
					return Mono.empty();
				}).subscribe();
			 
			 // Dispose the listeners
			 sink.onDispose(() -> { for (Disposable d : disposables) { if (d != null && !d.isDisposed()) d.dispose(); }});
		}).timeout(java.time.Duration.ofSeconds(300))
             .onErrorResume(TimeoutException.class, e -> Mono.empty());
	}
}
