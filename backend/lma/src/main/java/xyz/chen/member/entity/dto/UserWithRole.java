package xyz.chen.member.entity.dto;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import xyz.chen.member.entity.Role;
import xyz.chen.member.entity.User;

import java.util.List;

@Data
public class UserWithRole {

    private Long id;
    private String username;

    private String nickName;

    private String oauth_uuid;

    private List<RoleDto> roles;

    public UserWithRole(User user, List<Role> roles) {
        BeanUtils.copyProperties(user, this);
        this.roles = roles.stream().map(role -> new RoleDto(role.getRoleName(), role.getRoleCode(), role.getId())).toList();
    }
}
