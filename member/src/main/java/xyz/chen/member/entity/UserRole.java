package xyz.chen.member.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.chen.commons.base.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@TableName("t_user_role")
@Data
public class UserRole extends BaseEntity {
    //@Comment("用户ID")
    private Long userId;
    //@Comment("角色ID")
    private Long roleId;
}
