package swyg.vitalroutes.comments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyg.vitalroutes.comments.domain.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentSearchRepository {
}
