package com.fgiannesini.web.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<WordDao, String> {

    @Query(value = """
            SELECT *
            FROM word w
            WHERE w.sm_repetitions < :masteryRepetitions
              AND (w.next_review is null or w.next_review <= :now)
              AND w.translation NOT IN (:excluded)
            ORDER BY w.next_review IS NULL, w.next_review, w.checked_count DESC, w.word
            LIMIT :limit
            """, nativeQuery = true)
    List<WordDao> getTopOrderByNextReview(@Param("limit") int limit, @Param("now") LocalDateTime now, @Param("masteryRepetitions") int masteryRepetitions, @Param("excluded") Collection<String> excluded);

    List<WordKeyProjection> findAllProjectedBy();

    long countByNextReviewIsNull();

    long countBySmRepetitionsGreaterThanEqualAndSmRepetitionsLessThan(int minSmRepetitions, int maxSmRepetitions);
}
