package com.chibiforge.elfnein.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chibiforge.elfnein.model.ShogunInfo;

public interface ShogunInfoRepository extends JpaRepository<ShogunInfo, String> {
	
	Optional<ShogunInfo> findById(String id);
	
}
