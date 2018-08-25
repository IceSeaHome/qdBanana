package site.binghai.lib.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Image extends BaseEntity implements Comparable{
    @Id
    @GeneratedValue
    private Long id;
    private Integer weight;
    private String url;
    private String name;

    @Override
    public int compareTo(Object o) {
        Image that = (Image) o;
        return that.getWeight() - this.getWeight();
    }
}
