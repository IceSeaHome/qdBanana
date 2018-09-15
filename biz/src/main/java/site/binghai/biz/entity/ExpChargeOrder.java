package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class ExpChargeOrder extends PayBizEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Integer fee;
    private Long expId;

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.EXP_CHARGE;
    }
}
