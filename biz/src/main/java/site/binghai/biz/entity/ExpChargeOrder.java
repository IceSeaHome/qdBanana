package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;
import site.binghai.lib.enums.OrderStatusEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class ExpChargeOrder extends BaseEntity {
    @Id
    @GeneratedValue
    private String id;
    private Long userId;
    private Integer fee;
    private Boolean paid;
    private Long unifiedId;
    private Long expId;
    /**
     * {@link OrderStatusEnum}
     * */
    private Integer status;
}
