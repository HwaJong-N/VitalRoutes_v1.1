package swyg.vitalroutes.challenge.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import swyg.vitalroutes.challenge.domain.ChallengeType;
import swyg.vitalroutes.challenge.domain.ReactionType;
import swyg.vitalroutes.challenge.domain.Tags;
import swyg.vitalroutes.challenge.dto.*;
import swyg.vitalroutes.challenge.service.ChallengeService;
import swyg.vitalroutes.challenge.service.LikeAndBookmarkService;
import swyg.vitalroutes.common.exception.ChallengeException;
import swyg.vitalroutes.common.exception.FileProcessException;
import swyg.vitalroutes.common.response.ApiResponseDTO;
import swyg.vitalroutes.common.response.DataWithCount;
import swyg.vitalroutes.member.domain.Member;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static swyg.vitalroutes.common.response.ResponseType.FAIL;
import static swyg.vitalroutes.common.response.ResponseType.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/challenge")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final LikeAndBookmarkService likeAndBookmarkService;

    // 챌린지 목록 조회 ( 좋아요, 북마크 했는지 여부 )
    @GetMapping("/list")
    public ApiResponseDTO<?> viewChallengeList(@PageableDefault(size = 12) Pageable pageable, @RequestParam(value = "searchType", defaultValue = "") String searchType) {
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DataWithCount<?> allChallenges = challengeService.findAllChallenges(member, pageable, searchType);
        return new ApiResponseDTO<>(OK, SUCCESS, null, allChallenges);
    }

    // 챌린지 상세 화면 ( 좋아요, 북마크 했는지 여부, 좋아요 수, 북마크 수 )
    @GetMapping("/{challengeId}")
    public ApiResponseDTO<?> viewChallenge(@PathVariable Long challengeId) {
        ChallengeResponseDTO responseDTO = null;
        try {
            Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            responseDTO = challengeService.findChallenge(member, challengeId);
        } catch (ChallengeException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, null, responseDTO);
    }

    // 내가 등록한 챌린지
    @GetMapping("/my-challenges")
    public ApiResponseDTO<?> viewMyChallenges(@PageableDefault(size = 12) Pageable pageable) {
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DataWithCount<?> myChallenges = challengeService.findMyChallenges(member, pageable);
        return new ApiResponseDTO<>(OK, SUCCESS, null, myChallenges);
    }

    // 내가 참여한 챌린지
    @GetMapping("/participate-challenges")
    public ApiResponseDTO<?> viewParticipateChallenges(@PageableDefault(size = 12) Pageable pageable) {
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DataWithCount<?> participateChallenges = challengeService.findParticipateChallenges(member, pageable);
        return new ApiResponseDTO<>(OK, SUCCESS, null, participateChallenges);
    }

    // 내가 좋아요한 챌린지
    @GetMapping("/like-challenges")
    public ApiResponseDTO<?> viewLikeChallenges(@PageableDefault(size = 12) Pageable pageable) {
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DataWithCount<?> reactionChallenges = challengeService.findReactionChallenges(member, ReactionType.LIKE, pageable);
        return new ApiResponseDTO<>(OK, SUCCESS, null, reactionChallenges);
    }


    // 내가 북마크한 챌린지
    @GetMapping("/bookmark-challenges")
    public ApiResponseDTO<?> viewBookmarkChallenges(@PageableDefault(size = 12) Pageable pageable) {
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DataWithCount<?> reactionChallenges = challengeService.findReactionChallenges(member, ReactionType.BOOKMARK, pageable);
        return new ApiResponseDTO<>(OK, SUCCESS, null, reactionChallenges);
    }



    // 등록 화면에 보여줄 챌린지 타입과 태그들
    @GetMapping("/save")
    public ApiResponseDTO<?> saveView() {
        List<Map<String, String>> types = Arrays.stream(ChallengeType.values()).map(type -> Map.of(type.name(), type.getValue())).toList();
        List<Map<String, String>> tags = Arrays.stream(Tags.values()).map(tag -> Map.of(tag.name(), tag.getValue())).toList();
        ChallengeSaveViewDTO challengeSaveViewDTO = new ChallengeSaveViewDTO(types, tags);
        return new ApiResponseDTO<>(OK, SUCCESS, null, challengeSaveViewDTO);
    }


    @PostMapping("/save")
    public ApiResponseDTO<?> saveChallenge(@Valid ChallengeSaveDTO saveDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponseDTO<>(BAD_REQUEST, FAIL, bindingResult.getFieldError().getDefaultMessage(), null);
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Member member = (Member) authentication.getPrincipal();
            challengeService.saveChallenge(member.getMemberId(), saveDTO);
        } catch (FileProcessException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        } catch (ChallengeException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }

        return new ApiResponseDTO<>(OK, SUCCESS, "챌린지 등록이 완료되었습니다", null);
    }

    // 챌린지 수정, 제목과 내용만 수정 가능
    @PatchMapping("/{challengeId}")
    public ApiResponseDTO<?> modifyChallenge(@PathVariable Long challengeId, @RequestBody ChallengeModifyDTO modifyDTO) {
        try {
            challengeService.modifyChallenge(challengeId, modifyDTO);
        } catch (ChallengeException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "챌린지가 수정되었습니다", null);
    }



    @DeleteMapping("/{challengeId}")
    public ApiResponseDTO<?> deleteChallenge(@PathVariable Long challengeId) {
        try {
            challengeService.deleteChallenge(challengeId);
        } catch (ChallengeException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "챌린지가 삭제되었습니다", null);
    }


    // 챌린지 좋아요
    @PostMapping("/{challengeId}/like")
    public ApiResponseDTO<?> likeChallenge(@PathVariable Long challengeId) {
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        likeAndBookmarkService.changeLikeOrBookmark(member, challengeId, ReactionType.LIKE);
        return new ApiResponseDTO<>(OK, SUCCESS, null, null);
    }

    // 챌린지 북마크
    @PostMapping("/{challengeId}/bookmark")
    public ApiResponseDTO<?> bookmarkChallenge(@PathVariable Long challengeId) {
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        likeAndBookmarkService.changeLikeOrBookmark(member, challengeId, ReactionType.BOOKMARK);
        return new ApiResponseDTO<>(OK, SUCCESS, null, null);
    }

}
