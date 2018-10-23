package site.binghai.biz.controller.manager;

import org.apache.http.util.Asserts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.SysConfig;
import site.binghai.biz.service.SysConfigService;
import site.binghai.lib.controller.BaseController;

import java.util.Map;

@RestController
@RequestMapping("/manage/system/")
public class SystemController extends BaseController {
    @Autowired
    private SysConfigService sysConfigService;

    @PostMapping("update")
    public Object update(@RequestBody Map map) {
        Asserts.check(readOnly(), "read only mananger!");
        SysConfig config = sysConfigService.newInstance(map);
        if (hasEmptyString(config.getCloseMessage(), config.getCloseSystem())) {
            return fail("设置不完整!");
        }

        sysConfigService.setSystem(config);
        return success();
    }

    @GetMapping("getConfig")
    public Object get() {
        return success(sysConfigService.getConfig(), null);
    }
}
