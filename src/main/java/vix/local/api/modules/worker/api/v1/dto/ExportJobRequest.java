package vix.local.api.modules.worker.api.v1.dto;

import lombok.Data;

@Data
public class ExportJobRequest {
    private String jobType;
    private String payload;
    private String fileName;
}
