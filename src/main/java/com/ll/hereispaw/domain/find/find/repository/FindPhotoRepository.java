package com.ll.hereispaw.domain.find.find.repository;

import com.ll.hereispaw.domain.find.find.entity.Photo;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FindPhotoRepository extends JpaRepository<Photo, Long> {
    Photo findByPostId(Long post_id);
}
