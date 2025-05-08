package com.fgiannesini.web.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<WordDao, String> {

    @Query(value = "SELECT * FROM word w WHERE w.learnt_count < 2 ORDER BY w.learnt_moment IS NULL DESC, w.checked_count, w.learnt_moment LIMIT :limit", nativeQuery = true)
    List<WordDao> getTopOrderByLearntMoment(@Param("limit") int limit);

    long countByLearntMomentIsNull();

    long countByLearntCountLessThan(int learntCount);
}
