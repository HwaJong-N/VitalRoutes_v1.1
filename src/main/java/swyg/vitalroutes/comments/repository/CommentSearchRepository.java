package swyg.vitalroutes.comments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import swyg.vitalroutes.comments.domain.Comment;

import java.util.List;

public interface CommentSearchRepository {
    Page<Comment> findAllByParticipationId(Long participationId, List<Long> hideIds, Pageable pageable);

    long countByParticipationId(Long participationId, List<Long> hideIds);
}
