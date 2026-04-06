package com.arracso.ElfneinBot.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "user_server_activity")
public class UserServerActivity {

	////////////////
	// Attributes //
	////////////////
	
	@Id @Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "server")
	private String server;
	@Column(name = "user")
	private String user;
	@Column(name = "experience")
	private Long experience;
	@Column(name = "level")
	private Integer level;
	@Column(name = "messages")
	private Long messages;
	@Column(name = "last_activity")
	private Timestamp lastActivity;
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Long getExperience() {
		return experience;
	}

	public void setExperience(Long experience) {
		this.experience = experience;
	}

	public Long getMessages() {
		return messages;
	}

	public void setMessages(Long messages) {
		this.messages = messages;
	}

	public Timestamp getLastActivity() {
		return lastActivity;
	}

	public void setLastActivity(Timestamp lastActivity) {
		this.lastActivity = lastActivity;
	}
	
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
		
	}
	
}
