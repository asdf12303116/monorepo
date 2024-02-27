package xyz.chen.member.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String nickName;
    private String oauth_uuid;

    private List<RoleDto> roles;


}
