package swyg.vitalroutes.participation.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import swyg.vitalroutes.participation.domain.Participation;

import java.util.List;

import static swyg.vitalroutes.member.domain.QMember.member;
import static swyg.vitalroutes.participation.domain.QParticipation.*;

public class ParticipationSearchRepositoryImpl implements ParticipationSearchRepository {

    private final JPAQueryFactory queryFactory;

    public ParticipationSearchRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Participation> findAllByChallengeId(Long challengeId, List<Long> hidedIds, Pageable pageable) {
        List<Participation> content = queryFactory
                .selectFrom(participation)
                .join(participation.member, member)
                .where(challengeIdEq(challengeId), hideIdsNotContains(hidedIds))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(participation.participationId.desc())
                .fetch();

        Long total = queryFactory
                .select(participation.count())
                .from(participation)
                .join(participation.member, member)
                .where(challengeIdEq(challengeId), hideIdsNotContains(hidedIds))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private Predicate challengeIdEq(Long challengeId) {
        return participation.challenge.challengeId.eq(challengeId);
    }

    private Predicate hideIdsNotContains(List<Long> hidedIds) {
        if (hidedIds.isEmpty()) {
            return null;
        }
        return participation.participationId.notIn(hidedIds);
    }
}
