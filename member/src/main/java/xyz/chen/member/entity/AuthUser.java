package xyz.chen.member.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import xyz.chen.commons.base.BaseEntity;
import xyz.chen.commons.base.UserInfo;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@TableName("t_user")
public class AuthUser extends BaseEntity implements UserDetails {
    private Long Id;
    private String username;

    private String nickName;

    private String password;

    private String roles;

    private String oauth_uuid;

    private boolean enabled = true;

    private boolean accountNonExpired = true;

    private boolean credentialsNonExpired = true;

    private boolean accountNonLocked = true;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Split the roles string into a list of roles
        List<String> roles = Arrays.asList(getRoles().split(","));

        // Return the list of granted authorities
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public AuthUser(UserInfo userInfo){
        this.setId(userInfo.userId());
        this.setUsername(userInfo.userName());
        this.setRoles(userInfo.roles());
    }

}
