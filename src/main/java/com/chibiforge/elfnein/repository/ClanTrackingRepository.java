package com.chibiforge.elfnein.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chibiforge.elfnein.model.ClanTracking;

public interface ClanTrackingRepository extends JpaRepository<ClanTracking, Long> {
	
	List<ClanTracking> findByUser(String user);

	List<ClanTracking> findByShogun(String shogun);

	List<ClanTracking> findByUserIn(List<String> user);
	
}
