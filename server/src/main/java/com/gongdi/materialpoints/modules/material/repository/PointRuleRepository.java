package com.gongdi.materialpoints.modules.material.repository;

import com.gongdi.materialpoints.modules.material.domain.PointRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PointRuleRepository extends JpaRepository<PointRule, Long> {

    List<PointRule> findAllByStatusOrderByIdAsc(String status);

    Optional<PointRule> findFirstByMaterialItemIdAndUnitCodeAndConditionCodeAndStatus(
            Long materialItemId,
            String unitCode,
            String conditionCode,
            String status
    );
}
