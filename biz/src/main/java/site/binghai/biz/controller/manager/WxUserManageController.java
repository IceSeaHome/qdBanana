package site.binghai.biz.controller.manager;

import com.alibaba.fastjson.JSONObject;
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
        if(page == null) page = 0;
        if(pageSize == null) pageSize = 10;

        JSONObject data = new JSONObject();
        data.put("page",page);
        data.put("pageSize",pageSize);

        if(StringUtils.isNotBlank(search)){
            list = wxUserService.search(search);
        }else{
            list = wxUserService.findAll(page,pageSize);
            data.put("total",wxUserService.count());
            data.put("data",list);
        }

        return success(data, null);
    }
}
