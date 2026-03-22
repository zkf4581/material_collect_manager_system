package com.gongdi.materialpoints.modules.worker.repository;

import com.gongdi.materialpoints.modules.worker.domain.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    List<Worker> findAllByStatusOrderByIdAsc(String status);
}
