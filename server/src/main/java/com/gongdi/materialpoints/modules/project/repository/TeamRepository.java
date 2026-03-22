package com.gongdi.materialpoints.modules.project.repository;

import com.gongdi.materialpoints.modules.project.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllByStatusOrderByIdAsc(String status);

    List<Team> findAllByProjectIdAndStatusOrderByIdAsc(Long projectId, String status);
}
