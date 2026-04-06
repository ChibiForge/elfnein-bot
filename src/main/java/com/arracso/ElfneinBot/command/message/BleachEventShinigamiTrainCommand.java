package com.arracso.ElfneinBot.command.message;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.arracso.ElfneinBot.model.BleachEventUser;
import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.Container;
import discord4j.core.object.component.LayoutComponent;
import discord4j.core.object.component.Section;
import discord4j.core.object.component.Separator;
import discord4j.core.object.component.Separator.SpacingSize;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.component.TextInput;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class BleachEventShinigamiTrainCommand extends MessageCommand {
	
	public BleachEventShinigamiTrainCommand(){
		commandNames.add("train");
		commandNames.add("shinigami train");
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
		
		if(!Service.bleachEventService.isUserInEvent(guildId,userId))
			return Util.replyToMessage(message,"You haven't graduated from the shinigami academy yet!\n-# Do `e.academy` to join the shinigami academy.").then();
		
		if(Service.bleachEventService.isUserInTrainCd(guildId,userId)) {
			Timestamp cd = Service.bleachEventService.getUserTrainCd(guildId,userId);
			return Util.replyToMessage(message,"**You are on cooldown!**\n*Cooldown finish <t:" + cd.getTime()/1000 + ":R>*").then();
		}
		
		if(Service.bleachEventService.isUserInjured(guildId,userId)) {
			Timestamp cd = Service.bleachEventService.getUserInjureCd(guildId,userId);
			return Util.replyToMessage(message,"**You cannot train because you are injured!**\n*You will be fully healed <t:" + cd.getTime()/1000 + ":R>*").then();
		}
		
		if(Service.bleachEventService.isChannelNotAllowed(message.getChannelId().asString()))
			return Util.replyToMessage(message,"**You cannot do this here!**\n*Use this command in another channel like <#1387118821151604746> or <#1220596977713287209>*").then();
		
		return Util.replyToMessage(message,trainContainer(guildId,userId)).flatMap(msg -> trainListener(msg)).then();	
    
	}
	
	
	
	public static final String ID_FOCUS_ZANJUTSU = "bleach:train:zanjutsu";
    public static final String ID_FOCUS_KIDO     = "bleach:train:kido";
    public static final String ID_FOCUS_HAKUDA   = "bleach:train:hakuda";
    public static final String ID_FOCUS_HOHO     = "bleach:train:hoho";
    
	private Container trainContainer(String guildId, String userId) {
		BleachEventUser user = Service.bleachEventService.getUser(guildId, userId);
		Integer lvZ = user.getZanjutsuLv();
		Integer lvK = user.getKidoLv();
		Integer lvH = user.getHakudaLv();
		Integer lvS = user.getHohoLv();
		Integer reiatsu = user.getReiatsu();
		
		String metaData = ":user=" + userId + "&lvZ=" + lvZ + "&lvK=" + lvK + "&lvH=" + lvH + "&lvS=" + lvS + "&reiatsu=" + reiatsu;
		
		Container c = Container.of(
			Color.BLACK,
			TextDisplay.of("# Shinigami Training"),
			TextDisplay.of("You return to the yard where steel sings and reiatsu stirs."),
			Separator.of(true, SpacingSize.of(2)),
			TextDisplay.of("## Choose a discipline to train:"),
			Section.of(
				lvZ<100 ? Button.secondary(ID_FOCUS_ZANJUTSU + metaData, "Lv."+lvZ+" -> Lv."+(lvZ+1))
						: Button.secondary(ID_FOCUS_ZANJUTSU + metaData, "Lv."+lvZ).disabled(),
				TextDisplay.of("🗡️ **Zanjutsu** — art of the sword")
			),
			Section.of(
				lvK<100 ? Button.secondary(ID_FOCUS_KIDO + metaData, "Lv."+lvK+" -> Lv."+(lvK+1))
						: Button.secondary(ID_FOCUS_KIDO + metaData, "Lv."+lvK).disabled(),
				TextDisplay.of("🔮 **Kidō** — demon arts and binding")
			),
			Section.of(
				lvH<100 ? Button.secondary(ID_FOCUS_HAKUDA + metaData, "Lv."+lvH+" -> Lv."+(lvH+1))
						: Button.secondary(ID_FOCUS_HAKUDA + metaData, "Lv."+lvH).disabled(),
				TextDisplay.of("🥋 **Hakuda** — hand-to-hand combat")
			),
			Section.of(
				lvS<100 ? Button.secondary(ID_FOCUS_HOHO + metaData, "Lv."+lvS+" -> Lv."+(lvS+1))
						: Button.secondary(ID_FOCUS_HOHO + metaData, "Lv."+lvS).disabled(),
				TextDisplay.of("🪽 **Hohō** — swift movement")
			)
		);
		return c;
	}
	
	private Mono<Void> trainListener(Message msg) {
		return Service.client.on(ButtonInteractionEvent.class)
			.filter(e-> e.getMessageId().asString().equals(msg.getId().asString()))
			.filter(e -> e.getCustomId().startsWith("bleach:train:"))
			.flatMap(e -> {
				String userId = e.getUser().getId().asString();
				String [] args = e.getCustomId().split(":");
				Map<String,String> metadata = Util.parseButtonMetadata(args[3]);
				if(!metadata.get("user").equals(userId)) return e.reply().withContent("You cannot do that!").withEphemeral(true);
				
				Snowflake guildId = e.getInteraction().getGuildId().orElse(null);
				if(Service.bleachEventService.isUserInTrainCd(guildId.asString(),userId))  return e.reply().withContent("You cannot do that!").withEphemeral(true);
					
				String technique = args[2];
				Service.bleachEventService.setTraining(guildId.asString(), userId, technique, metadata);
				
				Container trained = trainSelectContainer(technique,metadata);
				
				if(technique.equals("zanjutsu")) {
					BleachEventUser user = Service.bleachEventService.getUser(guildId.asString(), userId);
					if(user.canLearnShikai()) return e.edit()
						.withComponents(trained,ascendContainer(userId,"shikai"))
						.then(ascendListener(msg,trained));
					if(user.canLearnBankai()) return e.edit()
						.withComponents(trained,ascendContainer(userId,"bankai"))
						.then(ascendListener(msg,trained));
							
				}
				return e.edit().withComponents(trained).then();		
			})
			.timeout(Duration.ofMinutes(1))
			.onErrorResume(TimeoutException.class, e -> Mono.empty())
			.then();
	}

	private Container trainSelectContainer(String technique, Map<String,String> metadata) {
		Integer lvZ = Integer.valueOf(metadata.get("lvZ"));
		Integer lvK = Integer.valueOf(metadata.get("lvK"));
		Integer lvH = Integer.valueOf(metadata.get("lvH"));
		Integer lvS = Integer.valueOf(metadata.get("lvS")); 
		
		Integer target = technique.equals("zanjutsu")? (lvZ + 1) : (technique.equals("kido")? (lvK + 1) : (technique.equals("hakuda")? (lvH + 1) : (lvS + 1)));
		String tech = technique.equals("zanjutsu")? "Zanjutsu" : (technique.equals("kido")? "Kidō" : (technique.equals("hakuda")? "Hakuda" : "Hohō"));
		
		Container c = Container.of(
			Color.BLACK,
			TextDisplay.of("# Shinigami Training"),
			TextDisplay.of("You return to the yard where steel sings and reiatsu stirs."),
			Separator.of(true, SpacingSize.of(2)),
			TextDisplay.of("## Choose a discipline to train:"),
			Section.of(
				technique.equals("zanjutsu") 
				? Button.success(ID_FOCUS_ZANJUTSU, "Lv."+lvZ+" -> Lv."+(lvZ+1)).disabled() 
				: Button.secondary(ID_FOCUS_ZANJUTSU, lvZ==100?"Lv."+lvZ:("Lv."+lvZ+" -> Lv."+(lvZ+1))).disabled(),
				TextDisplay.of("🗡️ **Zanjutsu** — art of the sword")
			),
			Section.of(
				technique.equals("kido") 
				? Button.success(ID_FOCUS_KIDO, "Lv."+lvK+" -> Lv."+(lvK+1)).disabled()
				:Button.secondary(ID_FOCUS_KIDO, lvK==100?"Lv."+lvK:("Lv."+lvK+" -> Lv."+(lvK+1))).disabled(),
				TextDisplay.of("🔮 **Kidō** — demon arts and binding")
			),
			Section.of(
				technique.equals("hakuda") 
				? Button.success(ID_FOCUS_HAKUDA, "Lv."+lvH+" -> Lv."+(lvH+1)).disabled()
				: Button.secondary(ID_FOCUS_HAKUDA, lvH==100?"Lv."+lvH:("Lv."+lvH+" -> Lv."+(lvH+1))).disabled(),
				TextDisplay.of("🥋 **Hakuda** — hand-to-hand combat")
			),
			Section.of(
				technique.equals("hoho") 
				? Button.success(ID_FOCUS_HOHO, "Lv."+lvS+" -> Lv."+(lvS+1)).disabled()
				: Button.secondary(ID_FOCUS_HOHO, lvS==100?"Lv."+lvS:("Lv."+lvS+" -> Lv."+(lvS+1))).disabled(),
				TextDisplay.of("🪽 **Hohō** — swift movement")
			),
			Separator.of(true, SpacingSize.of(2)),
			TextDisplay.of(
				"*Your " + tech + " has leveled up to level " + target + ".*" +
				(target==100 ? (
					technique.equals("kido") ? "\n*You have learned all Kidō, including **Hadō #99**.*" : (
					technique.equals("hakuda") ? "\n*You have mastered Hakuda, **Shunko** unleashed.*" : (
					technique.equals("hoho") ? "\n*You acquired **Shunpo** the highest form of Hohō.*" : ""))
				): "")
			)
			
		);
		return c;
	}
	
	private Container ascendContainer(String userId, String ascencion) {
		if(ascencion.equals("shikai"))
			return Container.of(
				Color.BLACK,
				TextDisplay.of("# Zanpakutō Awakening"),
				TextDisplay.of("*Your Zanpakutō spirit stirs within you...*"),
				Separator.of(true, SpacingSize.of(2)),
				Section.of(
					Button.secondary("bleach:ascend:shikai:"+userId, "Name Your Shikai"),
					TextDisplay.of("What name does it reveal?")
				),
				Separator.of(true, SpacingSize.of(2)),
				TextDisplay.of("-# Answer the call within 30 seconds...")
			);
		
		return Container.of(
			Color.BLACK,
			TextDisplay.of("# Zanpakutō Transcendence"),
			TextDisplay.of("*Your bond is complete\n — your Zanpakutō demands its true name.*"),
			Separator.of(true, SpacingSize.of(2)),
			Section.of(
				Button.secondary("bleach:ascend:bankai:"+userId, "Unveil Your Bankai"),
				TextDisplay.of("Will you answer?")
			),
			Separator.of(true, SpacingSize.of(2)),
			TextDisplay.of("-# Answer the call within 30 seconds...")
		);
	}
	

	private Mono<Void> ascendListener(Message msg, Container trained) {
		AtomicBoolean pressed = new AtomicBoolean(false);
		return Service.client.on(ButtonInteractionEvent.class)
			.filter(e-> e.getMessageId().asString().equals(msg.getId().asString()))
			.filter(e -> e.getCustomId().startsWith("bleach:ascend:"))
			.flatMap(e -> {
				String userId = e.getUser().getId().asString();
				String [] args = e.getCustomId().split(":");
				if(!args[3].equals(userId)) return e.reply().withContent("You cannot do that!").withEphemeral(true);
				String ascend = args[2];
				
				pressed.set(true);
				
				Collection<LayoutComponent> c = List.of(
					ActionRow.of(TextInput.small("name", "Choose a name", 1, ascend.equals("bankai")?50:30).required(true))
				);
				
				return e.presentModal("Name your "+ascend, "bleach:naming:"+ascend,c).then(namingListener(msg, trained));
			})
			.timeout(Duration.ofMinutes(1))
			.onErrorResume(TimeoutException.class, e-> {
				if(pressed.get()) return Mono.empty();
				return msg.edit().withComponents(trained).then();
			})
			.then();
	}
	
	private Mono<Void> namingListener(Message msg, Container trained) {
		return Service.client.on(ModalSubmitInteractionEvent.class)
			.filter(e-> e.getMessageId().asString().equals(msg.getId().asString()))
			.filter(e -> e.getCustomId().startsWith("bleach:naming:"))
			.flatMap(e -> {
				String [] args = e.getCustomId().split(":");
				String ascend = args[2];
				String name = e.getComponents(TextInput.class).stream()
			            .filter(t -> t.getCustomId().equals("name"))
			            .findFirst().flatMap(TextInput::getValue).orElse("");
				
				String userId = e.getUser().getId().asString();
				Snowflake guildId = e.getInteraction().getGuildId().orElse(null);
				
				
				Container c = namingContainer(ascend, null); 
				if(Service.bleachEventService.nameZanpakuto(guildId.asString(),userId,ascend,name)) 
					c = namingContainer(ascend, name);
				
				return e.edit().withComponents(trained,c).then();
			})
			.timeout(Duration.ofMinutes(1))
			.onErrorResume(TimeoutException.class, e-> Mono.empty())
			.then();
	}
	
	private Container namingContainer(String ascencion, String name) {
		if(name == null) {
			if(ascencion.equals("shikai"))
				return Container.of(
					Color.RED,
					TextDisplay.of("# Zanpakutō Awakening"),
					TextDisplay.of("Your Zanpakutō seethes with disappointment..."),
					TextDisplay.of("*How dare you mistake me for another?*")
				);
			
			return Container.of(
				Color.RED,
				TextDisplay.of("# Zanpakutō Transcendence"),
				TextDisplay.of("Your Zanpakutō spirit roars in anger..."),
				TextDisplay.of("*To forget my name is to deny our bond!*")
			);
		}
		
		if(ascencion.equals("shikai"))
			return Container.of(
				Color.GREEN,
				TextDisplay.of("# Zanpakutō Awakening"),
				TextDisplay.of("*Your Zanpakutō spirit stirs within you...*"),
				TextDisplay.of("*...its name is " + name + "*")
				
			);
		
		return Container.of(
			Color.GREEN,
			TextDisplay.of("# Zanpakutō Transcendence"),
			TextDisplay.of("*Your bond is complete*"),
			TextDisplay.of("**BANKAI — " + name.toUpperCase() + "**")
		);
	}
	
}
