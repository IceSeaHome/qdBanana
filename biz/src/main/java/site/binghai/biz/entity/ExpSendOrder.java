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
public class ExpSendOrder extends PayBizEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long totalFee;

    private Long expId;
    private String expName;

    /**
     * 取件姓名
     */
    private String fetchName;
    /**
     * 取件地址
     */
    private String fetchAddr;
    /**
     * 取件手机号
     */
    private String fetchPhone;

    /**
     * 后期录入的快递单号
     */
    private String expNo;

    private String remark;

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.EXP_SEND;
    }
}
