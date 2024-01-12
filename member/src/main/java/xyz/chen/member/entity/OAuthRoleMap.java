package xyz.chen.member.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.chen.commons.base.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@TableName("t_oauth_role_map")
@Data
public class OAuthRoleMap extends BaseEntity {

    @TableField("group_uuid")
    private String groupUUID;
    private String groupName;
    private String groupDesc;
    private Long roleId;

}
