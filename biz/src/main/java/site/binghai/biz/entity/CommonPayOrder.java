package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class CommonPayOrder extends PayBizEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Integer fee;
    private String payName;
    private String remark;

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.COMMON_PAY;
    }
}
