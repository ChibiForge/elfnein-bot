package com.chibiforge.elfnein.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chibiforge.elfnein.model.BannedUser;

public interface BannedUserRepository extends JpaRepository<BannedUser, Long> {
	
	List<BannedUser> findByUser(String user);
	
	
}
