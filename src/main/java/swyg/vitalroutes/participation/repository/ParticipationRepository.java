package swyg.vitalroutes.participation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyg.vitalroutes.participation.domain.Participation;

import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long>, ParticipationSearchRepository {

    @Query("select p from Participation p where p.member.memberId = :memberId and p.challenge.challengeId = :challengeId")
    Optional<Participation> findByMemberIdAndChallengeId(@Param("memberId") Long memberId, @Param("challengeId") Long challengeId);
}
