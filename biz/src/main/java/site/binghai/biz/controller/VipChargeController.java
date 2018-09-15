package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.VipChargeOrder;
import site.binghai.biz.entity.VipPkg;
import site.binghai.biz.service.VipPkgService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user/vipCharge/")
public class VipChargeController extends PayBizController<VipChargeOrder> {
    @Autowired
    private VipPkgService vipPkgService;

    @GetMapping("prepare")
    public String prepare(ModelMap map) {
        map.put("pkgs", vipPkgService.findAll(999));
        return "vipCharge";
    }

    @GetMapping("create")
    public String create(@RequestParam Long pkgId) throws Exception {
        Map map = new HashMap();
        VipPkg pkg = vipPkgService.findById(pkgId);
        map.put("pkgInfo", toJsonObject(pkg).toJSONString());
        return "redirect:" + create(map, pkg.getPrice()).getString("msg");
    }
}
