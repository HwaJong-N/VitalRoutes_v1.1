package swyg.vitalroutes.challenge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import swyg.vitalroutes.challenge.dto.ChallengeListDTO;

public interface ChallengeSearchRepository {
    Page<ChallengeListDTO> findAllChallenge(Pageable pageable, String searchType);
}
