package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.enums.PayBizEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class VipChargeOrder extends PayBizEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String pkgInfo; // 套餐信息json

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.VIP_CHARGE;
    }
}
