package swyg.vitalroutes.challenge.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import swyg.vitalroutes.challenge.domain.ChallengeType;
import swyg.vitalroutes.challenge.domain.Tags;
import swyg.vitalroutes.challenge.dto.ChallengeListDTO;

import java.util.List;

import static swyg.vitalroutes.challenge.domain.QChallenge.*;
import static swyg.vitalroutes.participation.domain.QParticipation.*;

public class ChallengeSearchRepositoryImpl implements ChallengeSearchRepository {

    private final JPAQueryFactory queryFactory;

    public ChallengeSearchRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * @Query("select new swyg.vitalroutes.challenge.dto.ChallengeListDTO(c.challengeId, c.title, c.titleImg, count(cp))
     * from Challenge c
     * left join c.participationList cp
     * group by c.challengeId
     * order by c.challengeId desc")
     */

    @Override
    public Page<ChallengeListDTO> findAllChallenge(Pageable pageable, String searchType) {
        List<ChallengeListDTO> content = queryFactory
                .select(Projections.constructor(ChallengeListDTO.class,
                        challenge.challengeId,
                        challenge.title,
                        challenge.titleImg,
                        challenge.participationList.size()))
                .from(challenge)
                .where(tagContains(searchType), typeEq(searchType))
                .leftJoin(challenge.participationList, participation)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .groupBy(challenge.challengeId)
                .orderBy(searchTypeOrderBy(searchType))
                .fetch();

        Long total = queryFactory
                .select(challenge.countDistinct())
                .from(challenge)
                .where(tagContains(searchType), typeEq(searchType))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private Predicate typeEq(String searchType) {
        if (StringUtils.hasText(searchType)) {
            ChallengeType type = ChallengeType.findByKorean(searchType);
            if (type != null) {
                return challenge.type.eq(type);
            }
        }
        return null;
    }

    private Predicate tagContains(String searchType) {
        if (StringUtils.hasText(searchType)) {
            Tags searchTag = Tags.findByKorean(searchType);
            if (searchTag != null) {
                return challenge.tagList.any().tagEn.contains(searchTag.name());
            }
        }
        return null;
    }

    private OrderSpecifier<?> searchTypeOrderBy(String searchType) {
        return switch (searchType) {
            case "인기순" -> new OrderSpecifier<>(Order.DESC, challenge.participationList.size()); // 참여자 많은 순
            case "추천순" -> new OrderSpecifier<>(Order.DESC, challenge.likeCount); // 좋아요 많은 순
            default -> new OrderSpecifier<>(Order.DESC, challenge.challengeId);
        };
    }
}
