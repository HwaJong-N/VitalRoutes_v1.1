package swyg.vitalroutes.challenge.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Tags {
    ACTIVITY("활동적인"), PEACEFUL("평화로운"), UNIQUE("독특한"),
    NATURAL("자연 친화적인"), BEACH("해변가"), THEME("테마적인"),
    PICTURE("사진 찍기 좋은"), FAMILY("가족 친화적");

    String value;

    public static Tags findByKorean(String korean) {
        Tags[] values = Tags.values();
        for (Tags tags : values) {
            if (tags.getValue().equals(korean)) {
                return tags;
            }
        }
        return null;
    }
}
