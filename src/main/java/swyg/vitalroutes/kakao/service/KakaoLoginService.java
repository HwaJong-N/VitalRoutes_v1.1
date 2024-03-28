package swyg.vitalroutes.kakao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import swyg.vitalroutes.common.exception.KakaoLoginException;
import swyg.vitalroutes.common.response.ResponseType;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.domain.SocialType;
import swyg.vitalroutes.member.repository.MemberRepository;
import swyg.vitalroutes.kakao.dto.KakaoTokenResponse;
import swyg.vitalroutes.kakao.dto.KakaoUserInfoResponse;

import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    @Value("${kakao.login.client-id}")
    private String restAPiKey;

    @Value("${kakao.login.redirect-uri}")
    private String redirectURI;

    private final MemberRepository memberRepository;

    // 회원가입 여부 판단
    public Optional<Member> findMember(String socialId, SocialType socialType) {
        return memberRepository.findBySocialIdAndSocialType(socialId, socialType);
    }

    // 회원가입이 되어 있지 않은 경우 랜덤으로 닉네임을 생성하고 저장
    public Member saveSocialMember(String socialId, String name, SocialType socialType) {
        Member member = Member.builder()
                .name(name)
                .nickname(createRandomNickname(name))
                .profile("https://firebasestorage.googleapis.com/v0/b/vitalroutes-467cb.appspot.com/o/profile.png?alt=media")
                .socialId(socialId)
                .socialType(socialType)
                .build();
        return memberRepository.save(member);
    }


    // 닉네임 랜덤 생성
    public String createRandomNickname(String name) {
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000);
        return name + randomNumber;
    }

    public String[] kakaoLogin(String code) {
        String accessToken = getAccessToken(code);
        return getUserInfo(accessToken);
    }

    public String getAccessToken(String code) {

        RestTemplate restTemplate = new RestTemplate();

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // Body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", restAPiKey);
        body.add("redirect_uri", redirectURI);
        body.add("code", code);

        // Header 와 Body 를 가진 Request 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        String accessTokenUri = "https://kauth.kakao.com/oauth/token";
        UriComponents uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(accessTokenUri).build();

        // HTTP POST 요청
        ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(uriComponentsBuilder.toUri(), HttpMethod.POST, request, KakaoTokenResponse.class);

        if (response.getStatusCode().value() != 200) {
            throw new KakaoLoginException(HttpStatus.INTERNAL_SERVER_ERROR, ResponseType.ERROR, "Access Token 을 받는 중 문제가 발생하였습니다");
        }
        
        KakaoTokenResponse responseBody = response.getBody();
        String accessToken = responseBody.getAccessToken();
        return accessToken;
    }

    public String[] getUserInfo(String accessToken) {

        RestTemplate restTemplate = new RestTemplate();

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> request = new HttpEntity<>(headers);

        String userInfoUri = "https://kapi.kakao.com/v2/user/me";
        UriComponents uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(userInfoUri).build();

        ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(uriComponentsBuilder.toUri(), HttpMethod.GET, request, KakaoUserInfoResponse.class);

        if (response.getStatusCode().value() != 200) {
            throw new KakaoLoginException(HttpStatus.INTERNAL_SERVER_ERROR, ResponseType.ERROR, "사용자 정보를 받는 중 문제가 발생하였습니다");
        }

        KakaoUserInfoResponse responseBody = response.getBody();
        String socialId = String.valueOf(responseBody.getId());
        String nickname = responseBody.getProperties().getNickname();

        log.info("socialId = {}", socialId);
        log.info("nickname = {}", nickname);
        return new String[]{socialId, nickname};
    }



}
