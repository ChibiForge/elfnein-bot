package com.chibiforge.elfnein.command.message;

import java.util.List;

import com.chibiforge.elfnein.model.Item;
import com.chibiforge.elfnein.model.UserInventory;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class InventoryCommand extends MessageCommand {
	
	public InventoryCommand(){
		commandNames.add("inventory");
		commandNames.add("inv");
		commandNames.add("i");
		commandId = 0;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(message.getAuthor().isEmpty()) {
	        return Util.replyToMessage(message, "Something went wrong! Cannot retrieve user id. Please tell <@278957461120090113> to fix me!").then();
	    }
		
		List<String> params = getParameters(message);
		String userId = params.size() == 1 ? Util.parseId(params.get(0)) : message.getAuthor().get().getId().asString();
		
		return Mono.fromCallable(() -> {
            List<UserInventory> inv = Service.userInventoryService.listAllItemsOfUser(userId);

            if (inv.isEmpty()) {
                return List.of("_Empty inventory_");
            }

            return inv.stream()
                    .filter(x -> x.getQuantity() > 0)
                    .map(x -> {
                        Long itemId = x.getItemId();
                        int qty = x.getQuantity();
                        Item item = Service.userInventoryService.getItem(itemId);
                        
                        // if unknown item, still show id
                        if (item == null) {
                            return "`x" + qty + "` — Unknown Item (id=" + itemId + ")";
                        }
                        
                        return item.getIcon() + "  **x" + qty + "** — " + (qty!=1 ? item.getName_p() : item.getName_s());
                    })
                    .toList();
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(inventoryStr -> Service.paginationService.paginate("Inventory","Items carried by <@" + userId + ">\n",inventoryStr,message));
	}
}
