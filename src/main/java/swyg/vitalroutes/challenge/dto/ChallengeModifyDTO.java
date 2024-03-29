package swyg.vitalroutes.challenge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ChallengeModifyDTO {
    @NotBlank(message = "제목은 24자 이내로 작성해주세요")
    @Size(min = 1, max = 24, message = "제목은 1자 이상 24자 이내만 입력 가능합니다")
    private String title;

    @NotBlank(message = "게시글 내용은 2000자 이내로 작성해주세요")
    @Size(min = 1, max = 2000, message = "게시글 내용은 2000자 이내로 작성해주세요")
    private String content;

    @NotBlank(message = "이동 방법을 선택해주세요")
    private String type;
    private List<String> tags;
}
