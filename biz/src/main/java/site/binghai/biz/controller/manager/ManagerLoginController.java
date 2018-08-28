package site.binghai.biz.controller.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.service.ManagerService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.Manager;

import java.util.Map;

@RestController
@RequestMapping("/")
public class ManagerLoginController extends BaseController {
    @Autowired
    private ManagerService managerService;

    @RequestMapping("mlogin")
    public Object mlogin(@RequestBody Map map){
        String userName = getString(map,"userName");
        String passWord = getString(map,"passWord");
        if(hasEmptyString(userName,passWord)){
            return fail("登录失败");
        }
        Manager manager = managerService.login(userName,passWord);
        if(manager == null){
            return fail("登录失败");
        }

        persistent(manager);
        return success();
    }
}
