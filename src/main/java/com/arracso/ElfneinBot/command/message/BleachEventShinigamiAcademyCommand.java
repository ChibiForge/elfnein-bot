package com.arracso.ElfneinBot.command.message;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.Container;
import discord4j.core.object.component.MediaGallery;
import discord4j.core.object.component.MediaGalleryItem;
import discord4j.core.object.component.Separator;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.component.UnfurledMediaItem;
import discord4j.core.object.component.Separator.SpacingSize;
import discord4j.core.object.emoji.Emoji;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class BleachEventShinigamiAcademyCommand extends MessageCommand {
	
	public BleachEventShinigamiAcademyCommand(){
		commandNames.add("academy");
		commandNames.add("shinigami academy");
		commandId = Global.bleachEventBase;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve user id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();
		
		if(!message.getGuildId().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve guild id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();
		
		String userId = message.getAuthor().get().getId().asString();
		String guildId = message.getGuildId().get().asString();
		
		if(Service.bleachEventService.isUserInEvent(guildId,userId))
			return Util.replyToMessage(message,"You already graduated from the shinigami academy!\nKeep training to improve your skills!\n\n**Commands:**\n-# `e.stats` -> check your stats\n-# `e.train` -> train one skill").then();
		
		return message.getAuthorAsMember().flatMap(member -> {
			//if(!hasEventRole(member)) return Util.replyToMessage(message,"Join a fandom first! <#1401102873147736156>").then();
			
			return Util.replyToMessage(message,academyContainer(member.getId().asString())).flatMap(msg -> academyListener(msg.getId().asString())).then();
		});		
	}
	
	/*
	private static final Set<Snowflake> EVENT_ROLES = Set.of(
	    Snowflake.of("1400332847209906206"),
	    Snowflake.of("1400332865014861844"),
	    Snowflake.of("1400333310697406585"),
	    Snowflake.of("1400332869418618880"),
	    Snowflake.of("1400332873197813872"),
	    Snowflake.of("1400332855569289226")
	);

	private boolean hasEventRole(Member member) {
	    return member.getRoleIds().stream().anyMatch(EVENT_ROLES::contains);
	}
	
	private String getFandom(Member member) {
		return member.getRoleIds().stream()
	        .filter(EVENT_ROLES::contains)
	        .findFirst()
	        .map(roleId -> member.getGuild().block()              // get Guild
	            .getRoleById(roleId).block().getName())           // get Role name
	        .orElse(null);
	}*/
	
	public static final String ID_FOCUS_ZANJUTSU = "bleach:academy:zanjutsu";
    public static final String ID_FOCUS_KIDO     = "bleach:academy:kido";
    public static final String ID_FOCUS_HAKUDA   = "bleach:academy:hakuda";
    public static final String ID_FOCUS_HOHO     = "bleach:academy:hoho";
	
	private Container academyContainer(String id) {
		Container body = Container.of(
			discord4j.rest.util.Color.BLACK,
			TextDisplay.of("# Shinigami Academy (Shinōreijutsuin)"),
			MediaGallery.of(MediaGalleryItem.of(UnfurledMediaItem.of("https://static.wikia.nocookie.net/bleach/images/4/43/Ep184Shin%27oAcademy.png/revision/latest?cb=20190124043750&path-prefix=en"))),
			TextDisplay.of("Welcome to the shinigami academy <@" + id + ">!"),
			Separator.of(true, SpacingSize.of(1)),
			TextDisplay.of("Within these walls the future guardians of Soul Society are forged. Every student must master the blade, **Zanjutsu**, the foundation of a Shinigami’s strength."),
			TextDisplay.of("Yet the path you walk is yours to choose. Will you hone the deadly grace of **Zanjutsu**, channel the mystic force of **Kidō**, temper your body through **Hakuda**, or command the swiftness of **Hohō**?"),
			TextDisplay.of("All graduate with the sword at their side. But your focus determines how your spirit will shine."),
			Separator.of(true, SpacingSize.of(1)),
			TextDisplay.of("So, tell us... where will you place your resolve?"),
			ActionRow.of(
				Button.secondary(ID_FOCUS_ZANJUTSU + ":user=" + id, "🗡️ Zanjutsu"),
				Button.secondary(ID_FOCUS_KIDO     + ":user=" + id, "🔮 Kidō"),
				Button.secondary(ID_FOCUS_HAKUDA   + ":user=" + id, "🥋 Hakuda"),
				Button.secondary(ID_FOCUS_HOHO     + ":user=" + id, Emoji.unicode("🪽"), "Hohō")
			)
		);
		return body;
	}
	
	
	private Mono<Void> academyListener(String messageId) {
		return Service.client.on(ButtonInteractionEvent.class)
			.filter(e-> e.getMessageId().asString().equals(messageId))
			.filter(e -> e.getCustomId().startsWith("bleach:academy:"))
			.flatMap(e -> {
				String userId = e.getUser().getId().asString();
				String [] args = e.getCustomId().split(":");
				Map<String,String> metadata = Util.parseButtonMetadata(args[3]);
				if(!metadata.get("user").equals(userId)) return e.reply().withContent("You cannot use that!").withEphemeral(true);
				
				Snowflake guildId = e.getInteraction().getGuildId().orElse(null);
				if(Service.bleachEventService.isUserInEvent(guildId.asString(), userId)) return e.reply().withContent("You already graduated from the shinigami academy!").withEphemeral(true);
				
				return e.getUser().asMember(guildId).flatMap(member -> {
					
					//String fandom = getFandom(member);
					String fandom = "No Fandom";
					String technique = args[2];
					Integer reiatsu = 120 + (int)(Math.random() * 30);
					
					Service.bleachEventService.registerUser(guildId.asString(),userId,fandom,technique,reiatsu);
					return e.edit().withComponents(academySelectContainer(userId,technique,reiatsu)).then();
				});				
			})
			.timeout(Duration.ofMinutes(2))
			.onErrorResume(TimeoutException.class, e -> Mono.empty())
			.then();
	}

	private Container academySelectContainer(String id, String technique, Integer reiatsu) {
		Container body = null;
		if(technique.equals("zanjutsu")) {
			body = Container.of(
				discord4j.rest.util.Color.BLACK,
				TextDisplay.of("# Shinigami Academy (Shinōreijutsuin)"),
				MediaGallery.of(MediaGalleryItem.of(UnfurledMediaItem.of("https://static.wikia.nocookie.net/bleach/images/4/43/Ep184Shin%27oAcademy.png/revision/latest?cb=20190124043750&path-prefix=en"))),
				TextDisplay.of("Congratulations on graduating from the shinigami academy <@" + id + ">!"),
				Separator.of(true, SpacingSize.of(1)),
				TextDisplay.of("During your years at the academy you dedicated yourself to **Zanjutsu**, sharpening your spirit and steel until they moved as one. Through endless drills and duels, your mastery of the sword grew far beyond the basics, earning you recognition as a true disciple of the blade."),
				Separator.of(true, SpacingSize.of(1)),
				TextDisplay.of("## Graduation Report"),
				TextDisplay.of(
					"🗡️ **Zanjutsu** — Lv.5\n" +
					"🔮 **Kidō** — Lv.1\n" +
					"🥋 **Hakuda** — Lv.1\n" +
					"🪽 **Hohō** — Lv.1"
				),
				Separator.of(),
				TextDisplay.of("Your soul pressure grew steady and disciplined. You graduate from the academy with **" + reiatsu + " reiatsu** flowing through you.")
			);
		}else if(technique.equals("kido")) {
			body = Container.of(
				discord4j.rest.util.Color.BLACK,
				TextDisplay.of("# Shinigami Academy (Shinōreijutsuin)"),
				MediaGallery.of(MediaGalleryItem.of(UnfurledMediaItem.of("https://static.wikia.nocookie.net/bleach/images/4/43/Ep184Shin%27oAcademy.png/revision/latest?cb=20190124043750&path-prefix=en"))),
				TextDisplay.of("Congratulations on graduating from the shinigami academy <@" + id + ">!"),
				Separator.of(true, SpacingSize.of(1)),
				TextDisplay.of("During your years at the academy you focused your attention on **Kidō**, chanting incantations and weaving demon arts until the flow of reiryoku became second nature. Though your sword arm was kept sharp, your true strength shone in the precision of your spells."),
				Separator.of(true, SpacingSize.of(1)),
				TextDisplay.of("## Graduation Report"),
				TextDisplay.of(
					"🗡️ **Zanjutsu** — Lv.3\n" +
					"🔮 **Kidō** — Lv.3\n" +
					"🥋 **Hakuda** — Lv.1\n" +
					"🪽 **Hohō** — Lv.1"
				),
				Separator.of(),
				TextDisplay.of("Your soul pressure grew precise and arcane. You graduate from the academy with **" + reiatsu + " reiatsu** flowing through you.")
			);
		}else if(technique.equals("hakuda")) {
			body = Container.of(
				discord4j.rest.util.Color.BLACK,
				TextDisplay.of("# Shinigami Academy (Shinōreijutsuin)"),
				MediaGallery.of(MediaGalleryItem.of(UnfurledMediaItem.of("https://static.wikia.nocookie.net/bleach/images/4/43/Ep184Shin%27oAcademy.png/revision/latest?cb=20190124043750&path-prefix=en"))),
				TextDisplay.of("Congratulations on graduating from the shinigami academy <@" + id + ">!"),
				Separator.of(true, SpacingSize.of(1)),
				TextDisplay.of("During your years at the academy you devoted yourself to **Hakuda**, tempering your body into a living weapon. Every strike, throw, and counter refined your resolve, while your swordsmanship remained a solid foundation."),
				Separator.of(true, SpacingSize.of(1)),
				TextDisplay.of("## Graduation Report"),
				TextDisplay.of(
					"🗡️ **Zanjutsu** — Lv.3\n" +
					"🔮 **Kidō** — Lv.1\n" +
					"🥋 **Hakuda** — Lv.3\n" +
					"🪽 **Hohō** — Lv.1"
				),
				Separator.of(),
				TextDisplay.of("Your soul pressure grew fierce and unyielding. You graduate from the academy with **" + reiatsu + " reiatsu** flowing through you.")
			);
		}else if(technique.equals("hoho")) {
			body = Container.of(
				discord4j.rest.util.Color.BLACK,
				TextDisplay.of("# Shinigami Academy (Shinōreijutsuin)"),
				MediaGallery.of(MediaGalleryItem.of(UnfurledMediaItem.of("https://static.wikia.nocookie.net/bleach/images/4/43/Ep184Shin%27oAcademy.png/revision/latest?cb=20190124043750&path-prefix=en"))),
				TextDisplay.of("Congratulations on graduating from the shinigami academy <@" + id + ">!"),
				Separator.of(true, SpacingSize.of(1)),
				TextDisplay.of("During your years at the academy you trained in **Hohō**, taking your first true steps into the art of swiftness. Your movements became lighter and sharper, preparing you for the path of shunpo that lies ahead, while your blade drills remained reliable."),
				Separator.of(true, SpacingSize.of(1)),
				TextDisplay.of("## Graduation Report"),
				TextDisplay.of(
					"🗡️ **Zanjutsu** — Lv.3\n" +
					"🔮 **Kidō** — Lv.1\n" +
					"🥋 **Hakuda** — Lv.1\n" +
					"🪽 **Hohō** — Lv.3"
				),
				Separator.of(),
				TextDisplay.of("Your soul pressure grew swift and flowing. You graduate from the academy with **" + reiatsu + " reiatsu** flowing through you.")
			);
		}
		return body;
	}
    
}
