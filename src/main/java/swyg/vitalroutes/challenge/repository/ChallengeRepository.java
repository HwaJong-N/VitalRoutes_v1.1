package swyg.vitalroutes.challenge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyg.vitalroutes.challenge.domain.Challenge;
import swyg.vitalroutes.challenge.domain.ReactionType;
import swyg.vitalroutes.challenge.dto.ChallengeConditionDTO;
import swyg.vitalroutes.challenge.dto.ChallengeListDTO;
import swyg.vitalroutes.member.domain.Member;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @EntityGraph(attributePaths = "tagList")
    @Query("select c from Challenge c join fetch c.member where c.challengeId = :challengeId")
    Optional<Challenge> findByChallengeId(@Param("challengeId") Long challengeId);

    @Query("select new swyg.vitalroutes.challenge.dto.ChallengeListDTO(c.challengeId, c.title, c.titleImg, count(cp)) from Challenge c left join c.participationList cp group by c.challengeId order by c.challengeId desc")
    Page<ChallengeListDTO> findAllChallenge(Pageable pageable);

    // 내가 등록한 챌린지
    @Query("select new swyg.vitalroutes.challenge.dto.ChallengeConditionDTO(c.challengeId, c.title) from Challenge c where c.member = :member")
    Page<ChallengeConditionDTO> findMyChallenges(@Param("member") Member member, Pageable pageable);
    
    // 내가 참여한 챌린지
    @Query("select new swyg.vitalroutes.challenge.dto.ChallengeConditionDTO(c.challengeId, c.title) from Challenge c join c.participationList cp where cp.member = :member group by c.challengeId")
    Page<ChallengeConditionDTO> findParticipateChallenge(@Param("member") Member member, Pageable pageable);

    // 내가 좋아요 or 북마크한 챌린지
    @Query("select new swyg.vitalroutes.challenge.dto.ChallengeConditionDTO(c.challengeId, c.title) from Challenge c join ChallengeLikeAndBookmark  clb on c.challengeId = clb.challenge.challengeId where clb.member = :member and clb.reactionType = :type")
    Page<ChallengeConditionDTO> findReactionChallenges(@Param("member") Member member, @Param("type") ReactionType type, Pageable pageable);
}
