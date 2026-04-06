package com.chibiforge.elfnein.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chibiforge.elfnein.model.UserLoopSize;

public interface UserLoopSizeRepository extends JpaRepository<UserLoopSize, Long> {
	
	List<UserLoopSize> findByUser(String user);
	
}
