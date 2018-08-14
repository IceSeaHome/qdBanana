package site.binghai.biz.service.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.biz.entity.ExpTakeOrder;

import java.util.List;

public interface ExpTakeDao extends JpaRepository<ExpTakeOrder, Long> {
    List<ExpTakeOrder> findAllByStatusOrderByIdDesc(Integer status, Pageable pageable);
}
