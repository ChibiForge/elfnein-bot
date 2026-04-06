package com.chibiforge.elfnein.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "command_setting")
public class CommandSetting {
	
	////////////////
	// Attributes //
	////////////////
	
	// TODO add category
	
	@Id @Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "command")
	private Integer command;
	@Column(name = "server")
	private String server;
	@Column(name = "channel")
	private String channel;
	@Column(name = "user")
	private String user;
	@Column(name = "active")
	private Boolean active;
	
    /////////////////////////
    // Getters and setters //
    /////////////////////////
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getCommand() {
		return command;
	}
	public void setCommand(Integer commandId) {
		this.command = commandId;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	
}
