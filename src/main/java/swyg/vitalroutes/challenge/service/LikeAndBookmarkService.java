package swyg.vitalroutes.challenge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyg.vitalroutes.challenge.domain.Challenge;
import swyg.vitalroutes.challenge.domain.ChallengeLikeAndBookmark;
import swyg.vitalroutes.challenge.domain.ReactionType;
import swyg.vitalroutes.challenge.repository.ChallengeRepository;
import swyg.vitalroutes.challenge.repository.LikeAndBookmarkRepository;
import swyg.vitalroutes.common.exception.LikeAndBookmarkException;
import swyg.vitalroutes.common.response.ResponseType;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.repository.MemberRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeAndBookmarkService {

    private final LikeAndBookmarkRepository likeAndBookmarkRepository;
    private final MemberRepository memberRepository;
    private final ChallengeRepository challengeRepository;

    public void changeLikeOrBookmark(Member member, Long challengeId, ReactionType reactionType) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new LikeAndBookmarkException(HttpStatus.NOT_FOUND, ResponseType.FAIL, "챌린지를 찾을 수 없습니다"));
        Optional<ChallengeLikeAndBookmark> optional = likeAndBookmarkRepository.judgeMyReactionChallengeId(member, challenge, reactionType);
        // 좋아요나 북마크 추가
        if (optional.isEmpty()) {
            if (reactionType == ReactionType.LIKE) {
                challenge.increaseLikeCount();
            } else {
                challenge.increaseBookmarkCount();
            }
            ChallengeLikeAndBookmark challengeLikeAndBookmark = ChallengeLikeAndBookmark.createChallengeLikeAndBookmark(member, challenge, reactionType);
            likeAndBookmarkRepository.save(challengeLikeAndBookmark);
        } else {
            // 좋아요나 북마크 제거
            if (reactionType == ReactionType.LIKE) {
                challenge.decreaseLikeCount();
            } else {
                challenge.decreaseBookmarkCount();
            }
            ChallengeLikeAndBookmark challengeLikeAndBookmark = optional.get();
            likeAndBookmarkRepository.delete(challengeLikeAndBookmark);
        }
    }
}
