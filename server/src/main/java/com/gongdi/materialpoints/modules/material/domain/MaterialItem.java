package com.gongdi.materialpoints.modules.material.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "material_item")
public class MaterialItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String unitCode;

    @Column(nullable = false)
    private String status;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public String getStatus() {
        return status;
    }
}
