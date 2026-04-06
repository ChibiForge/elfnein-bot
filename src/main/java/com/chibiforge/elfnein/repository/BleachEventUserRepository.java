package com.chibiforge.elfnein.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chibiforge.elfnein.model.BleachEventUser;

public interface BleachEventUserRepository extends JpaRepository<BleachEventUser, Long> {
	
	List<BleachEventUser> findByServerAndUser(String server, String user);
	
	List<BleachEventUser> findByServerOrderByReiatsuDesc(String serverId);
	@Query("SELECT b FROM BleachEventUser b WHERE b.server = :serverId ORDER BY (b.healings + b.fullHealings) DESC, b.fullHealings DESC, b.reiatsu DESC")
	List<BleachEventUser> findByServerOrderByTotalHealingsDesc(@Param("serverId") String serverId);

	List<BleachEventUser> findByShikai(String name);
	List<BleachEventUser> findByBankai(String name);
	
}
