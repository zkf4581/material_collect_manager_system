package com.gongdi.materialpoints.modules.recycle.service;

import com.gongdi.materialpoints.common.exception.BusinessException;
import com.gongdi.materialpoints.modules.file.domain.FileRecord;
import com.gongdi.materialpoints.modules.file.repository.FileRecordRepository;
import com.gongdi.materialpoints.modules.material.domain.PointRule;
import com.gongdi.materialpoints.modules.material.repository.PointRuleRepository;
import com.gongdi.materialpoints.modules.point.domain.PointAccount;
import com.gongdi.materialpoints.modules.point.domain.PointLedger;
import com.gongdi.materialpoints.modules.point.repository.PointAccountRepository;
import com.gongdi.materialpoints.modules.point.repository.PointLedgerRepository;
import com.gongdi.materialpoints.modules.recycle.domain.RecyclePhoto;
import com.gongdi.materialpoints.modules.recycle.domain.RecycleRecord;
import com.gongdi.materialpoints.modules.recycle.repository.RecyclePhotoRepository;
import com.gongdi.materialpoints.modules.recycle.repository.RecycleRecordRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecycleRecordService {

    private final RecycleRecordRepository recycleRecordRepository;
    private final RecyclePhotoRepository recyclePhotoRepository;
    private final PointRuleRepository pointRuleRepository;
    private final PointAccountRepository pointAccountRepository;
    private final PointLedgerRepository pointLedgerRepository;
    private final FileRecordRepository fileRecordRepository;

    public RecycleRecordService(
            RecycleRecordRepository recycleRecordRepository,
            RecyclePhotoRepository recyclePhotoRepository,
            PointRuleRepository pointRuleRepository,
            PointAccountRepository pointAccountRepository,
            PointLedgerRepository pointLedgerRepository,
            FileRecordRepository fileRecordRepository
    ) {
        this.recycleRecordRepository = recycleRecordRepository;
        this.recyclePhotoRepository = recyclePhotoRepository;
        this.pointRuleRepository = pointRuleRepository;
        this.pointAccountRepository = pointAccountRepository;
        this.pointLedgerRepository = pointLedgerRepository;
        this.fileRecordRepository = fileRecordRepository;
    }

    @Transactional
    public RecycleRecord create(CreateRecycleRecordCommand command) {
        PointRule pointRule = pointRuleRepository
                .findFirstByMaterialItemIdAndUnitCodeAndConditionCodeAndStatus(
                        command.materialItemId(),
                        command.unitCode(),
                        command.conditionCode(),
                        "ENABLED"
                )
                .orElseThrow(() -> new BusinessException(40401, "未找到匹配的积分规则"));

        List<FileRecord> fileRecords = fileRecordRepository.findAllById(command.photoIds());
        if (fileRecords.size() != command.photoIds().size()) {
            throw new BusinessException(40401, "存在未找到的图片文件");
        }

        BigDecimal points = pointRule.getBasePoint()
                .multiply(pointRule.getConditionFactor())
                .multiply(command.quantity());

        RecycleRecord recycleRecord = new RecycleRecord();
        recycleRecord.setProjectId(command.projectId());
        recycleRecord.setTeamId(command.teamId());
        recycleRecord.setWorkerId(command.workerId());
        recycleRecord.setMaterialItemId(command.materialItemId());
        recycleRecord.setQuantity(command.quantity());
        recycleRecord.setUnitCode(command.unitCode());
        recycleRecord.setConditionCode(command.conditionCode());
        recycleRecord.setCalculatedPoints(points.setScale(0, RoundingMode.HALF_UP).intValue());
        recycleRecord.setStatus("SUBMITTED");
        recycleRecord.setRemark(command.remark());
        recycleRecord.setSubmittedAt(LocalDateTime.now());
        RecycleRecord saved = recycleRecordRepository.save(recycleRecord);

        for (FileRecord fileRecord : fileRecords) {
            RecyclePhoto recyclePhoto = new RecyclePhoto();
            recyclePhoto.setRecycleRecordId(saved.getId());
            recyclePhoto.setFileUrl(fileRecord.getFileUrl());
            recyclePhoto.setStorageType(fileRecord.getStorageType());
            recyclePhoto.setFileSize(fileRecord.getFileSize());
            recyclePhotoRepository.save(recyclePhoto);
        }

        return saved;
    }

    @Transactional
    public RecycleRecord approve(Long recycleRecordId) {
        RecycleRecord recycleRecord = recycleRecordRepository.findById(recycleRecordId)
                .orElseThrow(() -> new BusinessException(40401, "回收记录不存在"));
        if (!"SUBMITTED".equals(recycleRecord.getStatus())) {
            throw new BusinessException(40901, "当前状态不允许审核通过");
        }

        PointAccount pointAccount = pointAccountRepository
                .findByProjectIdAndWorkerId(recycleRecord.getProjectId(), recycleRecord.getWorkerId())
                .orElseGet(() -> {
                    PointAccount account = new PointAccount();
                    account.setProjectId(recycleRecord.getProjectId());
                    account.setWorkerId(recycleRecord.getWorkerId());
                    account.setBalance(0);
                    return pointAccountRepository.save(account);
                });

        int nextBalance = pointAccount.getBalance() + recycleRecord.getCalculatedPoints();
        pointAccount.setBalance(nextBalance);
        pointAccountRepository.save(pointAccount);

        PointLedger pointLedger = new PointLedger();
        pointLedger.setPointAccountId(pointAccount.getId());
        pointLedger.setWorkerId(recycleRecord.getWorkerId());
        pointLedger.setProjectId(recycleRecord.getProjectId());
        pointLedger.setBizType("RECYCLE_AWARD");
        pointLedger.setBizId(recycleRecord.getId());
        pointLedger.setChangeAmount(recycleRecord.getCalculatedPoints());
        pointLedger.setBalanceAfter(nextBalance);
        pointLedger.setRemark("回收记录审核通过");
        pointLedgerRepository.save(pointLedger);

        recycleRecord.setStatus("APPROVED");
        recycleRecord.setApprovedAt(LocalDateTime.now());
        return recycleRecordRepository.save(recycleRecord);
    }

    public List<RecycleRecord> listAll() {
        return recycleRecordRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<RecycleRecord> listByWorker(Long workerId) {
        return recycleRecordRepository.findAllByWorkerIdOrderByIdDesc(workerId);
    }

    public record CreateRecycleRecordCommand(
            Long projectId,
            Long teamId,
            Long workerId,
            Long materialItemId,
            BigDecimal quantity,
            String unitCode,
            String conditionCode,
            String remark,
            List<Long> photoIds
    ) {
    }
}
