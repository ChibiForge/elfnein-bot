package com.chibiforge.elfnein.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chibiforge.elfnein.model.EventXmasUser;

public interface EventXmasUserRepository extends JpaRepository<EventXmasUser, Long> {

	List<EventXmasUser> findByUser(String userId);
	
}
