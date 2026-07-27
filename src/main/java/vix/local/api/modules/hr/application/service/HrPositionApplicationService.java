package vix.local.api.modules.hr.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.hr.domain.exception.HrException;
import vix.local.api.modules.hr.domain.model.HrPosition;
import vix.local.api.modules.hr.domain.repository.HrPositionRepository;
import vix.local.api.modules.hr.api.v1.dto.request.CreatePositionRequest;
import vix.local.api.modules.hr.api.v1.dto.request.UpdatePositionRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HrPositionApplicationService {

    private final HrPositionRepository hrPositionRepository;

    @Transactional(readOnly = true)
    public List<HrPosition> getAllPositions() {
        return hrPositionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public HrPosition getPositionById(UUID id) {
        return hrPositionRepository.findById(id)
                .orElseThrow(() -> HrException.notFound("Không tìm thấy chức danh"));
    }

    @Transactional
    public HrPosition createPosition(CreatePositionRequest request) {
        if (hrPositionRepository.findByCode(request.getCode()).isPresent()) {
            throw HrException.badRequest("Mã chức danh đã tồn tại");
        }

        HrPosition position = HrPosition.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .build();

        return hrPositionRepository.save(position);
    }

    @Transactional
    public HrPosition updatePosition(UUID id, UpdatePositionRequest request) {
        HrPosition position = getPositionById(id);

        if (!position.getCode().equals(request.getCode()) && hrPositionRepository.findByCode(request.getCode()).isPresent()) {
            throw HrException.badRequest("Mã chức danh đã tồn tại");
        }

        position.setName(request.getName());
        position.setCode(request.getCode());
        position.setDescription(request.getDescription());

        return hrPositionRepository.save(position);
    }
}
