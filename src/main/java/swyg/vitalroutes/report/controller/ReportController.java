package swyg.vitalroutes.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swyg.vitalroutes.common.response.ApiResponseDTO;
import swyg.vitalroutes.common.response.ResponseType;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.report.dto.ReportDTO;
import swyg.vitalroutes.report.service.ReportService;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/challenge/{challengeId}")
    public ApiResponseDTO<?> reportChallenge(@PathVariable Long challengeId, @RequestBody ReportDTO reportDTO) {
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        reportService.saveChallengeReport(member, challengeId, reportDTO);
        return new ApiResponseDTO<>(HttpStatus.OK, ResponseType.SUCCESS, "신고가 접수되었습니다", null);
    }

    @PostMapping("/participation/{participationId}")
    public ApiResponseDTO<?> reportParticipation(@PathVariable Long participationId, @RequestBody ReportDTO reportDTO) {
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        reportService.saveParticipationReport(member, participationId, reportDTO);
        return new ApiResponseDTO<>(HttpStatus.OK, ResponseType.SUCCESS, "신고가 접수되었습니다", null);
    }

    @PostMapping("/comment/{commentId}")
    public ApiResponseDTO<?> reportComment(@PathVariable Long commentId, @RequestBody ReportDTO reportDTO) {
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        reportService.saveCommentReport(member, commentId, reportDTO);
        return new ApiResponseDTO<>(HttpStatus.OK, ResponseType.SUCCESS, "신고가 접수되었습니다", null);
    }
}
