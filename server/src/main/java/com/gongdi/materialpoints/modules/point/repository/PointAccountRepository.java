package com.gongdi.materialpoints.modules.point.repository;

import com.gongdi.materialpoints.modules.point.domain.PointAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointAccountRepository extends JpaRepository<PointAccount, Long> {

    Optional<PointAccount> findByProjectIdAndWorkerId(Long projectId, Long workerId);
}
