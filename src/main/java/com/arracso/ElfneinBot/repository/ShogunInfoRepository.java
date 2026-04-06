package com.arracso.ElfneinBot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arracso.ElfneinBot.model.ShogunInfo;

public interface ShogunInfoRepository extends JpaRepository<ShogunInfo, String> {
	
	Optional<ShogunInfo> findById(String id);
	
}
