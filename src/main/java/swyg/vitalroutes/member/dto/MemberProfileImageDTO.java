package swyg.vitalroutes.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "프로필 이미지를 수정할 때 사용, form-data 형식으로 전송 필요")
@Data
public class MemberProfileImageDTO {
    private MultipartFile profileImage;

}
