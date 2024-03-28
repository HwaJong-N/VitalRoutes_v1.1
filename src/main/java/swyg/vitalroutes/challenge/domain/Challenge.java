package swyg.vitalroutes.challenge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.participation.domain.Participation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeId;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String titleImg;    // 대표이미지
    private LocalDateTime localDateTime;
    private long viewCount; // 조회 수
    private long likeCount; // 좋아요 수
    private long bookmarkCount; // 북마크 수

    private String roadAddress;
    private String region;


    @Enumerated(value = EnumType.STRING)
    private ChallengeType type;

    @ElementCollection
    private List<ChallengeTag> tagList = new ArrayList<>();


    @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChallengeLocation> locationList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Participation> participationList = new ArrayList<>();


    // 조회 수 증가
    public void increaseViewCount() {
        this.viewCount += 1;
    }

    public void increaseLikeCount() {
        this.likeCount += 1;
    }

    public void increaseBookmarkCount() {
        this.bookmarkCount += 1;
    }

    public void decreaseLikeCount() {
        this.likeCount -= 1;
    }

    public void decreaseBookmarkCount() {
        this.bookmarkCount -= 1;
    }


    // 챌린지와 위치정보 연관관계 매핑
    public void setChallengeInLocation(ChallengeLocation location) {
        this.locationList.add(location);
        location.setChallenge(this);
    }

    public static Challenge createChallenge(Member member, List<ChallengeLocation> locationList, List<ChallengeTag> tagList,
                                            String title, String content, String titleImg, ChallengeType type, String roadAddress, String region) {
        Challenge challenge = new Challenge();
        // 기본 세팅
        challenge.setViewCount(0L);
        challenge.setLikeCount(0L);
        challenge.setBookmarkCount(0L);
        challenge.setLocalDateTime(LocalDateTime.now());
        
        // 내용 세팅
        challenge.setTitle(title);
        challenge.setContent(content);
        challenge.setTitleImg(titleImg);
        challenge.setType(type);
        challenge.setRoadAddress(roadAddress);
        challenge.setRegion(region);

        // 회원 세팅, 단방향
        challenge.setMember(member);
        
        // 태그 연관관계
        for (ChallengeTag challengeTag : tagList) {
            challenge.getTagList().add(challengeTag);
        }

        // 위치정보 연관관계
        for (ChallengeLocation challengeLocation : locationList) {
            challenge.setChallengeInLocation(challengeLocation);
        }
        
        return challenge;
    }
}
