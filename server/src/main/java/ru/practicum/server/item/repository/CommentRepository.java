package ru.practicum.server.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.server.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemIdOrderByCreatedDesc(Long userId);

    @Query("SELECT c FROM Comment c WHERE c.item.id IN (:ids) ORDER BY c.created")
    List<Comment> findByItemsIds(@Param("ids") List<Long> ids);
}