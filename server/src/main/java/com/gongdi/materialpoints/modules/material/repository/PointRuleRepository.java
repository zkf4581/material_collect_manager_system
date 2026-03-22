package com.gongdi.materialpoints.modules.material.repository;

import com.gongdi.materialpoints.modules.material.domain.PointRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRuleRepository extends JpaRepository<PointRule, Long> {

    List<PointRule> findAllByStatusOrderByIdAsc(String status);
}
