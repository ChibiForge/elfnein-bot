package com.arracso.ElfneinBot.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "event_tot_user")
public class EventTOTUser {
	
	////////////////
	// Attributes //
	////////////////
	
	@Id @Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name= "server")
	private String server;
	@Column(name = "user")
	private String user;
	@Column(name = "house")
	private Long house;
	
	@Column(name = "coins_earned")
	private Integer coinsEarned;
	@Column(name = "candy_given")
	private Integer candyGiven;
	@Column(name = "candy_wrappers")
	private Integer candyWrappers;
	@Column(name = "egg_throws")
	private Integer eggThrows;
	@Column(name = "eggshells")
	private Integer eggshells;
	@Column(name = "toilet_paper_roll_throws")
	private Integer toiletPaperRollThrows;
	@Column(name = "toilet_paper_scraps")
	private Integer toiletPaperScraps;
	
	@Column(name = "cd_home")
	private Timestamp cdHome;
	@Column(name = "cd_clean")
	private Timestamp cdClean;
	
	
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

	public Long getHouse() {
		return house;
	}

	public void setHouse(Long house) {
		this.house = house;
	}

	public Integer getCoinsEarned() {
		return coinsEarned;
	}

	public void setCoinsEarned(Integer coinsEarned) {
		this.coinsEarned = coinsEarned;
	}

	public Integer getCandyGiven() {
		return candyGiven;
	}

	public void setCandyGiven(Integer candyGiven) {
		this.candyGiven = candyGiven;
	}

	public Integer getCandyWrappers() {
		return candyWrappers;
	}

	public void setCandyWrappers(Integer candyWrappers) {
		this.candyWrappers = candyWrappers;
	}

	public Integer getEggThrows() {
		return eggThrows;
	}

	public void setEggThrows(Integer eggThrows) {
		this.eggThrows = eggThrows;
	}

	public Integer getEggshells() {
		return eggshells;
	}

	public void setEggshells(Integer eggshells) {
		this.eggshells = eggshells;
	}

	public Integer getToiletPaperRollThrows() {
		return toiletPaperRollThrows;
	}

	public void setToiletPaperRollThrows(Integer toiletPaperRollThrows) {
		this.toiletPaperRollThrows = toiletPaperRollThrows;
	}

	public Integer getToiletPaperScraps() {
		return toiletPaperScraps;
	}

	public void setToiletPaperScraps(Integer toiletPaperScraps) {
		this.toiletPaperScraps = toiletPaperScraps;
	}

	public Timestamp getCdHome() {
		return cdHome;
	}

	public void setCdHome(Timestamp cdHome) {
		this.cdHome = cdHome;
	}

	public Timestamp getCdClean() {
		return cdClean;
	}

	public void setCdClean(Timestamp cdClean) {
		this.cdClean = cdClean;
	}
	
}
