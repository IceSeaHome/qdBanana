package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.service.ExpSendOrderService;
import site.binghai.lib.controller.BaseController;

@RestController
@RequestMapping("/user/expSend/")
public class ExpSendController extends BaseController {
    @Autowired
    private ExpSendOrderService expSendOrderService;


}
