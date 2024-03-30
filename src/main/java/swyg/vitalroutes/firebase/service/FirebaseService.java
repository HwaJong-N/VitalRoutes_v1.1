package swyg.vitalroutes.firebase.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.common.exception.FileProcessException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static swyg.vitalroutes.common.response.ResponseType.FAIL;

@Slf4j
@Service
public class FirebaseService {
    @Value("${firebase.bucket}")
    private String firebaseBucket;

    @Value("${READ_URL}")
    private String readUrl;

    public String saveFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();

        StringBuffer sb = new StringBuffer();
        sb.append(UUID.randomUUID());
        sb.append("-");
        sb.append(originalFilename);
        String filename = sb.toString();

        if (!multipartFile.getContentType().startsWith("image")) {
            throw new FileProcessException(BAD_REQUEST, FAIL, "이미지 파일만 업로드 가능합니다");
        }

        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        InputStream content = new ByteArrayInputStream(multipartFile.getBytes());
        Blob blob = bucket.create(filename, content, multipartFile.getContentType());

        // 조회 링크 생성
        return readUrl + filename + "?alt=media";
    }

    public void deleteFile(String viewUrl) {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        String filename = viewUrl.replace(readUrl, "").replace("?alt=media", "");
        Blob blob = bucket.get(filename);
        if (blob != null) {
            blob.delete();
        }
    }
}
