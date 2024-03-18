package swyg.vitalroutes.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyg.vitalroutes.challenge.domain.Challenge;
import swyg.vitalroutes.challenge.domain.ChallengeLikeAndBookmark;
import swyg.vitalroutes.challenge.domain.ReactionType;
import swyg.vitalroutes.member.domain.Member;

import java.util.List;
import java.util.Optional;

public interface LikeAndBookmarkRepository extends JpaRepository<ChallengeLikeAndBookmark, Long> {

    // 내가 좋아요 하거나 북마크한 챌린지 아이디 조회
    @Query("select clb.challenge.challengeId from ChallengeLikeAndBookmark clb where clb.member = :member and clb.reactionType = :reactionType")
    List<Long> findMyReactionChallengeId(@Param("member") Member member,
                                         @Param("reactionType") ReactionType reactionType);


    // 내가 특정 챌린지를 좋아요 학나 북마크 했는지
    @Query("select clb from ChallengeLikeAndBookmark clb where clb.member = :member and clb.challenge = :challenge and clb.reactionType = :reactionType")
    Optional<ChallengeLikeAndBookmark> judgeMyReactionChallengeId(@Param("member") Member member,
                                                                  @Param("challenge") Challenge challenge,
                                                                  @Param("reactionType") ReactionType reactionType);
}

