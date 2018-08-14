package site.binghai.biz.service.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.biz.entity.ExpSendOrder;

import java.util.List;

public interface ExpSendDao extends JpaRepository<ExpSendOrder, Long> {
    List<ExpSendOrder> findAllByStatusOrderByIdDesc(Integer status, Pageable pageable);
}
