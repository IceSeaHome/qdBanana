package site.binghai.lib.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.lib.entity.WxUser;



public interface WxUserDao extends JpaRepository<WxUser, Long> {
    Long countByCreatedBetween(Long createdAfter,Long createdBefore);
}
