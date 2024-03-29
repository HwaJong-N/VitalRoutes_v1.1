package swyg.vitalroutes.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swyg.vitalroutes.challenge.domain.Challenge;
import swyg.vitalroutes.challenge.repository.ChallengeRepository;
import swyg.vitalroutes.comments.domain.Comment;
import swyg.vitalroutes.comments.repository.CommentRepository;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.participation.domain.Participation;
import swyg.vitalroutes.participation.repository.ParticipationRepository;
import swyg.vitalroutes.report.domain.Report;
import swyg.vitalroutes.report.dto.ReportDTO;
import swyg.vitalroutes.report.repository.ReportRepository;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ChallengeRepository challengeRepository;
    private final ParticipationRepository participationRepository;
    private final CommentRepository commentRepository;

    public void saveChallengeReport(Member member, Long challengeId, ReportDTO reportDTO) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow();
        Member reportedMember = challenge.getMember();
        Report report = Report.builder().reporter(member).reported(reportedMember)
                .challenge(challenge).reason(reportDTO.getReason()).build();
        reportRepository.save(report);
    }

    public void saveParticipationReport(Member member, Long participationId, ReportDTO reportDTO) {
        Participation participation = participationRepository.findById(participationId).orElseThrow();
        Member reportedMember = participation.getMember();
        Report report = Report.builder().reporter(member).reported(reportedMember)
                .participation(participation).reason(reportDTO.getReason()).build();
        reportRepository.save(report);
    }

    public void saveCommentReport(Member member, Long commentId, ReportDTO reportDTO) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        Member reportedMember = comment.getMember();
        Report report = Report.builder().reporter(member).reported(reportedMember)
                .comment(comment).reason(reportDTO.getReason()).build();
        reportRepository.save(report);
    }
}
