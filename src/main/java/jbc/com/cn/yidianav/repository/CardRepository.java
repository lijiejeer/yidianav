package jbc.com.cn.yidianav.repository;

import jbc.com.cn.yidianav.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByCategoryIdOrderBySortOrder(Long categoryId);
    List<Card> findByNameContainingOrDescriptionContaining(String name, String description);
}
