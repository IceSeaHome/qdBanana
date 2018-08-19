package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 地址库
 */
@Data
@Entity
public class AddressBook extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

}
