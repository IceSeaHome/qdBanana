package site.binghai.biz.controller.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.service.ExpSendService;
import site.binghai.biz.service.ExpTakeService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.Manager;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.service.UnifiedOrderService;

import java.util.List;
import java.util.Map;

@RequestMapping("/manage/expSend/")
public class ExpSendManageController extends BaseController {

    @Autowired
    private ExpSendService sendService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @GetMapping("list")
    private Object list(@RequestParam Integer status, @RequestParam Integer page, @RequestParam Integer pageSize) {
        List ls = sendService.findByStatusIdDesc(status, page, pageSize);
        return success(ls, null);
    }

    @PostMapping("update")
    public Object update(@RequestBody Map map) {
        try {
            sendService.updateAndSave(getSessionPersistent(Manager.class), map);
        } catch (Exception e) {
            logger.error("update expSend order failed!", e);
            return fail(e.getMessage());
        }

        return success();
    }

    @GetMapping("cancel")
    public Object cancel(@RequestParam Long unifiedId) {
        if (unifiedOrderService.cancel(unifiedId)) {
            UnifiedOrder unifiedOrder = new UnifiedOrder();
            unifiedOrder.setId(unifiedId);
            sendService.cancel(unifiedOrder);
        }
        return success();
    }
}
