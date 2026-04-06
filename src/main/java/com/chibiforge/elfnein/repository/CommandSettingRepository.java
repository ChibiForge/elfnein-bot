package com.chibiforge.elfnein.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chibiforge.elfnein.model.CommandSetting;

public interface CommandSettingRepository extends JpaRepository<CommandSetting, Long> {
	
	List<CommandSetting> findByCommand(Integer command);

}
