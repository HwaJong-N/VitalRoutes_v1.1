package swyg.vitalroutes.report.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportDTO {
    @Size(min = 1, max = 2000, message = "신고 사유는 2000자 이내로 작성해주세요")
    private String reason;
}
