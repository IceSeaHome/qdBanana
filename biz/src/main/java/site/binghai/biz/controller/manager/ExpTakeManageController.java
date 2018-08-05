package site.binghai.biz.controller.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.biz.service.ExpTakeService;
import site.binghai.lib.controller.BaseController;

import java.util.List;

@RequestMapping("/manage/expTake/")
public class ExpTakeManageController extends BaseController {

    @Autowired
    private ExpTakeService takeService;

    @GetMapping("list")
    private Object list(@RequestParam Integer status, @RequestParam Integer page, @RequestParam Integer pageSize) {
        List ls = takeService.findByStatus(status,page,pageSize);
        return null;
    }
}
