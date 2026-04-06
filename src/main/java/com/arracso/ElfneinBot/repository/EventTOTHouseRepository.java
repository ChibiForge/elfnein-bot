package com.arracso.ElfneinBot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.arracso.ElfneinBot.model.EventTOTHouse;

public interface EventTOTHouseRepository extends JpaRepository<EventTOTHouse, Long> {
	
	List<EventTOTHouse> findByServerAndPosition(String server, Integer position);

	List<EventTOTHouse> findByServerOrderByCandyWrappersDesc(String serverId);
	List<EventTOTHouse> findByServerOrderByCandyGivenDesc(String serverId);
	List<EventTOTHouse> findByServerOrderByDirtyTimeDesc(String serverId);
	
	@Query(value = "SELECT * FROM event_tot_house WHERE server = :serverId ORDER BY (egg_throws + toilet_paper_roll_throws) DESC, toilet_paper_roll_throws DESC, egg_throws DESC", nativeQuery = true)
	List<EventTOTHouse> findByServerOrderByTricksDesc(@Param("serverId") String serverId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE event_tot_house SET position = FLOOR(1 + RAND() * 18) WHERE position <> 0 AND server = :server", nativeQuery = true)
	int randomizePositions(@Param("server") String serverId);
	
}