package com.chibiforge.elfnein.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chibiforge.elfnein.model.EventTOTUser;

public interface EventTOTUserRepository extends JpaRepository<EventTOTUser, Long> {
	
	List<EventTOTUser> findByServerAndUser(String server, String user);
	Optional<EventTOTUser> findFirstByServerAndUser(String server, String user);
	List<EventTOTUser> findByServerOrderByCandyWrappersDesc(String server);
	List<EventTOTUser> findByHouse(Long id);
}
