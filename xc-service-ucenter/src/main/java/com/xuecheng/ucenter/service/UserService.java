package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:zhangl
 * @date:2019/7/13
 * @description:
 */
@Service
public class UserService {
    @Autowired
    private XcUserRepository xcUserRepository;
    @Autowired
    private XcMenuMapper menuMapper;
    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;
    //根据用户名称查询用户信息
    private XcUser getXcUser(String username){
        return  xcUserRepository.findXcUserByUsername(username);
    }
    //根据用户名称查询用户信息和扩展信息
    public XcUserExt getUserExt(String username){
        XcUser xcUser = this.getXcUser(username);
        if(xcUser==null){
          return null;
        }
        //根据用户id查询用户权限
        List<XcMenu> xcMenus = menuMapper.selectPermissionByUserId(xcUser.getId());
        XcUserExt userExt=new XcUserExt();
        BeanUtils.copyProperties(xcUser,userExt);
        if(xcMenus==null){
            xcMenus=new ArrayList<>();
        }
        userExt.setPermissions(xcMenus);
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findXcCompanyUserByUserId(xcUser.getId());
        if (xcCompanyUser!=null) {
            String companyId = xcCompanyUser.getCompanyId();
            userExt.setCompanyId(companyId);
        }
        return userExt;
    }
}
