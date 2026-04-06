package com.chibiforge.elfnein.command.slash;

import java.util.stream.Collectors;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.Container;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.component.TopLevelMessageComponent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import reactor.core.publisher.Mono;

public class CheckPermsCommand extends SlashCommand {

	public CheckPermsCommand(){}
	
	@Override
	public String getName() { return "checkperms"; }
	
	@Override
	public Mono<Void> execute(ChatInputInteractionEvent event) {
		User callerUser = event.getInteraction().getUser();
		
		Mono<Member> callerMember = Mono.just(callerUser)
			.flatMap(user -> event.getInteraction().getGuild().flatMap(guild -> guild.getMemberById(user.getId())));
		
		Mono<Member> targetMember = event.getOption("user")
			.flatMap(opt -> opt.getValue().map(ApplicationCommandInteractionOptionValue::asUser))
			.map(Mono::from).orElse(Mono.just(callerUser))
			.flatMap(user -> event.getInteraction().getGuild().flatMap(guild -> guild.getMemberById(user.getId())));
		
		return callerMember.flatMap(member -> member.getBasePermissions()).flatMap(perms -> {
			if(!perms.contains(Permission.ADMINISTRATOR)) return event.reply().withEphemeral(true).withContent("You cannot do that!");
			return targetMember.flatMap(target -> target.getBasePermissions()
				.flatMap(permList -> event.reply().withEphemeral(true).withComponents(getContent(target, permList))))
				.then();
		}).then();
		
	}

	private TopLevelMessageComponent getContent(Member targetMember, PermissionSet permList) {
		
		String perms = permList.stream().map(Permission::name).collect(Collectors.joining(", "));
		String id = targetMember.getId().asString();

		return Container.of(
			Color.SEA_GREEN,
			TextDisplay.of("# Permissions from <@" + id + ">"),
			TextDisplay.of(perms)
		);

	}



}
