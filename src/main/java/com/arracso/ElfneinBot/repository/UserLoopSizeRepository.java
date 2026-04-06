package com.arracso.ElfneinBot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arracso.ElfneinBot.model.UserLoopSize;

public interface UserLoopSizeRepository extends JpaRepository<UserLoopSize, Long> {
	
	List<UserLoopSize> findByUser(String user);
	
}
