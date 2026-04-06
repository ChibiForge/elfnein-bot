package com.arracso.ElfneinBot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arracso.ElfneinBot.model.NodeInfo;

public interface NodeInfoRepository extends JpaRepository<NodeInfo, String> {
	
	Optional<NodeInfo> findById(String node);
	
	List<NodeInfo> findAllByOrderByGraceAsc();
	
}
