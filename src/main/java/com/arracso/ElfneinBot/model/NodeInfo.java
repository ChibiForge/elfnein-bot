package com.arracso.ElfneinBot.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "node_info")
public class NodeInfo {

	////////////////
	// Attributes //
	////////////////
	
	@Id @Column(name = "node")
	private String node;
	@Column(name = "managed")
	private Boolean managed;
	@Column(name = "holder")
	private String holder;
	@Column(name = "decay")
	private Integer decay;
	@Column(name = "grace")
	private Integer grace;
	@Column(name = "updated")
	private Timestamp updated;
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	
	public String getNode() {
		return node;
	}
	
	public void setNode(String node) {
		this.node = node;
	}
	
	public Boolean getManaged() {
		return managed;
	}

	public void setManaged(Boolean managed) {
		this.managed = managed;
	}
	
	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}
	
	public Integer getDecay() {
		return decay;
	}
	
	public void setDecay(Integer decay) {
		this.decay = decay;
	}
	
	public Integer getGrace() {
		return grace;
	}
	
	public void setGrace(Integer grace) {
		this.grace = grace;
	}

	public Timestamp getUpdated() {
		return updated;
	}

	public void setUpdated(Timestamp updated) {
		this.updated = updated;
	}
	
}
