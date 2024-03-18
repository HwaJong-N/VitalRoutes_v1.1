package swyg.vitalroutes.challenge.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChallengeType {
    WALK("도보"), BIKE("자전거");

    String value;

    public static ChallengeType findByKorean(String korean) {
        ChallengeType[] values = ChallengeType.values();
        for (ChallengeType challengeType : values) {
            if (challengeType.getValue().equals(korean)) {
                return challengeType;
            }
        }
        return null;
    }
}
