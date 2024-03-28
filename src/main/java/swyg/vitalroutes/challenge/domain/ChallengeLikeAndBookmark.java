package swyg.vitalroutes.challenge.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import swyg.vitalroutes.member.domain.Member;

@Entity
@Getter
@Setter
public class ChallengeLikeAndBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(value = EnumType.STRING)
    private ReactionType reactionType;

    public static ChallengeLikeAndBookmark createChallengeLikeAndBookmark(Member member, Challenge challenge, ReactionType reactionType) {
        ChallengeLikeAndBookmark challengeLikeAndBookmark = new ChallengeLikeAndBookmark();
        challengeLikeAndBookmark.setMember(member);
        challengeLikeAndBookmark.setChallenge(challenge);
        challengeLikeAndBookmark.setReactionType(reactionType);
        return challengeLikeAndBookmark;
    }
}
