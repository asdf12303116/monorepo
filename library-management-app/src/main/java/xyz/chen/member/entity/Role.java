package xyz.chen.member.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xyz.chen.commons.base.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@TableName("t_role")
@Data
@NoArgsConstructor
public class Role extends BaseEntity {
    // @Comment("角色名称")
    private String roleName;

    //@Comment("角色编码")
    private String roleCode;


    // @Comment("角色描述")
    private String roleDesc;

    public Role(String roleName, String roleCode, Long roleId) {
        this.roleName = roleName;
        this.roleCode = roleCode;
        this.setId(roleId);
    }

}
