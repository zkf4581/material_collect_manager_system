package com.gongdi.materialpoints.modules.recycle.repository;

import com.gongdi.materialpoints.modules.recycle.domain.RecycleRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecycleRecordRepository extends JpaRepository<RecycleRecord, Long> {
}
