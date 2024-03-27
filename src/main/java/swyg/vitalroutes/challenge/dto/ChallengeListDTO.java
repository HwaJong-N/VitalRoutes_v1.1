package swyg.vitalroutes.challenge.dto;

import lombok.Data;

@Data
public class ChallengeListDTO {
    // 목록 화면
    private Long challengeId;
    private String title;
    private String titleImg;
    private long participationCount;
    private boolean likeFlag = false;
    private boolean bookmarkFlag = false;

    public ChallengeListDTO(Long id, String cTitle, String cTitleImg, long cpCount) {
        challengeId = id;
        title = cTitle;
        titleImg = cTitleImg;
        participationCount = cpCount;
    }

    public ChallengeListDTO(Long id, String cTitle, String cTitleImg, int cpCount) {
        challengeId = id;
        title = cTitle;
        titleImg = cTitleImg;
        participationCount = (long) cpCount;
    }
}
