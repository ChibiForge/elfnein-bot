package com.chibiforge.elfnein.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "banned_user")
public class BannedUser {

	////////////////
	// Attributes //
	////////////////
	
	@Id @Column(name = "id")
	private Long id;
	@Column(name = "user")
	private String user;
	@Column(name= "reason")
	private String reason;
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getReason() {
		return reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
