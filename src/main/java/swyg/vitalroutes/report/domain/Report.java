package swyg.vitalroutes.report.domain;

import jakarta.persistence.*;
import lombok.*;
import swyg.vitalroutes.challenge.domain.Challenge;
import swyg.vitalroutes.comments.domain.Comment;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.participation.domain.Participation;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_member_id")
    private Member reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_member_id")
    private Member reported;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chalenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id")
    private Participation participation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    private String reason;
}
