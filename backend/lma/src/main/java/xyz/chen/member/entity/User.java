package xyz.chen.member.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.chen.commons.base.BaseEntity;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_user")
public class User extends BaseEntity {
    private String username;

    private String nickName;

    private String password;

    private String oauth_uuid;

}
