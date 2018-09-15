package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class VipPkg extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Integer price; // 售价
    private Integer recharge; // 充值金额
}
