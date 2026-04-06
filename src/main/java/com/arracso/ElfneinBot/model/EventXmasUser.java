package com.arracso.ElfneinBot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "event_xmas_user")
public class EventXmasUser {
	
	////////////////
	// Attributes //
	////////////////
	
	@Id @Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "user")
	private String user;
	@Column(name = "type")
	private Integer type;
	
	@Column(name = "coins")
	private Integer coins;
	@Column(name = "wood")
	private Integer wood;
	@Column(name = "paint")
	private Integer paint;
	
	@Column(name = "crafting_toy")
	private Integer craftingToy;
	@Column(name = "crafting_progression")
	private Integer craftingProgression;
	
	@Column(name = "wooden_cars_crafted")
	private Integer woodenCarsCrafted;
	@Column(name = "wooden_cars_given")
	private Integer woodenCarsGiven;
	@Column(name = "wooden_cars_received")
	private Integer woodenCarsReceived;
	
	@Column(name = "wooden_dolls_crafted")
	private Integer woodenDollsCrafted;
	@Column(name = "wooden_dolls_given")
	private Integer woodenDollsGiven;
	@Column(name = "wooden_dolls_received")
	private Integer woodenDollsReceived;
	
	@Column(name = "wooden_marble_runs_crafted")
	private Integer woodenMarbleRunsCrafted;
	@Column(name = "wooden_marble_runs_given")
	private Integer woodenMarbleRunsGiven;
	@Column(name = "wooden_marble_runs_received")
	private Integer woodenMarbleRunsReceived;
	
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getCoins() {
		return coins;
	}
	public void setCoins(Integer coins) {
		this.coins = coins;
	}
	public Integer getWood() {
		return wood;
	}
	public void setWood(Integer wood) {
		this.wood = wood;
	}
	public Integer getPaint() {
		return paint;
	}
	public void setPaint(Integer paint) {
		this.paint = paint;
	}
	public Integer getCraftingToy() {
		return craftingToy;
	}
	public void setCraftingToy(Integer craftingToy) {
		this.craftingToy = craftingToy;
	}
	public Integer getCraftingProgression() {
		return craftingProgression;
	}
	public void setCraftingProgression(Integer craftingProgression) {
		this.craftingProgression = craftingProgression;
	}
	public Integer getWoodenCarsCrafted() {
		return woodenCarsCrafted;
	}
	public void setWoodenCarsCrafted(Integer woodenCarsCrafted) {
		this.woodenCarsCrafted = woodenCarsCrafted;
	}
	public Integer getWoodenCarsGiven() {
		return woodenCarsGiven;
	}
	public void setWoodenCarsGiven(Integer woodenCarsGiven) {
		this.woodenCarsGiven = woodenCarsGiven;
	}
	public Integer getWoodenCarsReceived() {
		return woodenCarsReceived;
	}
	public void setWoodenCarsReceived(Integer woodenCarsReceived) {
		this.woodenCarsReceived = woodenCarsReceived;
	}
	public Integer getWoodenDollsCrafted() {
		return woodenDollsCrafted;
	}
	public void setWoodenDollsCrafted(Integer woodenDollsCrafted) {
		this.woodenDollsCrafted = woodenDollsCrafted;
	}
	public Integer getWoodenDollsGiven() {
		return woodenDollsGiven;
	}
	public void setWoodenDollsGiven(Integer woodenDollsGiven) {
		this.woodenDollsGiven = woodenDollsGiven;
	}
	public Integer getWoodenDollsReceived() {
		return woodenDollsReceived;
	}
	public void setWoodenDollsReceived(Integer woodenDollsReceived) {
		this.woodenDollsReceived = woodenDollsReceived;
	}
	public Integer getWoodenMarbleRunsCrafted() {
		return woodenMarbleRunsCrafted;
	}
	public void setWoodenMarbleRunsCrafted(Integer woodenMarbleRunsCrafted) {
		this.woodenMarbleRunsCrafted = woodenMarbleRunsCrafted;
	}
	public Integer getWoodenMarbleRunsGiven() {
		return woodenMarbleRunsGiven;
	}
	public void setWoodenMarbleRunsGiven(Integer woodenMarbleRunsGiven) {
		this.woodenMarbleRunsGiven = woodenMarbleRunsGiven;
	}
	public Integer getWoodenMarbleRunsReceived() {
		return woodenMarbleRunsReceived;
	}
	public void setWoodenMarbleRunsReceived(Integer woodenMarbleRunsReceived) {
		this.woodenMarbleRunsReceived = woodenMarbleRunsReceived;
	}
	
	public int getTotalUtilities() {
		return this.getWood() + this.getPaint();
	}
	public int getCraftedToys() {
		return this.woodenCarsCrafted + this.woodenDollsCrafted + this.woodenMarbleRunsCrafted;
	}
	
}
