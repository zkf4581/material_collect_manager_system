package com.gongdi.materialpoints.modules.file.repository;

import com.gongdi.materialpoints.modules.file.domain.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
}
