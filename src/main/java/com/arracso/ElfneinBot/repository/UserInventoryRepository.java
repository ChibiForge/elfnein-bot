package com.arracso.ElfneinBot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arracso.ElfneinBot.model.UserInventory;

public interface UserInventoryRepository extends JpaRepository<UserInventory, Long> {
	
	List<UserInventory> findAllByUserIdOrderByItemIdAsc(String userId);
	
	Optional<UserInventory> findByUserIdAndItemId(String userId, Long itemId);
	
	boolean existsByUserIdAndItemId(String userId, Long itemId);
	
	void deleteByUserIdAndItemId(String userId, Long itemId);
	
}
