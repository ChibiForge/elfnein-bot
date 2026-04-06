package com.chibiforge.elfnein.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "user_inventory")
public class UserInventory {
	
	////////////////
	// Attributes //
	////////////////
	
	@Id @Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "user_id", nullable = false)
	private String userId;
	@Column(name = "item_id", nullable = false)
	private Long itemId;
	@Column(name = "quantity", nullable = false)
	private int quantity;
	
	//////////////
	// Contruct //
	//////////////
	
	protected UserInventory() {}

    public UserInventory(String userId, Long itemId, int quantity) {
        this.userId = userId;
        this.itemId = itemId;
        this.quantity = quantity;
    }
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
	
}
