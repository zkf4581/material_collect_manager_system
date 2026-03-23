package com.gongdi.materialpoints.modules.project.service;

import com.gongdi.materialpoints.common.exception.BusinessException;
import com.gongdi.materialpoints.modules.project.domain.Project;
import com.gongdi.materialpoints.modules.project.domain.Team;
import com.gongdi.materialpoints.modules.project.repository.ProjectRepository;
import com.gongdi.materialpoints.modules.project.repository.TeamRepository;
import com.gongdi.materialpoints.modules.worker.domain.Worker;
import com.gongdi.materialpoints.modules.worker.repository.WorkerRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectManageService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final WorkerRepository workerRepository;

    public ProjectManageService(
            ProjectRepository projectRepository,
            TeamRepository teamRepository,
            WorkerRepository workerRepository
    ) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.workerRepository = workerRepository;
    }

    public List<Project> listProjects() {
        return projectRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<Team> listTeams(Long projectId) {
        return projectId == null
                ? teamRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                : teamRepository.findAllByProjectIdAndStatusOrderByIdAsc(projectId, "ENABLED");
    }

    public List<Worker> listWorkers() {
        return workerRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Transactional
    public Project createProject(SaveProjectCommand command) {
        Project project = new Project();
        project.setName(command.name());
        project.setLocation(command.location());
        project.setStatus(command.status());
        return projectRepository.save(project);
    }

    @Transactional
    public Project updateProject(Long id, SaveProjectCommand command) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "项目不存在"));
        project.setName(command.name());
        project.setLocation(command.location());
        project.setStatus(command.status());
        return projectRepository.save(project);
    }

    @Transactional
    public Team createTeam(SaveTeamCommand command) {
        ensureProjectExists(command.projectId());
        Team team = new Team();
        team.setProjectId(command.projectId());
        team.setName(command.name());
        team.setStatus(command.status());
        return teamRepository.save(team);
    }

    @Transactional
    public Team updateTeam(Long id, SaveTeamCommand command) {
        ensureProjectExists(command.projectId());
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "班组不存在"));
        team.setProjectId(command.projectId());
        team.setName(command.name());
        team.setStatus(command.status());
        return teamRepository.save(team);
    }

    @Transactional
    public Worker createWorker(SaveWorkerCommand command) {
        Worker worker = new Worker();
        worker.setName(command.name());
        worker.setPhone(command.phone());
        worker.setStatus(command.status());
        return workerRepository.save(worker);
    }

    @Transactional
    public Worker updateWorker(Long id, SaveWorkerCommand command) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "工人不存在"));
        worker.setName(command.name());
        worker.setPhone(command.phone());
        worker.setStatus(command.status());
        return workerRepository.save(worker);
    }

    private void ensureProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new BusinessException(40401, "项目不存在");
        }
    }

    public record SaveProjectCommand(String name, String location, String status) {
    }

    public record SaveTeamCommand(Long projectId, String name, String status) {
    }

    public record SaveWorkerCommand(String name, String phone, String status) {
    }
}
