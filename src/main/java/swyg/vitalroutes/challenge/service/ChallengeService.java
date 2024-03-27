package swyg.vitalroutes.challenge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.challenge.domain.*;
import swyg.vitalroutes.challenge.dto.*;
import swyg.vitalroutes.challenge.repository.ChallengeRepository;
import swyg.vitalroutes.challenge.repository.LikeAndBookmarkRepository;
import swyg.vitalroutes.common.exception.ChallengeException;
import swyg.vitalroutes.common.exception.FileProcessException;
import swyg.vitalroutes.common.exception.ParticipationException;
import swyg.vitalroutes.common.response.DataWithCount;
import swyg.vitalroutes.common.utils.FileUtils;
import swyg.vitalroutes.challenge.domain.ChallengeLocation;
import swyg.vitalroutes.firebase.service.FirebaseService;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;
import static swyg.vitalroutes.common.response.ResponseType.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository;
    private final LikeAndBookmarkRepository likeAndBookmarkRepository;
    private final FirebaseService firebaseService;

    public void saveChallenge(Long memberId, ChallengeSaveDTO saveDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ChallengeException(NOT_FOUND, FAIL, "사용자가 존재하지 않습니다"));

        // 타입 생성( 도보, 자전거 )
        ChallengeType type = ChallengeType.findByKorean(saveDTO.getType());

        // 태그 리스트 생성
        List<ChallengeTag> tagList = new ArrayList<>();
        for (String tag : saveDTO.getTags()) {
            Tags byKorean = Tags.findByKorean(tag);
            if (byKorean == null) {
                continue;
            }
            String name = byKorean.name();
            tagList.add(ChallengeTag.createChallengeTag(name, tag));
        }

        String titleImgUrl = "";
        List<ChallengeLocation> challengeLocationList = new ArrayList<>();
        try {
            // 대표 이미지 저장
            MultipartFile titleImg = saveDTO.getTitleImg();
            titleImgUrl = firebaseService.saveFile(titleImg);

            // 이미지와 위치정보 저장
            List<MultipartFile> files = saveDTO.getFiles();
            int seq = 0;
            for (MultipartFile file : files) {
                String fileName = firebaseService.saveFile(file);
                double[] locationInfo = FileUtils.getLocationInfo(file);
                challengeLocationList.add(ChallengeLocation.createLocation(++seq, fileName, locationInfo));
            }
        } catch (FileProcessException exception) {
            throw new ParticipationException(exception.getStatus(), exception.getType(), exception.getMessage());
        } catch (Exception exception) {
            throw new ChallengeException(INTERNAL_SERVER_ERROR, ERROR, exception.getMessage());
        }

        Challenge challenge = Challenge.createChallenge(member, challengeLocationList, tagList, saveDTO.getTitle(), saveDTO.getContent(), titleImgUrl, type);
        challengeRepository.save(challenge);
    }

    // 챌린지 상세 조회 ( 좋아요 여부, 북마크 여부, 좋아요, 복마크 수는 자동 처리 )
    public ChallengeResponseDTO findChallenge(Member member, Long challengeId) {
        Challenge challenge = challengeRepository.findByChallengeId(challengeId)
                .orElseThrow(() -> new ChallengeException(NOT_FOUND, FAIL, "챌린지가 존재하지 않습니다"));
        challenge.increaseViewCount();

        // 좋아요 여부, 북마크 여부 판단
        Optional<ChallengeLikeAndBookmark> optionalLike = likeAndBookmarkRepository
                .judgeMyReactionChallengeId(member, challenge, ReactionType.LIKE);
        Optional<ChallengeLikeAndBookmark> optionalBookmark = likeAndBookmarkRepository
                .judgeMyReactionChallengeId(member, challenge, ReactionType.BOOKMARK);

        return new ChallengeResponseDTO(challenge, optionalLike.isPresent(), optionalBookmark.isPresent());
    }


    public void deleteChallenge(Long challengeId) {
        Challenge challenge = challengeRepository.findByChallengeId(challengeId)
                .orElseThrow(() -> new ChallengeException(NOT_FOUND, FAIL, "챌린지가 존재하지 않습니다"));
        challengeRepository.deleteById(challengeId);
    }

    // 챌린지 상세 조회 ( 좋아요 여부, 북마크 여부 )
    public DataWithCount<?> findAllChallenges(Member member, Pageable pageable, String searchType) {
        Page<ChallengeListDTO> allChallenge = challengeRepository.findAllChallenge(pageable, searchType);
        List<Long> myLikeChallengeId = likeAndBookmarkRepository.findMyReactionChallengeId(member, ReactionType.LIKE);
        List<Long> myBookmarkChallengeId = likeAndBookmarkRepository.findMyReactionChallengeId(member, ReactionType.BOOKMARK);
        for (ChallengeListDTO challengeListDTO : allChallenge.getContent()) {
            Long challengeId = challengeListDTO.getChallengeId();
            boolean likeContains = myLikeChallengeId.contains(challengeId);
            boolean bookmarkContains = myBookmarkChallengeId.contains(challengeId);
            if (likeContains) {
                challengeListDTO.setLikeFlag(true);
            }
            if (bookmarkContains) {
                challengeListDTO.setBookmarkFlag(true);
            }
        }

        long count = allChallenge.getTotalElements();
        boolean remainFlag = allChallenge.hasNext();

        return new DataWithCount<>(count, remainFlag, allChallenge.getContent());
    }

    public void modifyChallenge(Long challengeId, ChallengeModifyDTO modifyDTO) {
        Challenge challenge = challengeRepository.findByChallengeId(challengeId)
                .orElseThrow(() -> new ChallengeException(NOT_FOUND, FAIL, "챌린지가 존재하지 않습니다"));
        challenge.setTitle(modifyDTO.getTitle());
        challenge.setContent(modifyDTO.getContent());
    }



    // 내가 등록한 챌린지
    public DataWithCount<?> findMyChallenges(Member member, Pageable pageable) {
        Page<ChallengeListDTO> myChallenges = challengeRepository.findMyChallenges(member, pageable);
        long count = myChallenges.getTotalElements();
        boolean remainFlag = myChallenges.hasNext();
        return new DataWithCount<>(count, remainFlag, myChallenges.getContent());
    }

    // 내가 참여한 챌린지
    public DataWithCount<?> findParticipateChallenges(Member member, Pageable pageable) {
        Page<ChallengeListDTO> participateChallenge = challengeRepository.findParticipateChallenge(member, pageable);
        long count = participateChallenge.getTotalElements();
        boolean remainFlag = participateChallenge.hasNext();
        return new DataWithCount<>(count, remainFlag, participateChallenge.getContent());
    }

    // 내가 좋아요한 or 북마크한 챌린지
    public DataWithCount<?> findReactionChallenges(Member member, ReactionType type, Pageable pageable) {
        Page<ChallengeListDTO> reactionChallenges = challengeRepository.findReactionChallenges(member, type, pageable);
        for (ChallengeListDTO challenge : reactionChallenges.getContent()) {
            if (type == ReactionType.LIKE) {
                challenge.setLikeFlag(true);
            } else {
                challenge.setBookmarkFlag(true);
            }
        }
        long count = reactionChallenges.getTotalElements();
        boolean remainFlag = reactionChallenges.hasNext();
        return new DataWithCount<>(count, remainFlag, reactionChallenges.getContent());
    }

}
