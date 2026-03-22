package com.gongdi.materialpoints.modules.material.repository;

import com.gongdi.materialpoints.modules.material.domain.MaterialItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialItemRepository extends JpaRepository<MaterialItem, Long> {

    List<MaterialItem> findAllByStatusOrderByIdAsc(String status);
}
