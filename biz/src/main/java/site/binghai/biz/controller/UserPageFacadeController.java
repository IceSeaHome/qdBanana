package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.Image;
import site.binghai.lib.service.ImageService;

import java.util.List;

@Controller
@RequestMapping("/user/my/")
public class UserPageFacadeController extends BaseController {

    @Autowired
    private ImageService imageService;

    @RequestMapping("index")
    public String listRollingImage(ModelMap map) {
        List<Image> imageList = imageService.listRollingImage();
        map.put("imgs", imageList);
        return "index";
    }
}
