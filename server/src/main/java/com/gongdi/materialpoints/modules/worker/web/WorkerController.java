package com.gongdi.materialpoints.modules.worker.web;

import com.gongdi.materialpoints.common.api.ApiResponse;
import com.gongdi.materialpoints.modules.worker.domain.Worker;
import com.gongdi.materialpoints.modules.worker.repository.WorkerRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    private final WorkerRepository workerRepository;

    public WorkerController(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    @GetMapping
    public ApiResponse<List<Worker>> list() {
        return ApiResponse.success(workerRepository.findAllByStatusOrderByIdAsc("ENABLED"));
    }
}
