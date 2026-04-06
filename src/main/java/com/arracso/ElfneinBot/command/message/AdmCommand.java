package com.arracso.ElfneinBot.command.message;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class AdmCommand extends MessageCommand {

	public AdmCommand(){
		commandNames.add("unban");
		commandId = Global.cmdAdmin;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		String commandName = getCommandName(message);
		List<String> params = getParameters(message);
		if(commandName.equals("unban")) {
			if(!params.isEmpty() && params.get(0).toLowerCase().equals("past")) 
				return unbanAllPast(message, params);
		}
		
		return Util.replyToMessage(message, "Wrong usage. Correct usage is: `e.unban [past] [duration] [s|m|h|d|w]`").then();
		
	}
	
	private Mono<Void> unbanAllPast(Message message, List<String> params) {
		if(params.size() != 1 && params.size() != 3) {
			return Util.replyToMessage(message, "Wrong usage. Correct usage is: `e.unban past [duration] [s|m|h|d|w]`").then();
		}
		
		long period = 1;
		ChronoUnit unit = ChronoUnit.HOURS;
		if(params.size() == 3) {
			try {
		        period = Long.parseLong(params.get(1));
		        if (period <= 0) throw new NumberFormatException();
		        unit = parseUnit (params.get(2));
		    } catch (Exception e) {
		        return Util.replyToMessage(message, "Wrong usage. Correct usage is: `e.unban past [duration] [s|m|h|d|w]`").then();
		    }
		}
		
	    Instant start = Instant.now().minus(period, unit);
	    
	    return Util.replyToMessage(message, "*Starting unban… This may take a moment.*").flatMap(msg -> message.getGuild().flatMap(guild -> guild.getAuditLog()
	        .flatMapIterable(part -> part.getEntries())
	        .filter(e -> e.getId().getTimestamp().isAfter(start))
	        .flatMap(e -> Mono.justOrEmpty(e.getTargetId())
                .flatMap(id -> guild.unban(id).thenReturn(id))
                .onErrorResume(ex -> Mono.empty())
	        )
	        .map(id -> id.asString()).distinct().collectList()
	        .flatMap(ids -> {
	            int n = ids.size();
	            if (n == 0) {
	                return Util.replyToMessage(message, "Done. No users matched the criteria (nothing to unban).");
	            }
	            String body = "Unban done. Unbanned: " + n + (ids.isEmpty() ? "": ("\n" + String.join(", ", ids)));
	            if (body.length() > 1900) body = "Unban done. Unbanned: " + n + "\n(List too long)";
	            String res = body.length() > 1900 ? "Unban done. Unbanned: " + n + "\n(List too long)": body;
	            return Util.replyToMessage(message,res);
	        })
	        .then(msg.delete())
	    ));
	}
	
	private ChronoUnit parseUnit(String raw) {
	    return switch (raw.toLowerCase()) {
	        case "s" -> ChronoUnit.SECONDS;
	        case "m" -> ChronoUnit.MINUTES;
	        case "h" -> ChronoUnit.HOURS;
	        case "d" -> ChronoUnit.DAYS;
	        case "w" -> ChronoUnit.WEEKS;
	        default -> throw new IllegalArgumentException("Invalid time unit");
	    };
	}
	
	
}
