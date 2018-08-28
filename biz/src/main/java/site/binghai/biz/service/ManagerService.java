package site.binghai.biz.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.entity.Manager;
import site.binghai.lib.service.BaseService;

@Service
public class ManagerService extends BaseService<Manager> {

    public Manager login(String userName, String passWord) {
        Manager manager = new Manager();
        manager.setUserName(userName);
        manager.setPassWord(passWord);
        return queryOne(manager);
    }
}
