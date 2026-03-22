package com.gongdi.materialpoints.modules.material.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "point_rule")
public class PointRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long materialItemId;

    @Column(nullable = false)
    private String unitCode;

    @Column(nullable = false)
    private BigDecimal basePoint;

    @Column(nullable = false)
    private String conditionCode;

    @Column(nullable = false)
    private BigDecimal conditionFactor;

    @Column(nullable = false)
    private String status;

    public Long getId() {
        return id;
    }

    public Long getMaterialItemId() {
        return materialItemId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public BigDecimal getBasePoint() {
        return basePoint;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public BigDecimal getConditionFactor() {
        return conditionFactor;
    }

    public String getStatus() {
        return status;
    }
}
