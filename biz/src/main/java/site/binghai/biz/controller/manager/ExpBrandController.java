package site.binghai.biz.controller.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.ExpBrand;
import site.binghai.biz.service.ExpBrandService;
import site.binghai.lib.controller.BaseController;

import java.util.Map;

@RestController
@RequestMapping("/manage/expBrand/")
public class ExpBrandController extends BaseController {
    @Autowired
    private ExpBrandService expBrandService;

    @PostMapping("create")
    public Object create(@RequestBody Map map) {
        ExpBrand expBrand = expBrandService.newInstance(map);
        if (hasEmptyString(expBrand.getExpName(), expBrand.getSendServiceFee(), expBrand.getTakeServiceFee(),
            expBrand.getServiceUrl())) {
            return fail("尚有必填项未填写完整");
        }

        expBrandService.save(expBrand);
        return success();
    }

    @PostMapping("update")
    public Object update(@RequestBody Map map){
        Long id = getLong(map,"id");
        ExpBrand expBrand = expBrandService.findById(id);
        if(expBrand == null){
            return fail("不存在!");
        }

        expBrand = expBrandService.updateParams(expBrand,map);

        if (hasEmptyString(expBrand.getExpName(), expBrand.getSendServiceFee(), expBrand.getTakeServiceFee(),
            expBrand.getServiceUrl())) {
            return fail("尚有必填项未填写完整");
        }

        expBrandService.save(expBrand);

        return success();
    }

    @GetMapping("delete")
    public Object delete(@RequestParam Long id){
        expBrandService.delete(id);
        return success();
    }
}
