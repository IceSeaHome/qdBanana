package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;
import site.binghai.lib.enums.OrderStatusEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class ExpSendOrder extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private Long unifiedId;
    private Long totalFee;
    private Boolean paid;

    private Long expId;
    private String expName;
    /**
     * {@link OrderStatusEnum}
     */
    private Integer status;

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
}
