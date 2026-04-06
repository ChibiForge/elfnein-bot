package com.arracso.ElfneinBot.game.bleach;

public class Hollow {
	
	private String tpye;
	private String name;
	private String imageUrl;
	private Integer reiatsu;
	private Integer ferocity; // Cero
	private Integer resilience; // Hierro
	private Integer instinct; // Pesquisa
	private Integer agility; // Sonido
	private Hollow resurreccion;
	
	public Hollow() {
		this("Demi-Hollow",null,"https://i.postimg.cc/PxypBrb3/Demi-Hollow-0.png",75,2,2,2,2);
	}
	
	public Hollow(String type, String name, String imageUrl, Integer reiatsu, Integer ferocity, Integer resilience, Integer instinct, Integer agility) {
		this(type, name, imageUrl, reiatsu, ferocity, resilience, instinct, agility,null);
	}
	
	public Hollow(String type, String name, String imageUrl, Integer reiatsu, Integer ferocity, Integer resilience, Integer instinct, Integer agility, Hollow resurreccion) {
		this.tpye = type;
		this.name = name;
		this.imageUrl = imageUrl;
		this.reiatsu = reiatsu;
		this.ferocity = ferocity;
		this.resilience = resilience;
		this.instinct = instinct;
		this.agility = agility;
		this.resurreccion = resurreccion;
	}
	
	public Hollow(Hollow other) {
		if (other != null) {
            this.tpye = other.tpye;
            this.name = other.name;
            this.imageUrl = other.imageUrl;
            this.reiatsu = other.reiatsu;
            this.ferocity = other.ferocity;
            this.resilience = other.resilience;
            this.instinct = other.instinct;
            this.agility = other.agility;
            this.resurreccion = other.resurreccion != null ? new Hollow(other.resurreccion) : null;
        }
	}

	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getTpye() {
		return tpye;
	}
	public void setTpye(String tpye) {
		this.tpye = tpye;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getReiatsu() {
		return reiatsu;
	}
	public void setReiatsu(Integer reiatsu) {
		this.reiatsu = reiatsu;
	}
	public Integer getFerocity() {
		return ferocity;
	}
	public void setFerocity(Integer ferocity) {
		this.ferocity = ferocity;
	}
	public Integer getResilience() {
		return resilience;
	}
	public void setResilience(Integer resilience) {
		this.resilience = resilience;
	}
	public Integer getInstinct() {
		return instinct;
	}
	public void setInstinct(Integer instinct) {
		this.instinct = instinct;
	}
	public Integer getAgility() {
		return agility;
	}
	public void setAgility(Integer agility) {
		this.agility = agility;
	}

	public Hollow getResurreccion() {
		return resurreccion;
	}

	public void setResurreccion(Hollow resurreccion) {
		this.resurreccion = resurreccion;
	}
	
	

}
