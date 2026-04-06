package com.arracso.ElfneinBot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "shogun_info")
public class ShogunInfo {

	////////////////
	// Attributes //
	////////////////

	@Id @Column(name = "id")
	private String id;
	@Column(name = "side")
	private Integer side;

	/////////////////////////
	// Getters and setters //
	/////////////////////////
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Integer getSide() {
		return side;
	}
	
	public void setSide(Integer side) {
		this.side = side;
	}
	
}
