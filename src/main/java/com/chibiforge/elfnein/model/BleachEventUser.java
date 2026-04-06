package com.chibiforge.elfnein.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "event_bleach_user")
public class BleachEventUser {
	
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
	@Column(name = "division")
	private String division;
	
	@Column(name = "shikai")
	private String shikai;
	@Column(name = "bankai")
	private String bankai;
	@Column(name = "reiatsu")
	private Integer reiatsu;
	
	@Column(name = "scaling")
	private Double scaling;
	
	@Column(name = "zanjutsu_lv")
	private Integer zanjutsuLv;
	@Column(name = "kido_lv")
	private Integer kidoLv;
	@Column(name = "hakuda_lv")
	private Integer hakudaLv;
	@Column(name = "hoho_lv")
	private Integer hohoLv;
	
	@Column(name = "train_cd")
	private Timestamp trainCd;
	@Column(name = "injure_cd")
	private Timestamp injureCd;
	@Column(name = "heal_cd")
	private Timestamp healCd;
	
	@Column(name = "demi_hollow_kills")
	private Integer demiHollowKills;
	@Column(name = "huge_hollow_kills")
	private Integer hugeHollowKills;
	@Column(name = "gillian_kills")
	private Integer gillianKills;
	@Column(name = "adjuchas_kills")
	private Integer adjuchasKills;
	@Column(name = "vasto_lorde_kills")
	private Integer vastoLordeKills;
	@Column(name = "arrancar_kills")
	private Integer arrancarKills;
	
	@Column(name = "injuries")
	private Integer injuries;
	@Column(name = "mortal_injuries")
	private Integer mortalInjuries;
	@Column(name = "healings")
	private Integer healings;
	@Column(name = "full_healings")
	private Integer fullHealings;
	
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

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getShikai() {
		return shikai;
	}

	public void setShikai(String shikai) {
		this.shikai = shikai;
	}

	public String getBankai() {
		return bankai;
	}

	public void setBankai(String bankai) {
		this.bankai = bankai;
	}

	public Integer getReiatsu() {
		return reiatsu;
	}

	public void setReiatsu(Integer reiatsu) {
		this.reiatsu = reiatsu;
	}
	
	public Double getScaling() {
		return scaling;
	}

	public void setScaling(Double scaling) {
		this.scaling = scaling;
	}
	
	public Integer getZanjutsuLv() {
		return zanjutsuLv;
	}

	public void setZanjutsuLv(Integer zanjutsuLv) {
		this.zanjutsuLv = zanjutsuLv;
	}

	public Integer getKidoLv() {
		return kidoLv;
	}

	public void setKidoLv(Integer kidoLv) {
		this.kidoLv = kidoLv;
	}

	public Integer getHakudaLv() {
		return hakudaLv;
	}

	public void setHakudaLv(Integer hakudaLv) {
		this.hakudaLv = hakudaLv;
	}

	public Integer getHohoLv() {
		return hohoLv;
	}

	public void setHohoLv(Integer hohoLv) {
		this.hohoLv = hohoLv;
	}

	public Timestamp getTrainCd() {
		return trainCd;
	}

	public void setTrainCd(Timestamp trainCd) {
		this.trainCd = trainCd;
	}

	public Timestamp getInjureCd() {
		return injureCd;
	}

	public void setInjureCd(Timestamp injureCd) {
		this.injureCd = injureCd;
	}
	
	public Timestamp getHealCd() {
		return healCd;
	}

	public void setHealCd(Timestamp healCd) {
		this.healCd = healCd;
	}
	
	public Integer getDemiHollowKills() {
		return demiHollowKills;
	}

	public void setDemiHollowKills(Integer demiHollowKills) {
		this.demiHollowKills = demiHollowKills;
	}

	public Integer getHugeHollowKills() {
		return hugeHollowKills;
	}

	public void setHugeHollowKills(Integer hugeHollowKills) {
		this.hugeHollowKills = hugeHollowKills;
	}

	public Integer getGillianKills() {
		return gillianKills;
	}

	public void setGillianKills(Integer gillianKills) {
		this.gillianKills = gillianKills;
	}

	public Integer getAdjuchasKills() {
		return adjuchasKills;
	}

	public void setAdjuchasKills(Integer adjuchasKills) {
		this.adjuchasKills = adjuchasKills;
	}

	public Integer getVastoLordeKills() {
		return vastoLordeKills;
	}

	public void setVastoLordeKills(Integer vastoLordeKills) {
		this.vastoLordeKills = vastoLordeKills;
	}

	public Integer getArrancarKills() {
		return arrancarKills;
	}

	public void setArrancarKills(Integer arrancarKills) {
		this.arrancarKills = arrancarKills;
	}
	
	public Integer getInjuries() {
		return injuries;
	}

	public void setInjuries(Integer injuries) {
		this.injuries = injuries;
	}

	public Integer getMortalInjuries() {
		return mortalInjuries;
	}

	public void setMortalInjuries(Integer mortalInjuries) {
		this.mortalInjuries = mortalInjuries;
	}

	public Integer getHealings() {
		return healings;
	}

	public void setHealings(Integer healings) {
		this.healings = healings;
	}

	public Integer getFullHealings() {
		return fullHealings;
	}

	public void setFullHealings(Integer fullHealings) {
		this.fullHealings = fullHealings;
	}
	
	// HELPERS
	
	public void increaseKills(String type) {
		if(type.equals("Demi-Hollow")) this.demiHollowKills++;
		else if(type.equals("Huge Hollow")) this.hugeHollowKills++;
		else if(type.equals("Gillian")) this.gillianKills++;
		else if(type.equals("Adjuchas")) this.adjuchasKills++;
		else if(type.equals("Vasto Lorde")) this.vastoLordeKills++;
		else if(type.equals("Arrancar")) this.arrancarKills++;
		else if(type.equals("Resurreccion")) this.arrancarKills++;
	}

	public boolean canLearnShikai() {
		return shikai == null && reiatsu>500 && (int)(Math.random() * 20) <= zanjutsuLv-20;
	}

	public boolean canLearnBankai() {
		return bankai == null && reiatsu>3000 && (int)(Math.random() * 20) <= zanjutsuLv-60;
	}
	
	public int getTotalHollowKills() {
		return demiHollowKills + hugeHollowKills + gillianKills + adjuchasKills + vastoLordeKills + arrancarKills;
	}
	
	public boolean hasMedicalRecord() {
		// TODO Auto-generated method stub
		return injuries>0 || mortalInjuries>0 || healings>0 || fullHealings>0;
	}
	
}
