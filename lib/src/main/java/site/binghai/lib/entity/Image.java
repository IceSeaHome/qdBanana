package site.binghai.lib.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Image extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Integer weight;
    private String url;
    private String name;
}
