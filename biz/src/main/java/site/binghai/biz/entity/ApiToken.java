package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class ApiToken extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String token;
    private String holderName;
    private Integer invokeTimes;
    private String lastInvokeTime;
}
