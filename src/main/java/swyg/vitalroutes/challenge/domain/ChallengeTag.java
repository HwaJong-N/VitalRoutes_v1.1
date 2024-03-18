package swyg.vitalroutes.challenge.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeTag {
    private String tagEn;   // 영문명
    private String tagKo;   // 한글명

    public static ChallengeTag createChallengeTag(String name, String value) {
        ChallengeTag challengeTag = new ChallengeTag();
        challengeTag.setTagEn(name);
        challengeTag.setTagKo(value);
        return challengeTag;
    }
}
