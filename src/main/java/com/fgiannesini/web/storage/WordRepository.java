package com.fgiannesini.web.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<WordDao, String> {

    @Query(value = """
            SELECT *
            FROM word w
            WHERE w.sm_repetitions < :masteryRepetitions
              AND (w.next_review is null or w.next_review < :aWeekAgo)
            ORDER BY w.next_review IS NULL DESC, w.checked_count, w.next_review
            LIMIT :limit
            """, nativeQuery = true)
    List<WordDao> getTopOrderByNextReview(@Param("limit") int limit, @Param("aWeekAgo") LocalDateTime aWeekAgo, @Param("masteryRepetitions") int masteryRepetitions);

    long countByNextReviewIsNull();

    long countBySmRepetitionsGreaterThanEqualAndSmRepetitionsLessThan(int minSmRepetitions, int maxSmRepetitions);
}
