package site.binghai.biz.controller.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.AddressBook;
import site.binghai.biz.service.AddressBookService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.Manager;

import java.util.Map;

@RestController
@RequestMapping("/manage/addressBook/")
public class AddressBookController extends BaseController {
    @Autowired
    private AddressBookService addressBookService;

    @PostMapping("create")
    public Object create(@RequestBody Map map) {
        AddressBook book = addressBookService.newAndSave(map);
        return success(book, null);
    }

    @GetMapping("delete")
    public Object delete(@RequestParam Long id) {
        addressBookService.delete(id);
        logger.warn("manager deleted addressBook {}", getSessionPersistent(Manager.class));
        return success();
    }
}
