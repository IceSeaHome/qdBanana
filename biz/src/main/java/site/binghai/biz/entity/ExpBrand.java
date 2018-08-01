package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/** 快递品牌 中通。。。*/

@Data
@Entity
public class ExpBrand extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String expName;

    /**
     * 寄件服务费
     * */
    private Long sendServiceFee;
    /**
     * 取件费用
     * */
    private Long takeServiceFee;

    private Boolean enableSend;
    private Boolean enableTake;

}
