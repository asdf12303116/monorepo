package xyz.chen.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.chen.commons.base.BaseResponse;
import xyz.chen.commons.base.STATUS_CODE;
import xyz.chen.commons.base.UserInfo;
import xyz.chen.member.entity.dto.UserDto;
import xyz.chen.member.entity.dto.UserWithRole;
import xyz.chen.member.services.UserService;
import xyz.chen.member.utils.UserUtils;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public BaseResponse<UserWithRole> getUserInfo() {
        UserInfo userInfo = UserUtils.getUserInfo();
        UserWithRole user = userService.getUserById(userInfo.userId());
        return BaseResponse.ok(user);
    }

    @PostMapping("/add")
    public BaseResponse<String> addUser(@RequestBody UserDto userDto) {
        boolean isUsernameExists = userService.checkUsernameExists(userDto.getUsername());
        if (isUsernameExists) {
            return BaseResponse.fail(STATUS_CODE.CREATE_USER_FAIL, "用户名已存在");
        }
        userService.saveUser(userDto);
        return BaseResponse.ok("添加成功");
    }

    @PostMapping("/update")
    public BaseResponse<String> updateUser(@RequestBody UserDto userDto) {
        boolean isUserExists = userService.checkUserExists(userDto.getId());
        if (!isUserExists) {
            return BaseResponse.fail(STATUS_CODE.UPDATE_USER_FAIL, "用户不存在");
        }
        userService.updateUser(userDto);
        return BaseResponse.ok("更新成功");
    }



}
