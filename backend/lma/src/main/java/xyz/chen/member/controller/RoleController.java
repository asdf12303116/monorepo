package xyz.chen.member.controller;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.chen.commons.base.BaseResponse;
import xyz.chen.commons.base.STATUS_CODE;
import xyz.chen.member.entity.Role;
import xyz.chen.member.entity.dto.RoleDto;
import xyz.chen.member.entity.dto.RoleSearchDto;
import xyz.chen.member.services.RoleService;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping("/list")
    public BaseResponse<List<Role>> getRoles(@RequestBody RoleSearchDto roleSearchDto) {
        return BaseResponse.ok(roleService.searchRoles(roleSearchDto));
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/add")
    public BaseResponse<String> addRole (@RequestBody RoleDto role) {
        if (roleService.isRoleExist(role.roleId())) {
            return BaseResponse.fail(STATUS_CODE.CREATE_FAIL,"角色已存在");
        }
        roleService.saveRole(role);
        return BaseResponse.ok("创建角色成功");
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('admin')")
    public BaseResponse<String> editRole(@RequestBody RoleDto role) {
        if (!roleService.isRoleExist(role.roleId())) {
            return BaseResponse.fail(STATUS_CODE.UPDATE_FAIL,"当前角色不存在");
        }
        roleService.saveRole(role);
        return BaseResponse.ok("更新角色成功");
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/delete")
    public BaseResponse<String> deleteRole(@RequestBody List<RoleDto> roleDtos) {
        List<Long> roleIds = roleDtos.stream().map(RoleDto::roleId).filter(obj -> NumberUtil.isLong(String.valueOf(obj))).toList();
        roleService.removeRolesByIds(roleIds);
        return BaseResponse.ok("删除角色成功");
    }

}
