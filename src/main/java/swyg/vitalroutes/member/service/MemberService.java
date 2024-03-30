package swyg.vitalroutes.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.common.exception.MemberModifyException;
import swyg.vitalroutes.common.exception.MemberSignUpException;
import swyg.vitalroutes.common.utils.FileUtils;
import swyg.vitalroutes.firebase.service.FirebaseService;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.domain.SocialType;
import swyg.vitalroutes.member.dto.*;
import swyg.vitalroutes.member.repository.MemberRepository;
import swyg.vitalroutes.security.dto.SocialMemberDTO;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static swyg.vitalroutes.common.response.ResponseType.ERROR;
import static swyg.vitalroutes.common.response.ResponseType.FAIL;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FirebaseService firebaseService;

    public void duplicateNicknameCheck(MemberNicknameDTO dto) {
        String nickname = dto.getNickname();
        if (nickname == null) {
            throw new MemberSignUpException(BAD_REQUEST, FAIL, "닉네임이 전달되지 않았습니다");
        }

        Optional<Member> byNickname = memberRepository.findByNickname(nickname);
        if (byNickname.isPresent()) {
            throw new MemberSignUpException(BAD_REQUEST, FAIL, "이미 존재하는 닉네임입니다");
        }
    }

    public Member saveMember(MemberSaveDTO memberDTO) {
        if (!memberDTO.getIsChecked()) {
            throw new MemberSignUpException(BAD_REQUEST, FAIL, "닉네임 중복확인이 필요합니다");
        }
        
        Member member = Member.builder()
                .name(memberDTO.getName())
                .nickname(memberDTO.getNickname())
                .profile("https://firebasestorage.googleapis.com/v0/b/vitalroutes-467cb.appspot.com/o/profile.png?alt=media")
                .email(memberDTO.getEmail())
                .password(passwordEncoder.encode(memberDTO.getPassword()))
                .build();
        return memberRepository.save(member);
    }
    
    /*
    소셜 로그인 회원가입 변경
    public Member saveSocialMember(SocialMemberDTO memberDTO) {
        if (!memberDTO.getIsChecked()) {
            throw new MemberSignUpException(BAD_REQUEST, FAIL, "닉네임 중복확인이 필요합니다");
        }

        Member member = Member.builder()
                .name(memberDTO.getName())
                .nickname(memberDTO.getNickname())
                .profile("https://firebasestorage.googleapis.com/v0/b/vitalroutes-467cb.appspot.com/o/profile.png?alt=media")
                .socialId(memberDTO.getSocialId())
                .socialType(SocialType.valueOf(memberDTO.getSocialType()))
                .build();
        return memberRepository.save(member);
    }
    */

    public MemberModifyDTO getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다"));
        return MemberModifyDTO.entityToDto(member);
    }

    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다"));
        firebaseService.deleteFile(member.getProfile());
        memberRepository.deleteById(memberId);
    }


    public Member modifyMemberInfo(Long memberId, MemberModifyDTO memberModifyDTO) {
        Member member = memberRepository.findById(memberId).get();  // controller 에서 예외처리

        boolean socialFlag = false;
        if (member.getSocialId() != null) {
            socialFlag = true;
        }

        /**
         * 1. 변경할 데이터만 전달 받는다
         * 2. 닉네임이 전달되면 DB 중복 확인이 필요
         * 3. 이메일 변경된 경우 -> 소셜 회원이라면 ok, 일반 회원이라면 이메일은 삭제 불가
         * 4. 이전 비밀번호는 일치하는지 확인, 새 비밀번호는 규칙에 맞는지 확인
         */

        if (StringUtils.hasText(memberModifyDTO.getName())) {
            // 일반 JPA 와 동일하게 엔티티 변경하면 하면 커밋 시점에 자동으로 update
            member.setName(memberModifyDTO.getName());
        }

        if (StringUtils.hasText(memberModifyDTO.getNickname())) {
            // 닉네임 중복 확인을 하지 않았더라도 DB 에 저장될 때 유니크 제약조건에 의해 예외 발생 -> Controller 에서 처리
            member.setNickname(memberModifyDTO.getNickname());
        }

        if (StringUtils.hasText(memberModifyDTO.getEmail())) {
            // 이메일이 비어있는데 일반 회원인 경우
            if (!StringUtils.hasText(memberModifyDTO.getEmail()) && !socialFlag) {
                throw new MemberModifyException(BAD_REQUEST, FAIL, "일반회원은 이메일을 비워둘 수 없습니다");
            }
            member.setEmail(memberModifyDTO.getEmail());
        }

        if (StringUtils.hasText(memberModifyDTO.getNewPassword()) &&  !StringUtils.hasText(memberModifyDTO.getPrePassword())) {
            throw new MemberModifyException(BAD_REQUEST, FAIL, "비밀번호 변경 시에는 이전 비밀번호와 새 비밀번호를 입력해주세요");
        } else if (StringUtils.hasText(memberModifyDTO.getNewPassword()) && StringUtils.hasText(memberModifyDTO.getPrePassword())) {
            // 1. 이전 비밀번호 일치여부 확인
            boolean matchePre = passwordEncoder.matches(memberModifyDTO.getPrePassword(), member.getPassword());
            if (!matchePre) { // 일치하지 않는 경우
                throw new MemberModifyException(BAD_REQUEST, FAIL, "이전 비밀번호가 일치하지 않습니다");
            }
            // 2. 새로운 비밀번호 규칙 준수 확인
            String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$";
            boolean matchNew = memberModifyDTO.getNewPassword().matches(regex);
            if (!matchNew) {
                throw new MemberModifyException(BAD_REQUEST, FAIL, "비밀번호는 대문자, 소문자, 숫자를 포함한 최소 8자 이상, 20자 이하여야 합니다");
            }
            member.setPassword(passwordEncoder.encode(memberModifyDTO.getNewPassword()));
        }

        return member;
    }


    public MemberModifyDTO modifyProfileImage(Long memberId, MemberProfileImageDTO imageDTO) {
        // 프로필 이미지를 업로드하고 URL 을 받는다
        String imageURL = "";
        Member member = null;
        try {
            imageURL = firebaseService.saveFile(imageDTO.getProfileImage());
            if (!StringUtils.hasText(imageURL)) {
                throw new MemberModifyException(BAD_REQUEST, FAIL, "프로필 이미지가 전달되지 않았습니다");
            }
            member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다"));

            // 기본 프로필이 아닌 경우 기존 프로필 삭제
            if (!member.getProfile().equals("https://firebasestorage.googleapis.com/v0/b/vitalroutes-467cb.appspot.com/o/profile.png?alt=media")) {
                firebaseService.deleteFile(member.getProfile());
            }

            member.setProfile(imageURL);
        } catch (IOException exception) {
            throw new MemberModifyException(INTERNAL_SERVER_ERROR, ERROR, "파일 업로드 중 오류가 발생하였습니다");
        }
        return MemberModifyDTO.entityToDto(member);
    }

    public Member findMemberByEmail(MemberEmailDTO emailDTO) {
        String email = emailDTO.getEmail();
        if (!StringUtils.hasText(email)) {
            throw new MemberModifyException(BAD_REQUEST, FAIL, "이메일이 전달되지 않았습니다");
        }
        return memberRepository.findByEmail(emailDTO.getEmail())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 이메일입니다"));
    }


    public void modifyPassword(Long memberId, MemberPasswordDTO passwordDTO) {
        // 사용자 존재하는지 다시 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다"));

        // 비밀번호 규칙을 준수하는지 확인
        String password = passwordDTO.getPassword();
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$";
        boolean matches = password.matches(regex);
        if (!matches) {
            throw new MemberModifyException(BAD_REQUEST, FAIL, "비밀번호는 대문자, 소문자, 숫자를 포함한 최소 8자 이상, 20자 이하여야 합니다");
        }

        member.setPassword(passwordEncoder.encode(password));
    }

}
