package com.chibiforge.elfnein.util.DateSolver;

public class DateStats {
	public int fuel;
	public int food;
	public int drink;
	public int entertainment;
	public int time;
	public int AP;
	public Boolean ring;
	public Boolean house;
	public int cd;
	
	public DateStats() {
		this(100,50,50,75,100,0,false,false,0);
	}
	
	public DateStats(int fuel,int food,int drink,int entertainment, int time, int AP) {
		this(fuel,food,drink,entertainment,time,AP,null,null);		
	}
	
	public DateStats(int fuel,int food,int drink,int entertainment, int time, int AP, Boolean ring, Boolean house) {
		this(fuel,food,drink,entertainment,time,AP,ring,house,10);		
	}
	
	public DateStats(int fuel,int food,int drink,int entertainment, int time, int AP, Boolean ring, Boolean house, int cd) {
		this.fuel = fuel;
		this.food = food;
		this.drink = drink;
		this.entertainment = entertainment;
		this.time = time;
		this.AP = AP;
		this.ring = ring;
		this.house = house;
		this.cd = cd;
		
		if(this.fuel>100) this.fuel = 100;
		if(this.food>100) this.food = 100;
		if(this.drink>100) this.drink = 100;
		if(this.entertainment>100) this.entertainment = 100;
	}
	
	public boolean isOver() {
		return fuel <=0 || food <=0 || drink <=0 || entertainment <=0 || time <= 0;
	}

	public boolean isSolution() {
		return time <= 0 && fuel > 0 && food > 0 && drink > 0 && entertainment > 0 || house;
	}

	public DateStats add(DateStats stats) {
		return new DateStats(this.fuel+stats.fuel,this.food+stats.food,this.drink+stats.drink,this.entertainment+stats.entertainment,this.time+stats.time,this.AP+stats.AP,this.ring||stats.ring,stats.house);
	}

	public int getPuntuation() {
		int points = this.AP;
		points += (((this.food + this.drink + this.entertainment)*(100-this.time)-1)/600) + 1;
		if(ring) points += 100;
		return points;
	}
	
	public String getRealPoints() {
		String res = "+" + ((((this.food + this.drink + this.entertainment)*(100-this.time)-1)/600) + 1) + " AP";
		if(this.AP > 0) res = res + " + (" + this.AP + " AP)";
		return res;
	}
}