package com.gongdi.materialpoints.modules.project.web;

import com.gongdi.materialpoints.common.api.ApiResponse;
import com.gongdi.materialpoints.modules.project.domain.Project;
import com.gongdi.materialpoints.modules.project.domain.Team;
import com.gongdi.materialpoints.modules.project.service.ProjectManageService;
import com.gongdi.materialpoints.modules.worker.domain.Worker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectController {

    private final ProjectManageService projectManageService;

    public ProjectController(ProjectManageService projectManageService) {
        this.projectManageService = projectManageService;
    }

    @GetMapping("/projects")
    public ApiResponse<List<Project>> projects() {
        return ApiResponse.success(projectManageService.listProjects());
    }

    @PostMapping("/projects")
    public ApiResponse<Project> createProject(@Valid @RequestBody SaveProjectRequest request) {
        return ApiResponse.success(projectManageService.createProject(
                new ProjectManageService.SaveProjectCommand(request.name(), request.location(), request.status())
        ));
    }

    @PutMapping("/projects/{id}")
    public ApiResponse<Project> updateProject(@PathVariable Long id, @Valid @RequestBody SaveProjectRequest request) {
        return ApiResponse.success(projectManageService.updateProject(
                id,
                new ProjectManageService.SaveProjectCommand(request.name(), request.location(), request.status())
        ));
    }

    @GetMapping("/teams")
    public ApiResponse<List<Team>> teams(@RequestParam(required = false) Long projectId) {
        return ApiResponse.success(projectManageService.listTeams(projectId));
    }

    @PostMapping("/teams")
    public ApiResponse<Team> createTeam(@Valid @RequestBody SaveTeamRequest request) {
        return ApiResponse.success(projectManageService.createTeam(
                new ProjectManageService.SaveTeamCommand(request.projectId(), request.name(), request.status())
        ));
    }

    @PutMapping("/teams/{id}")
    public ApiResponse<Team> updateTeam(@PathVariable Long id, @Valid @RequestBody SaveTeamRequest request) {
        return ApiResponse.success(projectManageService.updateTeam(
                id,
                new ProjectManageService.SaveTeamCommand(request.projectId(), request.name(), request.status())
        ));
    }

    @GetMapping("/workers")
    public ApiResponse<List<Worker>> workers() {
        return ApiResponse.success(projectManageService.listWorkers());
    }

    @PostMapping("/workers")
    public ApiResponse<Worker> createWorker(@Valid @RequestBody SaveWorkerRequest request) {
        return ApiResponse.success(projectManageService.createWorker(
                new ProjectManageService.SaveWorkerCommand(request.name(), request.phone(), request.status())
        ));
    }

    @PutMapping("/workers/{id}")
    public ApiResponse<Worker> updateWorker(@PathVariable Long id, @Valid @RequestBody SaveWorkerRequest request) {
        return ApiResponse.success(projectManageService.updateWorker(
                id,
                new ProjectManageService.SaveWorkerCommand(request.name(), request.phone(), request.status())
        ));
    }

    public record SaveProjectRequest(
            @NotBlank(message = "项目名称不能为空")
            String name,
            String location,
            @NotBlank(message = "状态不能为空")
            String status
    ) {
    }

    public record SaveTeamRequest(
            @NotNull(message = "项目不能为空")
            Long projectId,
            @NotBlank(message = "班组名称不能为空")
            String name,
            @NotBlank(message = "状态不能为空")
            String status
    ) {
    }

    public record SaveWorkerRequest(
            @NotBlank(message = "工人姓名不能为空")
            String name,
            String phone,
            @NotBlank(message = "状态不能为空")
            String status
    ) {
    }
}
