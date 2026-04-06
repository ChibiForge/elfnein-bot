package com.arracso.ElfneinBot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arracso.ElfneinBot.model.UserServerActivity;

public interface UserServerActivityRepository extends JpaRepository<UserServerActivity, Long> {
	
	List<UserServerActivity> findByServerAndUser(String server, String user);

	List<UserServerActivity> findByServer(String serverId);
	
	List<UserServerActivity> findByServerOrderByExperienceDesc(String serverId);
	
}
