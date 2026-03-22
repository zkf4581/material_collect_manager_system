package com.gongdi.materialpoints.modules.project.web;

import com.gongdi.materialpoints.common.api.ApiResponse;
import com.gongdi.materialpoints.modules.project.domain.Project;
import com.gongdi.materialpoints.modules.project.domain.Team;
import com.gongdi.materialpoints.modules.project.repository.ProjectRepository;
import com.gongdi.materialpoints.modules.project.repository.TeamRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;

    public ProjectController(ProjectRepository projectRepository, TeamRepository teamRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
    }

    @GetMapping("/projects")
    public ApiResponse<List<Project>> projects() {
        return ApiResponse.success(projectRepository.findAllByStatusOrderByIdAsc("ENABLED"));
    }

    @GetMapping("/teams")
    public ApiResponse<List<Team>> teams(@RequestParam(required = false) Long projectId) {
        List<Team> teams = projectId == null
                ? teamRepository.findAllByStatusOrderByIdAsc("ENABLED")
                : teamRepository.findAllByProjectIdAndStatusOrderByIdAsc(projectId, "ENABLED");
        return ApiResponse.success(teams);
    }
}
