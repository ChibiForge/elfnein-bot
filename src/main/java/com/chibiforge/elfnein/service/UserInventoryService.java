package com.chibiforge.elfnein.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chibiforge.elfnein.model.Item;
import com.chibiforge.elfnein.model.UserInventory;
import com.chibiforge.elfnein.repository.UserInventoryRepository;

@Service
public class UserInventoryService {
	
	private static Map<String,Timestamp> lastTimeSpeak = new ConcurrentHashMap<>();
	
    private final UserInventoryRepository repo;
    
    public UserInventoryService(UserInventoryRepository repo) {
        this.repo = repo;
    }
    
    ////////////
    // Public //
    ////////////
    
    @Transactional(readOnly = true)
    public List<UserInventory> listAllItemsOfUser(String userId) {
        requireUser(userId);
        return repo.findAllByUserIdOrderByItemIdAsc(userId);
    }
    
    @Transactional
    public InventoryChange changeItemQuantity(String userId, Long itemId, int delta) {
        requireUser(userId);
        requireItem(itemId);

        if (delta == 0) throw new IllegalArgumentException("delta cannot be 0");

        UserInventory inv = repo.findByUserIdAndItemId(userId, itemId)
                .orElseGet(() -> new UserInventory(userId, itemId, 0));

        int before = inv.getQuantity();

        long newQtyLong = (long) before + delta;
        if (newQtyLong < 0) throw new IllegalStateException("quantity underflow");
        if (newQtyLong > Integer.MAX_VALUE) throw new IllegalStateException("quantity overflow");

        int after = (int) newQtyLong;
        inv.setQuantity(after);

        repo.save(inv);

        return new InventoryChange(userId, itemId, before, after, after - before);
    }
    
    @Transactional
    public InventoryChange giveItemToUser(String userId, Long itemId, int amount) {
        requirePositive(amount);
        return changeItemQuantity(userId, itemId, amount);
    }

    @Transactional
    public InventoryChange removeItemFromUser(String userId, Long itemId, int amount) {
        requirePositive(amount);
        return changeItemQuantity(userId, itemId, -amount);
    }

	private static void requireUser(String discordUserId) {
		if (discordUserId == null || discordUserId.isBlank()) {
		    throw new IllegalArgumentException("discordUserId must not be blank");
		}
	}
	
	private static void requireItem(Long itemId) {
        if (itemId == null) {
            throw new IllegalArgumentException("itemId must not be null");
        }
    }

    private static void requirePositive(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
    }
    
	public Item getItem(Long itemId) {
		if(ITEMS.containsKey(itemId)) return ITEMS.get(itemId);
		return null;
	}
	
	public record InventoryChange(
        String userId,
        Long itemId,
        int before,
        int after,
        int delta
    ) {}
    
    //////////
    // Bows // 
    //////////
    
    private static final long[] BOW_ITEM_IDS = {1001L,1002L,1003L,1004L,1005L,1006L};
    
    @Transactional
	public Long checkForBows(String userId) {
    	// Check CD
    	Instant now = Instant.now();
        Timestamp prev = lastTimeSpeak.get(userId);
        if (prev != null) {
            if (prev.toInstant().isAfter(now.minus(15, ChronoUnit.SECONDS))) {
            	return null;
            } else {
            	lastTimeSpeak.replace(userId, Timestamp.from(now));
            }
        } else {
            lastTimeSpeak.put(userId, Timestamp.from(now));
        }
        
        // 1% chance
        if (ThreadLocalRandom.current().nextInt(100) >= 1) return null;
        
        // Give Bow
        long bowItemId = BOW_ITEM_IDS[ThreadLocalRandom.current().nextInt(BOW_ITEM_IDS.length)];
        giveItemToUser(userId, bowItemId, 1);
        
		return bowItemId;
	}
    
    @Transactional(readOnly = true)
    public boolean hasAllBows(String userId) {
        requireUser(userId);

        for (long bowId : BOW_ITEM_IDS) {
            int qty = repo.findByUserIdAndItemId(userId, bowId)
                    .map(UserInventory::getQuantity)
                    .orElse(0);
            if (qty <= 0) return false;
        }
        return true;
    }
    
    @Transactional
    public boolean exchangeAllBowsForCoins(String userId, int coins) {
        requireUser(userId);
        requirePositive(coins);
        // Check user has the bows
        if (!hasAllBows(userId)) return false;
        // Remove bows
        for (long bowId : BOW_ITEM_IDS) {
            removeItemFromUser(userId, bowId, 1);
        }
        // Give coins
        giveItemToUser(userId, 0L, coins);
        // Give bows to missy
        for (long bowId : BOW_ITEM_IDS) {
        	giveItemToUser("696347459751903293", bowId, 1);
        }        
        return true;
    }
    
    ///////////////
    ///////////////

    private static final Map<Long, Item> ITEMS = Map.of(
    		0000L, new Item("coin","🪙", "Coin", "Coins"),
    	    1001L, new Item("black_bow", "<:bow_black:1461128215010476133>", "Black Bow", "Black Bows"),
    	    1002L, new Item("white_bow", "<:bow_white:1461128235378278515>", "White Bow", "White Bows"),
    	    1003L, new Item("red_bow", "<:bow_red:1461128250817515695>", "Red Bow", "Red Bows"),
    	    1004L, new Item("pink_bow", "<:bow_pink:1461128264675492122>", "Pink Bow", "Pink Bows"),
    	    1005L, new Item("purple_bow", "<:bow_purple:1461128277312803029>", "Purple Bow", "Purple Bows"),
    	    1006L, new Item("yellow_bow", "<:bow_yellow:1461128289463828500>", "Yellow Bow", "Yellow Bows")
    	);
}
