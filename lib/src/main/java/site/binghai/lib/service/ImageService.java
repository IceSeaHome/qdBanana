package site.binghai.lib.service;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import site.binghai.lib.entity.Image;

import java.util.Collections;
import java.util.List;

@Service
public class ImageService extends BaseService<Image> {

    public List<Image> listRollingImage() {
        List<Image> all = findAll(999);
        Collections.sort(all);
        return all;
    }
}
