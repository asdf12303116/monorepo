package xyz.chen.member.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import xyz.chen.member.entity.OAuthRoleMap;

@Repository
public interface OAuthRoleRepository extends BaseMapper<OAuthRoleMap> {


}
