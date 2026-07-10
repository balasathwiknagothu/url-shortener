package com.sathwik.url_shortener.repository;

import com.sathwik.url_shortener.entity.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    List<ClickEvent> findByUrlIdOrderByClickedAtDesc(Long urlId);
}