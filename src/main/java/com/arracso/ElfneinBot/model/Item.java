package com.arracso.ElfneinBot.model;

public class Item {
	
	private String name;
    private String icon;
    private String name_s;
    private String name_p;
    
    public Item(String name, String icon, String name_s, String name_p) {
        this.name = name;
        this.icon = icon;
        this.name_s = name_s;
        this.name_p = name_p;
    }
    
    ///////////////////////
    // Getters i setters //
    ///////////////////////
    
    public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getIcon() { return icon; }
	public void setIcon(String icon) { this.icon = icon; }
	
	public String getName_s() { return name_s; }
	public void setName_s(String name_s) { this.name_s = name_s; }
	
	public String getName_p() { return name_p; }
	public void setName_p(String name_p) { this.name_p = name_p; }
	
	////////////////////////
	
	public String getNameAndIcon(Boolean plural) {
        return this.icon + " " + (plural?this.name_p:this.name_s);
	}
}