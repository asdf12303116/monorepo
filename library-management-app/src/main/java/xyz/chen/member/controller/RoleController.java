package xyz.chen.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.chen.commons.base.BaseResponse;
import xyz.chen.member.entity.Role;
import xyz.chen.member.services.RoleService;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping("/list")
    public BaseResponse<List<Role>> getRoles() {
        return BaseResponse.ok(roleService.getAllRoles());
    }
}
