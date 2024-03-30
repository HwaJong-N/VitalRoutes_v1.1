package swyg.vitalroutes.participation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.challenge.domain.Challenge;
import swyg.vitalroutes.challenge.repository.ChallengeRepository;
import swyg.vitalroutes.comments.repository.CommentRepository;
import swyg.vitalroutes.common.exception.FileProcessException;
import swyg.vitalroutes.common.exception.ParticipationException;
import swyg.vitalroutes.common.response.DataWithCount;
import swyg.vitalroutes.common.utils.FileUtils;
import swyg.vitalroutes.firebase.service.FirebaseService;
import swyg.vitalroutes.hide.repository.HideRepository;
import swyg.vitalroutes.challenge.domain.ChallengeLocation;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.repository.MemberRepository;
import swyg.vitalroutes.participation.domain.Participation;
import swyg.vitalroutes.participation.domain.ParticipationImage;
import swyg.vitalroutes.participation.dto.*;
import swyg.vitalroutes.participation.repository.ParticipationRepository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static swyg.vitalroutes.common.response.ResponseType.ERROR;
import static swyg.vitalroutes.common.response.ResponseType.FAIL;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final MemberRepository memberRepository;
    private final ChallengeRepository challengeRepository;
    private final CommentRepository commentRepository;
    private final HideRepository hideRepository;
    private final FirebaseService firebaseService;


    public DataWithCount<?> findParticipation(Long memberId, Long challengeId, Pageable pageable) {
        // 숨김처리한 참여 게시글의 ID 를 조회
        List<Long> hidedParticipations = hideRepository.findHidedParticipations(memberId);
        List<Long> hidedComments = hideRepository.findHidedComments(memberId);

        Page<Participation> pagingData = participationRepository.findAllByChallengeId(challengeId, hidedParticipations, pageable);
        List<ParticipationResponseDTO> dtoList = pagingData.map(ParticipationResponseDTO::new).toList();

        for (ParticipationResponseDTO dto : dtoList) {
            long size = commentRepository.countByParticipationId(dto.getParticipationId(), hidedComments);
            dto.setTotalComments(size);
        }

        long count = pagingData.getTotalElements(); // 총 데이터의 수
        boolean remainFlag = pagingData.hasNext();  // 현재까지 보여지고 있는 데이터 외에 남은 데이터가 있는지

        return new DataWithCount<>(count, remainFlag, dtoList);
    }


    public void saveParticipation(Long memberId, ParticipationSaveDTO saveDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "사용자가 존재하지 않습니다"));
        Challenge challenge = challengeRepository.findById(saveDTO.getChallengeId())
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "챌린지가 존재하지 않습니다"));

        // 챌린지 이미지 조회
        List<ChallengeLocation> challengeLocationList = challenge.getLocationList();
        List<ParticipationImage> participationImages = new ArrayList<>();
        List<MultipartFile> files = saveDTO.getFiles();

        if (files.size() != challengeLocationList.size()) {
            throw new ParticipationException(BAD_REQUEST, FAIL, "챌린지와 동일한 개수의 이미지를 업로드 해주세요");
        }

        int seq = 0;

        try {
            for (MultipartFile file : files) {
                String fileName = firebaseService.saveFile(file);
                double[] locationInfo = FileUtils.getLocationInfo(file);

                // 챌린지 이미지와 위치 비교
                ChallengeLocation challengeLocation = challengeLocationList.get(seq);
                double distance = FileUtils.calDistance(locationInfo[0], locationInfo[1], challengeLocation.getLatitude(), challengeLocation.getLongitude());
                log.info("distance = {}", distance);
                if (distance > 5) {
                    throw new ParticipationException(BAD_REQUEST, FAIL, (seq + 1) + "번째 지점의 거리가 멀리 떨어져있습니다. 챌린지 지점과의 거리는 5m 이하여야 합니다. 현재거리 = " + (int) distance + "m");
                }
                participationImages.add(ParticipationImage.createParticipationImage(++seq, fileName));
            }
        } catch (FileProcessException exception) {
            throw new ParticipationException(exception.getStatus(), exception.getType(), exception.getMessage());
        } catch (Exception exception) {
            throw new ParticipationException(INTERNAL_SERVER_ERROR, ERROR, exception.getMessage());
        }

        Participation participation = Participation.createParticipation(saveDTO.getContent(), member, challenge, participationImages);
        participationRepository.save(participation);
    }

    public void deleteParticipation(Long participationId) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "참여 게시글이 존재하지 않습니다"));

        List<ParticipationImage> participationImages = participation.getParticipationImages();
        for (ParticipationImage participationImage : participationImages) {
            firebaseService.deleteFile(participationImage.getFileName());
        }

        participationRepository.deleteById(participationId);
    }

    public ParticipationResponseDTO findById(Long participationId) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "참여 게시글이 존재하지 않습니다"));
        return new ParticipationResponseDTO(participation);
    }

    /**
     * 이미지 변경 2가지 선택지
     * 1. 변경할 이미지를 업로드한다
     * 2. 변경할 이미지 없이 내용만 변경된다면 내용을 변경하는 API 를 호출한다
     */
    public ImageResponseDTO uploadImage(ImageSaveDTO imageDTO) {
        String url = "modifyURL";
        MultipartFile file = imageDTO.getFile();
        double[] locationInfo = FileUtils.getLocationInfo(file);

        // 챌린지의 사진과 위치 비교
        int sequence = imageDTO.getSequence();
        Challenge challenge = challengeRepository.findById(imageDTO.getChallengeId())
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "챌린지가 존재하지 않습니다"));
        List<ChallengeLocation> challengeLocationList = challenge.getLocationList();
        ChallengeLocation challengeLocation = challengeLocationList.get(sequence - 1);

        double distance = FileUtils.calDistance(locationInfo[0], locationInfo[1], challengeLocation.getLatitude(), challengeLocation.getLongitude());
        log.info("distance = {}", distance);
        if (distance > 5) {
            throw new ParticipationException(BAD_REQUEST, FAIL, sequence + "번째 지점의 거리가 멀리 떨어져있습니다. 챌린지 지점과의 거리는 5m 이하여야 합니다. 현재거리 = " + (int) distance + "m");
        }

        try {
            url = firebaseService.saveFile(file);
        } catch (FileProcessException exception) {
            throw new ParticipationException(exception.getStatus(), exception.getType(), exception.getMessage());
        } catch (Exception exception) {
            throw new ParticipationException(INTERNAL_SERVER_ERROR, ERROR, exception.getMessage());
        }

        return new ImageResponseDTO(sequence, url);
    }

    public void modifyParticipation(Long participationId, ParticipationModifyDTO modifyDTO) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "참여 게시글이 존재하지 않습니다"));
        participation.setContent(modifyDTO.getContent());
        // 파일 변경

        List<ParticipationImage> newImages = modifyDTO.getUploadedFiles().stream()
                .map(imageResponseDTO -> ParticipationImage
                        .createParticipationImage(imageResponseDTO.getSequence(), imageResponseDTO.getFileName()))
                .toList();
        participation.getParticipationImages().clear();
        for (ParticipationImage newImage : newImages) {
            participation.getParticipationImages().add(newImage);
        }
    }
}
