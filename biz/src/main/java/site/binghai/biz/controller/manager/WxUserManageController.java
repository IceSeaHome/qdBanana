package site.binghai.biz.controller.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.service.WxUserService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.WxUser;

import java.util.List;
@RestController
@RequestMapping("/manage/user/")
public class WxUserManageController extends BaseController {
    @Autowired
    private WxUserService wxUserService;

    @GetMapping("list")
    public Object list(String search,Integer page,Integer pageSize) {
        List<WxUser> list = null;
        if(page == null) page = 0;
        if(pageSize == null) pageSize = 100;

        JSONObject data = new JSONObject();
        data.put("currentPage",page);
        data.put("pageSize",pageSize);

        if(StringUtils.isNotBlank(search)){
            list = wxUserService.search(search);
        }else{
            list = wxUserService.findAll(page,pageSize);
            data.put("total",wxUserService.count());
            data.put("list",formatList(list));
        }

        return success(data, null);
    }

    private Object formatList(List<WxUser> list) {
        JSONArray arr = newJSONArray();
        for (WxUser wxUser : list) {
            JSONObject item = toJsonObject(wxUser);
            item.put("cover",wxUser.getAvatar());
            item.put("gender","未知");
            item.put("regTime",wxUser.getCreatedTime());
            item.put("status","正常");
            item.put("info",wxUser.getUserName()+"/"+wxUser.getPhone()+"/"+wxUser.getUsuallyAddress());
            arr.add(item);
        }
        return arr;
    }
}
