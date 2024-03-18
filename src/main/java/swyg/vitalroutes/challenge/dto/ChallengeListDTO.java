package swyg.vitalroutes.challenge.dto;

import lombok.Data;

@Data
public class ChallengeListDTO {
    // 목록 화면
    private Long challengeId;
    private String title;
    private long participationCount;
    private boolean isLike = false;
    private boolean isBookmark = false;

    public ChallengeListDTO(Long id, String cTitle, long cpCount) {
        challengeId = id;
        title = cTitle;
        participationCount = cpCount;
    }
}
