package site.binghai.biz.controller.manager;

import org.apache.http.util.Asserts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.Manager;
import site.binghai.lib.service.ImageService;

import java.util.Map;

@RestController
@RequestMapping("/manage/image/")
public class ImageManagerController extends BaseController {
    @Autowired
    private ImageService imageService;

    @PostMapping("create")
    public Object create(@RequestBody Map map) {
        Asserts.check(readOnly(), "read only mananger!");
        return success(imageService.newAndSave(map), null);
    }

    @PostMapping("update")
    public Object update(@RequestBody Map map) {
        Asserts.check(readOnly(), "read only mananger!");
        try {
            imageService.updateAndSave(getSessionPersistent(Manager.class), map);
        } catch (Exception e) {
            logger.error("admin update image error", e);
            return fail(e.getMessage());
        }
        return success();
    }

    @GetMapping("delete")
    public Object delete(@RequestParam Long id){
        Asserts.check(readOnly(), "read only mananger!");
        imageService.delete(id);
        return success();
    }
}
