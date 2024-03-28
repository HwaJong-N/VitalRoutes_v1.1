package swyg.vitalroutes.kakao.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import swyg.vitalroutes.challenge.domain.ChallengeLocation;
import swyg.vitalroutes.kakao.dto.KakaoMapResponse;

import java.util.List;

@Service
public class KakaoMapService {
    @Value("${kakao.login.client-id}")
    private String restAPiKey;

    public String[] getRegion(ChallengeLocation challengeLocation) {
        double x = challengeLocation.getLongitude();
        double y = challengeLocation.getLatitude();

        RestTemplate restTemplate = new RestTemplate();

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + restAPiKey);

        HttpEntity<String> request = new HttpEntity<>(headers);

        StringBuffer sb = new StringBuffer();
        sb.append("https://dapi.kakao.com/v2/local/geo/coord2address.json?");
        sb.append("x=");
        sb.append(x);
        sb.append("&y=");
        sb.append(y);

        UriComponents uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(sb.toString()).build();

        ResponseEntity<KakaoMapResponse> response = restTemplate.exchange(uriComponentsBuilder.toUri(), HttpMethod.GET, request, KakaoMapResponse.class);
        KakaoMapResponse mapResponse = response.getBody();
        List<KakaoMapResponse.Document> documents = mapResponse.getDocuments();
        KakaoMapResponse.Document document = documents.get(0);
        KakaoMapResponse.RoadAddress roadAddress = document.getRoadAddress();
        // 도로명 주소, 지역명
        return new String[]{roadAddress.getAddressName(), roadAddress.getRegion1depthName()};
    }
}
