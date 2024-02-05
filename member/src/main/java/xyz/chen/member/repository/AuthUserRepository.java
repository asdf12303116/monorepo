package xyz.chen.member.repository;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import xyz.chen.member.entity.AuthUser;

@Repository
public interface AuthUserRepository extends BaseMapper<AuthUser> {

    @Select("""
            SELECT t_user.id,
            t_user.deleted,
            t_user.expired,
            t_user.locked,
            t_user.credentials_non_expired,
            t_user.enabled,
            t_user.nick_name,
            t_user.password,
            t_user.username,
            t_user.oauth_uuid ,
            string_agg(t_user_role.role_code, ',') AS roles
            FROM t_user
                     JOIN t_user_role ON t_user.id = t_user_role.user_id
            where
            t_user.deleted = false and
            t_user.username = #{username}
            GROUP BY t_user.id
            """)
    AuthUser getAuthUserWithRoles(String username);

    @Select("""
            SELECT t_user.id,
            t_user.deleted,
            t_user.expired,
            t_user.locked,
            t_user.credentials_non_expired,
            t_user.enabled,
            t_user.nick_name,
            t_user.password,
            t_user.username,
            t_user.oauth_uuid ,
            string_agg(t_user_role.role_code, ',') AS roles
            FROM t_user
                     JOIN t_user_role ON t_user.id = t_user_role.user_id
            where
            t_user.deleted = false and
            t_user.oauth_uuid = #{uuid}
            GROUP BY t_user.id
            """)
    AuthUser getAuthUserByUUIDWithRoles(String uuid);


}
