package com.chibiforge.elfnein.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "user_loop_size")
public class UserLoopSize {

	////////////////
	// Attributes //
	////////////////
	
	@Id @Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "user")
	private String user;
	@Column(name = "size")
	private Integer size;
	
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
	
	public Integer geSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
		
	}
	
}
