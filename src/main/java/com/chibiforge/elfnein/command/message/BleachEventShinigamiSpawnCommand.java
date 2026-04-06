package com.chibiforge.elfnein.command.message;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.chibiforge.elfnein.game.bleach.Hollow;
import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.Container;
import discord4j.core.object.component.MediaGallery;
import discord4j.core.object.component.MediaGalleryItem;
import discord4j.core.object.component.Section;
import discord4j.core.object.component.Separator;
import discord4j.core.object.component.Separator.SpacingSize;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.component.Thumbnail;
import discord4j.core.object.component.UnfurledMediaItem;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class BleachEventShinigamiSpawnCommand extends MessageCommand {
	
	public BleachEventShinigamiSpawnCommand(){
		commandId = Global.bleachEventSpawn;
	}
	
	@Override
	public Boolean check(Message message) {
		if(message.getAuthor().map(user -> !user.isBot()).orElse(false)) return true;
		return false;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		// Check if hollow can spawn
		if(!Service.bleachEventService.canHollowSpawn(message.getChannelId().asString())) return Mono.empty();
		
		// Get a random hollow and spawn it
		Hollow hollow = Service.bleachEventService.getRandomHollow(message.getChannelId().asString());
		return message.getChannel().flatMap(channel -> channel.createMessage().withFlags(Message.Flag.IS_COMPONENTS_V2)
			.withComponents(spawnContainer(hollow))
			.flatMap(msg -> spawnListener(msg,hollow)).then()
		);
	}
    
	private static String [] arrows = {"⇊", "⇣", "", "⇡", "⇈", "?"};
	
	
	private Container spawnContainer(Hollow hollow) {
		return spawnContainer(hollow,0,true);
	}
	
	private Container spawnContainer(Hollow hollow,Integer etapa, Boolean active) {
		
		// Get hollow from etapa
		Hollow hollowEtapa = hollow;
		Integer etapaAux = etapa;
		while(etapaAux != 0) {
			hollowEtapa = hollowEtapa.getResurreccion();
			etapaAux --;
		}
		// Title
		String title = "# ";
		if(etapa == 0) {
			if(hollow.getTpye().equals("Not Hollow")) title = title + hollow.getName() + " Descends";
			else if(hollow.getTpye().equals("Unique Hollow")) title = title + "Hostile Singularity Detected";
			else if(hollow.getTpye().equals("Demi-Hollow") || hollow.getTpye().equals("Huge Hollow")) title = title + "Hostile Presence Detected";
			else if(hollow.getTpye().equals("Arrancar")) title = title + "Arrancar Manifestation";
			else title = title +  "Menos Grande Alert";
		} else {
			if(hollow.getTpye().equals("Not Hollow")){
				title = title + hollow.getName() + " (" + hollowEtapa.getName() + ")";
			}else if(etapa == 1){
				title = title + "Resurrección: " + hollow.getResurreccion().getName();
			}else {
				title = title + hollow.getResurreccion().getResurreccion().getName();
			}
		} 
		
		// Message
		String desc = "";
		if(etapa == 0) {
			if(hollow.getTpye().equals("Demi-Hollow") || hollow.getTpye().equals("Huge Hollow")) {
				if(hollow.getName()==null) desc = "A " + hollow.getTpye() + " breaks into the world.";
				else desc = "From the shadows steps the " + hollow.getTpye() + " known as " + hollow.getName() + ".";
			}else if(hollow.getTpye().equals("Gillian")) {
				if(hollow.getName()==null) desc = "A towering Gillian lumbers into view.";
				else desc = "The Gillian " + hollow.getName()  + " lumbers into view.";
			}else if(hollow.getTpye().equals("Adjuchas")) {
				if(hollow.getName()==null) desc = "A feral Adjuchas prowls into the fray.";
				else desc = "The feral Adjuchas " + hollow.getName() + " prowls into the battlefield";
			}else if(hollow.getTpye().equals("Vasto Lorde")) {
				if(hollow.getName()==null) desc = "The air cracks as a Vasto Lorde manifests.";
				else desc = "The Vasto Lorde " + hollow.getName() + " manifests.";
			}else if(hollow.getTpye().equals("Arrancar")) {
				desc = desc + hollow.getName() + " descends onto the battlefield.";
			}else if(hollow.getTpye().equals("Unique Hollow"))
				desc = desc + hollow.getName() + " materializes before you.";
			else if(hollow.getTpye().equals("Not Hollow"))
				desc = desc + hollow.getName() + " stands before you.";
		} else {
			if(hollowEtapa.getTpye().equals("Evolution")) {
				desc = desc + hollow.getName() + " evolved: *" + hollowEtapa.getName()+ "*";
			}else if(hollowEtapa.getTpye().equals("Resurreccion")) {
				if(etapa == 1) desc = desc + hollow.getName() + " evolved: *Resurrección — " + hollowEtapa.getName() + "*";
				else desc = desc + hollow.getName() + " evolved: *" + hollowEtapa.getName()+ "*";
			}
		}
		// Stats
		String hollowStats = "# 💠 " + (hollowEtapa.getReiatsu()!=-1? (""+hollowEtapa.getReiatsu()):"?????");
		String hollowStatsAux = "";
		
		if(hollowEtapa.getFerocity() != 2) hollowStatsAux = hollowStatsAux + "   💥" + arrows[hollowEtapa.getFerocity()];
		if(hollowEtapa.getInstinct() != 2) hollowStatsAux = hollowStatsAux + "   🧠" + arrows[hollowEtapa.getInstinct()];
		if(hollowEtapa.getResilience() != 2) hollowStatsAux = hollowStatsAux + "   🛡️" + arrows[hollowEtapa.getResilience()];
		if(hollowEtapa.getAgility() != 2) hollowStatsAux = hollowStatsAux + "   💨" + arrows[hollowEtapa.getAgility()];
		if(!hollowStatsAux.isEmpty()) hollowStats = hollowStats + "\n## " + hollowStatsAux;
		
		// Container
		Container c = Container.of(
			Color.BLACK,
			TextDisplay.of(title),
			MediaGallery.of(MediaGalleryItem.of(UnfurledMediaItem.of(hollowEtapa.getImageUrl()))),
			TextDisplay.of(desc),
			Separator.of(true, SpacingSize.of(1)),
			TextDisplay.of(hollowStats),
			Separator.of(true, SpacingSize.of(1)),
			TextDisplay.of("Which discipline will you wield in battle?"),
			ActionRow.of(
				active?Button.secondary("bleach:battle:zanjutsu", "🗡️ Zanjutsu"):Button.secondary("bleach:battle:zanjutsu", "🗡️ Zanjutsu").disabled(),
				active?Button.secondary("bleach:battle:kido", "🔮 Kidō"):Button.secondary("bleach:battle:kido", "🔮 Kidō").disabled(),
				active?Button.secondary("bleach:battle:hakuda", "🥋 Hakuda"):Button.secondary("bleach:battle:hakuda", "🥋 Hakuda").disabled()
			)
		);
		return c;
	}
	
	private Mono<Void> spawnListener(Message message,Hollow hollow) {
		return spawnListener(message, hollow, 0);
	}
	
	private Mono<Void> spawnListener(Message message,Hollow hollow, Integer etapa) {
		return Service.client.on(ButtonInteractionEvent.class)
			.filter(e-> e.getMessageId().asString().equals(message.getId().asString()))
			.filter(e -> e.getCustomId().startsWith("bleach:battle:"))
			.flatMap(e -> {
				try {
					String userId = e.getUser().getId().asString();
					String [] args = e.getCustomId().split(":");
					
					Snowflake guildId = e.getInteraction().getGuildId().orElse(null);
					
					if(!Service.bleachEventService.isUserInEvent(guildId.asString(),userId))
						return e.reply().withContent("You haven't graduated from the shinigami academy yet!\n-# Do `e.academy` to join the shinigami academy.").withEphemeral(true).then();
					
					if(Service.bleachEventService.isUserInjured(guildId.asString(),userId)) {
						Timestamp cd = Service.bleachEventService.getUserInjureCd(guildId.asString(),userId);
						return e.reply().withContent("**You cannot join the battle because you are injured!**\n*You will be fully healed <t:" + cd.getTime()/1000 + ":R>*").withEphemeral(true).then();
					}
					
					if(!Service.bleachEventService.registerShinigamiIntoBattle(guildId.asString(),message.getChannelId().asString(),userId, args[2]))
						return e.reply().withContent("You already joined the battle.").withEphemeral(true).then();
					
					return e.reply().withContent("You joined the battle!").withEphemeral(true).then();	
				} catch (Exception ex) {
					ex.printStackTrace();
					return e.reply().withContent("Something went wrong. Tell arracso!").withEphemeral(true).then();
				}
			})
			.timeout(Duration.ofSeconds(15))
			.onErrorResume(TimeoutException.class, e -> message.getGuild().flatMap(guild ->{
				// Get hollow from etapa
				Hollow hollowEtapa = hollow;
				Integer etapaAux = etapa;
				while(etapaAux != 0) {
					hollowEtapa = hollowEtapa.getResurreccion();
					etapaAux --;
				}
				Map<String,List<String>> results = Service.bleachEventService.performBattle(guild.getId().asString(),message.getChannelId().asString(),hollowEtapa);
				
				return message.edit()
				.withComponents(spawnContainer(hollow,etapa,false))
				.flatMap(msg -> Util.replyToMessage(msg, battleContainer(results,hollow,etapa))
					.flatMap(msg2 -> results.get("info").get(0).equals("RES")?
							Util.replyToMessage(msg2, spawnContainer(hollow,etapa+1,true))
							.flatMap(msg3 -> spawnListener(msg3,hollow,etapa+1)).then()
						: results.get("info").get(0).equals("END")?
							Util.replyToMessage(msg2, spawnContainer(chairSama))
							.flatMap(msg3 -> spawnListener(msg3,chairSama)).then()
						: Mono.empty())
					.then()
				).then();
			})).then();
	}
	
	Hollow chairSama = new Hollow("Not Hollow","Chair-sama","https://i.postimg.cc/hvvbxz7r/Aizen-6.png",-1,2,2,2,2);
	
	private Container [] battleContainer(Map<String,List<String>> results, Hollow hollow, Integer etapa) {
		String tech = "";
		if(!results.get("uZan").isEmpty()) {
			String joined = results.get("uZan").stream().map(user -> "<@" + user + ">").collect(Collectors.joining(", "));
			tech = tech + "\n**The following Shinigami wield their swords:**\n" + joined;
		}
		if(!results.get("uKid").isEmpty()) {
			String joined = results.get("uKid").stream().map(user -> "<@" + user + ">").collect(Collectors.joining(", "));
			tech = tech + "\n**The following Shinigami chant their incantations:**\n" + joined;
		}
		if(!results.get("uHak").isEmpty()) {
			String joined = results.get("uHak").stream().map(user -> "<@" + user + ">").collect(Collectors.joining(", "));
			tech = tech + "\n**The following Shinigami engage in close combat:**\n " + joined;
		}
		
		List<Container> c = new ArrayList<>();
		
		// Get hollow from etapa
		Hollow hollowEtapa = hollow;
		Integer etapaAux = etapa;
		while(etapaAux != 0) {
			hollowEtapa = hollowEtapa.getResurreccion();
			etapaAux --;
		}
		
		TextDisplay battleDesc = TextDisplay.of(
				"-# *" + (etapa == 0
					? hollow.getTpye().equals("Not Hollow")
						? hollow.getName()
						:(hollowEtapa.getTpye() + (hollowEtapa.getName()!=null?(" "+ hollowEtapa.getName()):""))
					: (hollow.getName() + " (" + hollowEtapa.getName() + ")")
				) + "*" + 
				"\n- **Number of Shinigamis:** " + results.get("info").get(1)
		);
		
		Container c1 = tech.isBlank()? 
			Container.of(
				Color.BLACK,
				Section.of(
					Thumbnail.of(UnfurledMediaItem.of(hollowEtapa.getImageUrl())),
					TextDisplay.of("# Battle Report"), battleDesc
				)
			):Container.of(
				Color.BLACK,
				Section.of(
					Thumbnail.of(UnfurledMediaItem.of(hollowEtapa.getImageUrl())),
					TextDisplay.of("# Battle Report"), battleDesc
				),
				Separator.of(true, SpacingSize.of(1)),
				TextDisplay.of(tech)
			);
		c.add(c1);
		
		if(!results.get("uSpecial").isEmpty()) {
			c.add(Container.of(Color.BLACK,TextDisplay.of(results.get("uSpecial").stream().collect(Collectors.joining("\n")))));
		}
		
		String injuresAndDeaths = "";
		if(!results.get("uInj").isEmpty()) {
			String injured = results.get("uInj").stream().map(user -> "<@" + user + ">").collect(Collectors.joining(", "));
			injuresAndDeaths = "\n**The following Shinigami were wounded in battle:**\n" + injured;
		}
		
		if(!results.get("uDie").isEmpty()) {
			String almostDead = results.get("uDie").stream().map(user -> "<@" + user + ">").collect(Collectors.joining(", "));
			injuresAndDeaths = injuresAndDeaths + "\n**The following Shinigami almost die:**\n" + almostDead;
		}
		
		if(!results.get("uSlo").isEmpty()) {
			String tooSlow = results.get("uSlo").stream().map(user -> "<@" + user + ">").collect(Collectors.joining(", "));
			injuresAndDeaths = injuresAndDeaths + "\n**The following Shinigami were too slow to join the battle:**\n" + tooSlow;
		}
		
		if(!injuresAndDeaths.isBlank()) {
			c.add(Container.of(Color.ORANGE,TextDisplay.of(injuresAndDeaths)));
		}
		
		String name = "The Hollow";
		if(hollow.getName()!=null) name = hollow.getName();
		
		c.add(Container.of(
			results.get("info").get(0).equals("WIN")?Color.GREEN:(results.get("info").get(0).equals("LOSE")?Color.RED:Color.ORANGE),
			TextDisplay.of(
				results.get("info").get(0).equals("WIN")?
					"# ✨ " + name + " is purified"
				:(results.get("info").get(0).equals("LOSE")?
					"# ☠️ " + name + " claims victory"
				:(results.get("info").get(0).equals("RES")?
					"# 💢 " + name + " is not done yet"
				: 
					"# ✨ " + name + " is purified, but..."
				)))
		));
		
		return c.toArray(new Container[0]);
	}
}
