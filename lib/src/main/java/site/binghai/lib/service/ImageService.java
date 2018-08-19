package site.binghai.lib.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.entity.Image;

import java.util.List;

@Service
public class ImageService extends BaseService<Image> {

    public List<Image> listRollingImage() {
        List<Image> all = findAll(999);
        all.sort((a,b) -> b.getWeight() - a.getWeight());
        return all;
    }
}
