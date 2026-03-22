package com.gongdi.materialpoints.modules.reward.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reward_item")
public class RewardItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer pointsCost;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private String status;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPointsCost() {
        return pointsCost;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }
}
