package com.gongdi.materialpoints.modules.recycle.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "recycle_photo")
public class RecyclePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long recycleRecordId;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private String storageType;

    @Column(nullable = false)
    private Long fileSize;

    public void setRecycleRecordId(Long recycleRecordId) {
        this.recycleRecordId = recycleRecordId;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
