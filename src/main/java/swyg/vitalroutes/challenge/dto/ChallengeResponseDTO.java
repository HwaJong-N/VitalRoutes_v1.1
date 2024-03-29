package swyg.vitalroutes.challenge.dto;

import lombok.Data;
import swyg.vitalroutes.challenge.domain.Challenge;
import swyg.vitalroutes.challenge.domain.ChallengeTag;

import java.util.List;

@Data
public class ChallengeResponseDTO {
    // 상세 화면
    private Long challengeId;
    private Long memberId;
    private String profileImg;
    private String nickname;
    private String title;
    private String content;
    private String type;
    private String roadAddress;
    private String region;
    private long totalParticipation;
    private long viewCount;
    private long likeCount;
    private long bookmarkCount;
    private boolean likeFlag;
    private boolean bookmarkFlag;
    private List<String> tagList;
    private String titleImgURL;
    private List<LocationResponseDTO> imageList;

    public ChallengeResponseDTO(Challenge challenge, boolean like, boolean bookmark) {
        challengeId = challenge.getChallengeId();
        memberId = challenge.getMember().getMemberId();
        profileImg = challenge.getMember().getProfile();
        nickname = challenge.getMember().getNickname();
        title = challenge.getTitle();
        content = challenge.getContent();
        type = challenge.getType().getValue();
        roadAddress = challenge.getRoadAddress();
        region = challenge.getRegion();
        totalParticipation = challenge.getParticipationList().size();
        viewCount = challenge.getViewCount();
        likeCount = challenge.getLikeCount();
        bookmarkCount = challenge.getBookmarkCount();
        tagList = challenge.getTagList().stream().map(ChallengeTag::getTagKo).toList();
        titleImgURL = challenge.getTitleImg();
        imageList = challenge.getLocationList().stream().map(LocationResponseDTO::new).toList();
        likeFlag = like;
        bookmarkFlag = bookmark;
    }
}
