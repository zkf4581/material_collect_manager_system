package com.gongdi.materialpoints.modules.point.repository;

import com.gongdi.materialpoints.modules.point.domain.PointLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointLedgerRepository extends JpaRepository<PointLedger, Long> {
}
