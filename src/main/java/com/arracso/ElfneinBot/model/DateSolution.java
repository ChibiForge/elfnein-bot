package com.arracso.ElfneinBot.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import com.arracso.ElfneinBot.util.DateSolver.DateStats;
import com.google.common.collect.Lists;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "date_solution")
public class DateSolution {

	////////////////
	// Attributes //
	////////////////
	
	@Id @Column(name = "card_code")
	private String cardCode;
	@Column(name = "food")
	private int food;
	@Column(name = "drink")
	private int drink;
	@Column(name = "entertainment")
	private int entertainment;
	@Column(name = "time")
	private int time;
	@Column(name = "ap")
	private int AP;
	@Column(name = "actions")
	private String actions;
	@Column(name = "updated")
	private Timestamp updated;
	@Column(name = "called")
	private Timestamp called;
	
	///////////////
	// Functions //
	///////////////
	
	public DateSolution() {
	}
	
	public DateSolution(String cardCode, DateStats stats, List<Character> actions, Timestamp now) {
		this.cardCode = cardCode;
		this.food = stats.food;
		this.drink = stats.drink;
		this.entertainment = stats.entertainment;
		this.time = stats.time;
		this.AP = stats.AP;
		this.actions = actions.stream().map(String::valueOf).collect(Collectors.joining());
		this.updated = now;
		this.called = now;
	}
	
	public DateStats getStats() {
		return new DateStats(100,this.food,this.drink,this.entertainment,this.time,this.AP);
	}
	
	public List<Character> getActionsList() {
		return Lists.charactersOf(actions);
	}
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	
	public String getCardCode() {
		return cardCode;
	}
	
	public void setCardCode(String cardCode) {
		this.cardCode = cardCode;
	}

	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		this.food = food;
	}

	public int getDrink() {
		return drink;
	}
	
	public void setDrink(int drink) {
		this.drink = drink;
	}

	public int getEntertainment() {
		return entertainment;
	}

	public void setEntertainment(int entertainment) {
		this.entertainment = entertainment;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getAP() {
		return AP;
	}

	public void setAP(int aP) {
		AP = aP;
	}

	public String getActions() {
		return actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	public Timestamp getUpdated() {
		return updated;
	}

	public void setUpdated(Timestamp updated) {
		this.updated = updated;
	}
	
	public Timestamp getCalled() {
		return called;
	}

	public void setCalled(Timestamp called) {
		this.called = called;
	}
	
}
