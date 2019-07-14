package com.xuecheng.ucenter.coontroller;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.UcenterControllerApi;
import com.xuecheng.ucenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:zhangl
 * @date:2019/7/13
 * @description:
 */
@RestController
@RequestMapping("/ucenter")
public class UcenterController implements UcenterControllerApi {
    @Autowired
    private UserService userService;
    @Override
    @GetMapping("/getuserext")
    public XcUserExt getUserext(@RequestParam("username") String username) {
        XcUserExt user = userService.getUserExt(username);
        return user;
    }
}
