package com.chibiforge.elfnein.command.message;


import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Locator;
import com.chibiforge.elfnein.util.Locator.Location;

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
		String channelId = message.getChannelId().asString();
		return message.getChannel().cast(GuildChannel.class).flatMap(channel -> {
			if (!channel.getName().startsWith("ticket")) {
				return Mono.empty();
			}
			return runTimer(message, channelId);
		});
	}
	 
	private Mono<Void> runTimer(Message message, String channelId) {
		GatewayDiscordClient client = message.getClient();
		
		AtomicInteger kcbCount = new AtomicInteger(0);
		AtomicInteger knaCount = new AtomicInteger(0);
		
		AtomicReference<Instant> kcsTime = new AtomicReference<>(null);
		AtomicReference<Instant> kcbTime = new AtomicReference<>(null);
		AtomicReference<Instant> knaTime = new AtomicReference<>(null);
		
		AtomicBoolean completed = new AtomicBoolean(false);
		
		return Mono.<Void>create(sink -> {
			final Disposable[] disposables = new Disposable[2];
			
			disposables[0] = client.on(MessageCreateEvent.class)
			.map(MessageCreateEvent::getMessage)
			.filter(m -> m.getChannelId().asString().equals(channelId))
			.subscribe(m -> handleCreateMessage(m,sink,completed,kcbCount,knaCount));
			
			disposables[1] = client.on(MessageUpdateEvent.class)
			.flatMap(MessageUpdateEvent::getMessage)
			.filter(m -> m.getChannelId().asString().equals(channelId))
			.flatMap(m -> handleUpdateMessage(m,sink,completed,kcsTime,kcbTime,knaTime))
			.onErrorResume(t -> {
				System.out.println("ERROR LOOP TIMER");
				System.out.println(t.getMessage());
				return Mono.empty();
			}).subscribe();
			
			sink.onDispose(() -> {
				for (Disposable d : disposables) {
					if (d != null && !d.isDisposed()) {
						d.dispose();
					}
				}
			});
		})
		.timeout(Duration.ofSeconds(300))
		.onErrorResume(TimeoutException.class, e -> Mono.empty())
		.then();
	}
	
	private void handleCreateMessage(Message message, MonoSink<Void> sink, AtomicBoolean completed, AtomicInteger kcbCount, AtomicInteger knaCount) {
		if (completed.get()) return;
		
		String kcb = Locator.get(message, Global.kcbL);
		if (kcb.startsWith(Global.kcbT)) {
			int count = kcbCount.incrementAndGet();
			if (count >= 2) complete(sink, completed);
			return;
		}
		
		String kcs = Locator.get(message, Global.kcsL);
		if (kcs.startsWith(Global.kcsT)) {
			complete(sink, completed);
			return;
		}
		
		String kna = Locator.get(message, Global.knaL);
		if (kna.startsWith(Global.knaT)) {
			knaCount.incrementAndGet();
		}
	}
	
	private Mono<Void> handleUpdateMessage(Message message, MonoSink<Void> sink, AtomicBoolean completed, AtomicReference<Instant> kcsTime, AtomicReference<Instant> kcbTime, AtomicReference<Instant> knaTime) {
		if (completed.get()) return Mono.empty();
		
		Instant editedTime = message.getEditedTimestamp().orElse(null);
		if (editedTime == null) return Mono.empty();
		
		boolean shouldCalculate = false;
		
		String kcb = Locator.get(message, Global.kcbL);
		if (kcb.startsWith(Global.kcbT)) {
			kcbTime.set(editedTime);
			shouldCalculate = true;
		} else {
			String kcs = Locator.get(message, Global.kcsL);
			if (kcs.startsWith(Global.kcsT)) {
				kcsTime.set(editedTime);
				shouldCalculate = true;
		    } else {
		    	String kna = Locator.get(message, Global.knaL);
		    	if (kna.startsWith(Global.knaT)) {
		    		knaTime.set(editedTime);
		    	}
		    }
		}
		
		if (!shouldCalculate) return Mono.empty();
		
		Instant kcs = kcsTime.get();
		Instant kcbInstant = kcbTime.get();
		Instant kna = knaTime.get();
		
		if (kcs == null || kcbInstant == null) {
			return Mono.empty();
		}
		
		Duration kcsToKcb = Duration.between(kcs, kcbInstant);
		
		String extra = "";
		if (kna != null) {
			Duration kcsToKna = Duration.between(kcs, kna);
			extra = "\n**kcs** to **kna:** " + (kcsToKna.toMillis() / 1000f) + " s";
		}
		
		EmbedCreateSpec embed = EmbedCreateSpec.builder()
		.color(Color.SEA_GREEN)
		.title("LOOP TIME")
		.description("**kcs** to **kcb:** " + (kcsToKcb.toMillis() / 1000f) + " s" + extra)
		.build();
		
		return message.getChannel()
			.flatMap(channel -> channel.createMessage(embed))
			.doOnSuccess(sentMessage -> complete(sink, completed))
			.then();
	}

	private void complete(MonoSink<Void> sink, AtomicBoolean completed) {
		if (completed.compareAndSet(false, true)) {
			sink.success();
		}
	}
	
	/*
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
	}*/
}
