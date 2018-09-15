package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
/** 代取快递订单 */
@Entity
@Data
public class ExpTakeOrder extends PayBizEntity {
    @Id
    @GeneratedValue
    private Long id;

    private Long totalFee;

    /**
     * 取件短信
     * */
    private String smsText;
    /**
     * 快递名
     * */
    private String expName;
    /**
     * 对应数据库快递品牌id
     * */
    private Long expId;

    /**
     * 取件人姓名
     * */
    private String expTakeName;
    /**
     * 取件人手机号
     * */
    private String expTakePhone;

    /**
     * 配送地址
     * */
    private String sendAddr;
    /**
     * 配送手机号
     * */
    private String sendPhone;

    private String remark;

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.EXP_TAKE;
    }
}
