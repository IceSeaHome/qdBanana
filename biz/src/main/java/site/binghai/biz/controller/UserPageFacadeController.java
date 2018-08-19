package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.Image;
import site.binghai.lib.service.ImageService;

import java.util.List;

@RestController
@RequestMapping("/user/pageFacade/")
public class UserPageFacadeController extends BaseController {

    @Autowired
    private ImageService imageService;

    @GetMapping("listRollingImage")
    public Object listRollingImage() {
        List<Image> imageList = imageService.listRollingImage();
        return success(imageList, null);
    }
}
