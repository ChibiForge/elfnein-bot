package com.arracso.ElfneinBot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arracso.ElfneinBot.model.BannedUser;

public interface BannedUserRepository extends JpaRepository<BannedUser, Long> {
	
	List<BannedUser> findByUser(String user);
	
	
}
