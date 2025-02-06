package xyz.chen.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.chen.commons.base.BaseResponse;
import xyz.chen.member.entity.UserRole;
import xyz.chen.member.entity.dto.RoleDto;
import xyz.chen.member.entity.dto.UserRoleSearchDto;
import xyz.chen.member.entity.dto.UserWithRole;
import xyz.chen.member.services.UserRoleService;

import java.util.List;

@RequestMapping("/userRole")
public class UseRoleController {
    @Autowired
    private UserRoleService userRoleService;
    @RequestMapping("/list")
    public BaseResponse<List<UserRole>> getUserRoles(@RequestBody UserRoleSearchDto userRoleSearchDto) {
        return BaseResponse.ok(userRoleService.getUserRoles(userRoleSearchDto));
    }

    @RequestMapping("/grant")
    public BaseResponse<String> grantUserRoles(@RequestBody UserWithRole userWithRoles) {
        userRoleService.grantRoles(userWithRoles.getId(), userWithRoles.getRoles().stream().map(RoleDto::roleId).toList());
        return BaseResponse.ok();
    }


}
