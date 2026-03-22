package com.gongdi.materialpoints.modules.point.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "point_ledger")
public class PointLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pointAccountId;

    @Column(nullable = false)
    private Long workerId;

    @Column(nullable = false)
    private Long projectId;

    @Column(nullable = false)
    private String bizType;

    @Column(nullable = false)
    private Long bizId;

    @Column(nullable = false)
    private Integer changeAmount;

    @Column(nullable = false)
    private Integer balanceAfter;

    @Column
    private String remark;

    public Long getId() {
        return id;
    }

    public void setPointAccountId(Long pointAccountId) {
        this.pointAccountId = pointAccountId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public void setChangeAmount(Integer changeAmount) {
        this.changeAmount = changeAmount;
    }

    public void setBalanceAfter(Integer balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
