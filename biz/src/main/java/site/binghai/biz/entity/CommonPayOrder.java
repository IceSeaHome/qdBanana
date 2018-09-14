package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;
import site.binghai.lib.enums.OrderStatusEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class CommonPayOrder extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private Integer fee;
    private Boolean paid;
    private Long unifiedId;
    private String payName;
    private String remark;
    /**
     * {@link OrderStatusEnum}
     * */
    private Integer status;
}
