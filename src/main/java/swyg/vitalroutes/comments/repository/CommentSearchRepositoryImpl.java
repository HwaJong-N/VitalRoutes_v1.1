package swyg.vitalroutes.comments.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import swyg.vitalroutes.comments.domain.Comment;

import java.util.List;

import static swyg.vitalroutes.comments.domain.QComment.*;
import static swyg.vitalroutes.member.domain.QMember.member;


public class CommentSearchRepositoryImpl implements CommentSearchRepository {

    private final JPAQueryFactory queryFactory;

    public CommentSearchRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Comment> findAllByParticipationId(Long participationId, List<Long> hideIds, Pageable pageable) {
        List<Comment> content = queryFactory
                .selectFrom(comment)
                .join(comment.member, member)
                .where(participationIdEq(participationId), hideIdsNotContains(hideIds))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = countByParticipationId(participationId, hideIds);
        return new PageImpl<>(content, pageable, total);
    }


    @Override
    public long countByParticipationId(Long participationId, List<Long> hideIds) {
        Long total = queryFactory
                .select(comment.count())
                .from(comment)
                .join(comment.member, member)
                .where(participationIdEq(participationId), hideIdsNotContains(hideIds))
                .fetchOne();
        return total;
    }


    private Predicate participationIdEq(Long participationId) {
        return comment.participation.participationId.eq(participationId);
    }


    private Predicate hideIdsNotContains(List<Long> hidedIds) {
        if (hidedIds.isEmpty()) {
            return null;
        }
        return comment.commentId.notIn(hidedIds);
    }
}
