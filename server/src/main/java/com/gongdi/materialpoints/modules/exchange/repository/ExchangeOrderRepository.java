package com.gongdi.materialpoints.modules.exchange.repository;

import com.gongdi.materialpoints.modules.exchange.domain.ExchangeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeOrderRepository extends JpaRepository<ExchangeOrder, Long> {

    List<ExchangeOrder> findAllByWorkerIdOrderByIdDesc(Long workerId);
}
