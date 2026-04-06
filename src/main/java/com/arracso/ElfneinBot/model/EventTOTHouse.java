package com.arracso.ElfneinBot.model;

import java.sql.Timestamp;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "event_tot_house")
public class EventTOTHouse {
	
	////////////////
	// Attributes //
	////////////////
	
	@Id @Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name= "server")
	private String server;
	@Column(name = "position")
	private Integer position;
	@Column(name = "members")
	private String members;
	
	@Column(name = "coins")
	private Integer coins;
	@Column(name = "candy")
	private Integer candy;
	@Column(name = "candy_given")
	private Integer candyGiven;
	@Column(name = "candy_wrappers")
	private Integer candyWrappers;
	@Column(name = "eggs")
	private Integer eggs;
	@Column(name = "egg_throws")
	private Integer eggThrows;
	@Column(name = "eggshells")
	private Integer eggshells;
	@Column(name = "toilet_paper_rolls")
	private Integer toiletPaperRolls;
	@Column(name = "toilet_paper_roll_throws")
	private Integer toiletPaperRollThrows;
	@Column(name = "toilet_paper_scraps")
	private Integer toiletPaperScraps;
	
	@Column(name = "dirty_time")
	private Integer dirtyTime;
	@Column(name = "dirty_state")
	private Integer dirtyState;
	@Column(name = "dirty_end")
	private Timestamp dirtyEnd;

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

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getMembers() {
		return members;
	}

	public void setMembers(String members) {
		this.members = members;
	}

	public Integer getCoins() {
		return coins;
	}

	public void setCoins(Integer coins) {
		this.coins = coins;
	}

	public Integer getCandy() {
		return candy;
	}

	public void setCandy(Integer candy) {
		this.candy = candy;
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

	public Integer getEggs() {
		return eggs;
	}

	public void setEggs(Integer eggs) {
		this.eggs = eggs;
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

	public Integer getToiletPaperRolls() {
		return toiletPaperRolls;
	}

	public void setToiletPaperRolls(Integer toiletPaperRolls) {
		this.toiletPaperRolls = toiletPaperRolls;
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

	public Integer getDirtyTime() {
		return dirtyTime;
	}

	public void setDirtyTime(Integer dirtyTime) {
		this.dirtyTime = dirtyTime;
	}
	
	public Integer getDirtyState() {
		return dirtyState;
	}

	public void setDirtyState(Integer dirtyState) {
		this.dirtyState = dirtyState;
	}
	
	public Timestamp getDirtyEnd() {
		return dirtyEnd;
	}

	public void setDirtyEnd(Timestamp dirtyEnd) {
		this.dirtyEnd = dirtyEnd;
	}

	public int getTotalInventory() {
		return this.candy + this.eggs + this.toiletPaperRolls;
	}

	public int getTotalTrash() {
		return this.candyWrappers + this.eggshells + this.toiletPaperScraps;
	}

	public int getTotalActions() {
		return this.candyGiven + this.eggThrows + this.toiletPaperRollThrows;
	}
	
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof EventTOTHouse other)) return false;
	    return id != null && id.equals(other.id);
	}

	@Override
	public int hashCode() {
	    return Objects.hashCode(id);
	}
	
}
