package com.arracso.ElfneinBot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arracso.ElfneinBot.model.EventXmasUser;

public interface EventXmasUserRepository extends JpaRepository<EventXmasUser, Long> {

	List<EventXmasUser> findByUser(String userId);
	
}
