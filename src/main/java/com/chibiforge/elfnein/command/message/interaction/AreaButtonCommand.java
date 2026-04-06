package com.chibiforge.elfnein.command.message.interaction;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.chibiforge.elfnein.game.area.AreaGame;
import com.chibiforge.elfnein.game.area.AreaState;
import com.chibiforge.elfnein.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.Container;
import discord4j.core.object.component.File;
import discord4j.core.object.component.Separator;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.component.UnfurledMediaItem;
import discord4j.core.spec.MessageCreateFields;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class AreaButtonCommand extends ButtonCommand {
	
	public AreaButtonCommand(){
		buttonCmdId = "area";
	}
	
	@Override
	public Mono<Void> execute(ButtonInteractionEvent event) {
		String [] args = event.getCustomId().split(":");
		if(args[1].equals("start")) return handleStart(event, args[2],args[3]);
		else if(args[1].equals("color")) return handleColor(event, args[2],args[3]);
		else if(args[1].equals("draw")) return handleDraw(event, args[2]);
		else if(args[1].equals("resign")) return handleResign(event, args[2]);
		else if(args[1].equals("flag")) return handleFlag(event, args[2]);
		return Mono.empty();
	}

	private Mono<Void> handleStart(ButtonInteractionEvent event, String action, String metadataStr) {
		Map<String,String> metadata = Util.parseButtonMetadata(metadataStr);
		if(action.equals("decline")) {
			if(!event.getUser().getId().asString().equals(metadata.get("p1")) && !event.getUser().getId().asString().equals(metadata.get("p2")))
				return event.reply("You not the person being challenged!").withEphemeral(true).then();
			Container body = Container.of(
				Color.RED,
				TextDisplay.of("Someone is challenging you!"),
				Separator.of(),
				TextDisplay.of("**Game:** Area"),
				TextDisplay.of("**Mode:** Classic"),
				TextDisplay.of("**Player 1:** <@" + metadata.get("p1") + ">"),
				TextDisplay.of("**Player 2:** <@" + metadata.get("p2") + ">"),
				Separator.of(),
				TextDisplay.of("Will you accept the challenge?"),
				Separator.of(),
				TextDisplay.of("❌ Challenge rejected.")
			);
			return event.edit().withComponents(body).then();
		} else if(action.equals("accept")) {
			if(!event.getUser().getId().asString().equals(metadata.get("p2")))
				return event.reply("You are not the person being challenged!").withEphemeral(true).then();
			AreaGame areaGame = new AreaGame(event.getMessageId().asString(),metadata);
			byte[] stateBytes = Json.write(areaGame.getState());
			String stateFile = "area-" + areaGame.gameId() + "-" + areaGame.turn() + ".json";
			String imgFile = "area-" + areaGame.gameId() + "-" + areaGame.turn() + ".png";
			Container areaGameContainer = areaGame.getContainer(imgFile);
			List<Button> options = areaGame.getOptions(stateFile);
			byte[] imgBytes = areaGame.generateImage();
			/*
			Container body = Container.of(
				Color.RED,
				TextDisplay.of("Someone is challenging you!"),
				Separator.of(),
				TextDisplay.of("**Game:** Area"),
				TextDisplay.of("**Mode:** Classic"),
				TextDisplay.of("**Player 1:** <@" + metadata.get("p1") + ">"),
				TextDisplay.of("**Player 2:** <@" + metadata.get("p2") + ">"),
				Separator.of(),
				TextDisplay.of("Will you accept the challenge?"),
				Separator.of(),
				TextDisplay.of("✅ Challenge accepted.")
			);*/
			return event.reply().withComponents(
				File.of(UnfurledMediaItem.of("attachment://"+stateFile),true),
				areaGameContainer,
				ActionRow.of(options)
			).withFiles(
				MessageCreateFields.File.of(imgFile,new ByteArrayInputStream(imgBytes)),
				MessageCreateFields.File.of(stateFile,new ByteArrayInputStream(stateBytes))
			).then();
		}
		return Mono.empty();
	}

	@SuppressWarnings("deprecation")
	private Mono<Void> handleColor(ButtonInteractionEvent event, String color, String metadataStr) {
		Map<String,String> metadata = Util.parseButtonMetadata(metadataStr);
		// Check turn
		String turnPlayer = metadata.get("t");
		//String fileState = metadata.get("f");
		
		if(!event.getUser().getId().asString().equals(turnPlayer))
			return event.reply("You are not the person playing this turn!").withEphemeral(true).then();
		
		// Get state
		AreaGame areaGame = new AreaGame();
		event.getMessage().ifPresent(message -> {
			File file = (File) message.getComponentById(1).get();
			try (InputStream in = new URL(file.getFile().getURL()).openStream()) {
			    byte[] bytes = in.readAllBytes();
			    AreaState state = Json.read(bytes, AreaState.class);
			    areaGame.updateState(state);
			} catch (Exception e) {
				e.printStackTrace();
			}			
		});
		if(!areaGame.hasState()) 
			return event.reply("Something break. Try again please.").withEphemeral(true).then();
		
		areaGame.playColor(Integer.parseInt(color));
		
		if(areaGame.isOver()) return event.reply("Congratulations <@" +turnPlayer+ ">. You win!").then();
		
		
		// Continue
		byte[] stateBytes = Json.write(areaGame.getState());
		String stateFile = "area-" + areaGame.gameId() + "-" + areaGame.turn() + ".json";
		String imgFile = "area-" + areaGame.gameId() + "-" + areaGame.turn() + ".png";
		Container areaGameContainer = areaGame.getContainer(imgFile);
		List<Button> options = areaGame.getOptions(stateFile);
		byte[] imgBytes = areaGame.generateImage();
		
		return event.reply().withComponents(
				File.of(UnfurledMediaItem.of(MessageCreateFields.File.of(stateFile,new ByteArrayInputStream(stateBytes))),true),
				areaGameContainer,
				ActionRow.of(options)
			).withFiles(
				MessageCreateFields.File.of(imgFile,new ByteArrayInputStream(imgBytes)),
				MessageCreateFields.File.of(stateFile,new ByteArrayInputStream(stateBytes))
			).then();
	}

	private Mono<Void> handleDraw(ButtonInteractionEvent event, String action) {
		
		return Mono.empty();
	}
	
	private Mono<Void> handleResign(ButtonInteractionEvent event, String action) {
		// TODO Auto-generated method stub
		return Mono.empty();
	}
	
	private Mono<Void> handleFlag(ButtonInteractionEvent event, String action) {
		// TODO Auto-generated method stub
		return Mono.empty();
	}
	
	/////////////////
	

	
	//////////////////////////////////
	
	private final class Json {		
	    private static final ObjectMapper MAPPER = new ObjectMapper()
	        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	    public static byte[] write(Object o) {
	        try { return MAPPER.writeValueAsBytes(o); }
	        catch (Exception e) { throw new RuntimeException(e); }
	    }

	    public static <T> T read(byte[] bytes, Class<T> cls) {
	        try { return MAPPER.readValue(bytes, cls); }
	        catch (Exception e) { throw new RuntimeException(e); }
	    }
	}
}
