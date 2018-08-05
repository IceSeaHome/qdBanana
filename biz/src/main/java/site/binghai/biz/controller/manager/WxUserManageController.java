package site.binghai.biz.controller.manager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.binghai.biz.service.WxUserService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.WxUser;

import java.util.List;

@RequestMapping("/manage/user/")
public class WxUserManageController extends BaseController {
    @Autowired
    private WxUserService wxUserService;

    @GetMapping("list")
    public Object list(String search,Integer page,Integer pageSize) {
        List<WxUser> list = null;

        if(StringUtils.isNotBlank(search)){
            list = wxUserService.search(search);
        }else{
            list = wxUserService.findAll(page,pageSize);
        }

        return success(list, null);
    }
}
