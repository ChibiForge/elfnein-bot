package com.arracso.ElfneinBot.command.message;

import java.util.HashSet;
import java.util.Set;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class TestCommand extends MessageCommand {

	public TestCommand(){
		commandNames.add("test");
		commandId = 999;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		GatewayDiscordClient client = message.getClient();
		Mono<Integer> sharedCountMono = Mono.zip(
                client.getGuildById(Snowflake.of("1194217314787282974"))
                      .flatMapMany(Guild::getMembers)
                      .map(Member::getId)
                      .collectList(),
                client.getGuildById(Snowflake.of("1148715815789658172"))
                      .flatMapMany(Guild::getMembers)
                      .map(Member::getId)
                      .collectList()
        ).map(tuple -> {
            Set<?> set1 = new HashSet<>(tuple.getT1());
            set1.retainAll(tuple.getT2());
            System.out.println(set1.size());
            return set1.size();
        });
		
		System.out.println("test");
		
		return sharedCountMono.flatMap(count ->
			message.getChannel().flatMap(ch -> ch.createMessage("Shared members: " + count))
		).then();
		
		/*
		return Util.replyToMessage(message, "test")
			.then(message.delete().then()).then();
		*/
		/*
		String text = Global.gas_west;
		EmbedCreateSpec embed = EmbedCreateSpec.builder()
				.color(Color.SEA_GREEN)
				.description("# "+text)
				.build();
		
		return message.getChannel().block()
			.createMessage(embed)
			.then(message.delete().then()).then();
		
		*/
		
		// DELETE ROLES
		/*String startsWith = "nktest";
		return message.getGuild().block().getRoles().filter(role -> role.getName().toLowerCase().startsWith(startsWith.toLowerCase())).flatMap(role -> role.delete()
                .doOnSuccess(ignored -> System.out.println("Deleted Role: " + role.getName()))
                .onErrorResume(e -> {
                    System.err.println("Failed to delete role: " + role.getName());
                    return Mono.empty(); // Continue processing other channels
                })
            ).then();
		
		*/
		// DELETE CHANELS
		/*String startsWith = "nktest-";
		return message.getGuild().block().getChannels().filter(channel -> channel.getName().toLowerCase().startsWith(startsWith.toLowerCase())).flatMap(channel -> channel.delete()
                .doOnSuccess(ignored -> System.out.println("Deleted Channel: " + channel.getName()))
                .onErrorResume(e -> {
                    System.err.println("Failed to delete channel: " + channel.getName());
                    return Mono.empty(); // Continue processing other channels
                })
            ).then();
            */
	}
	/*
	private Mono<Void> unban(Message message){
		return message.getGuild().flatMap(guild -> {
			// Get the start of the current day (UTC)
			Instant todayStart = Instant.now().truncatedTo(ChronoUnit.DAYS);
			;
			return guild.getAuditLog()
	        		.filter(auditLogEntry -> auditLogEntry.getCreationTimestamp().isAfter(todayStart))
	                // Unban each user based on the audit log's target user id
	                .flatMap(auditLogEntry -> {
	                    Snowflake targetId = auditLogEntry.getTargetId();
	                    return guild.unban(targetId)
	                            .doOnSuccess(unbanned -> System.out.println("Unbanned user: " + targetId.asString()));
	                })
	                .then(message.getChannel().block().createMessage("Unban done.").then(message.delete().then()).then())
	                .then();
	    });
	}*/
	
	
	
}
