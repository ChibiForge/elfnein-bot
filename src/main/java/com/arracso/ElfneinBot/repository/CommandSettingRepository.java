package com.arracso.ElfneinBot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arracso.ElfneinBot.model.CommandSetting;

public interface CommandSettingRepository extends JpaRepository<CommandSetting, Long> {
	
	List<CommandSetting> findByCommand(Integer command);

}
