package swyg.vitalroutes.participation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import swyg.vitalroutes.participation.domain.Participation;

import java.util.List;

public interface ParticipationSearchRepository {
    Page<Participation> findAllByChallengeId(Long challengeId, List<Long> hidedIds, Pageable pageable);
}
