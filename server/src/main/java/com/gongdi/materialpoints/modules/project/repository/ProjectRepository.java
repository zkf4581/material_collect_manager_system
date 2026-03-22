package com.gongdi.materialpoints.modules.project.repository;

import com.gongdi.materialpoints.modules.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByStatusOrderByIdAsc(String status);
}
