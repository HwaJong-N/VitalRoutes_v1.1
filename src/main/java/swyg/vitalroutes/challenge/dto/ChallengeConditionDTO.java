package swyg.vitalroutes.challenge.dto;

import lombok.Data;

@Data
public class ChallengeConditionDTO {
    private Long challengeId;
    private String title;

    public ChallengeConditionDTO(Long id, String ctitle) {
        challengeId = id;
        title = ctitle;
    }
}
