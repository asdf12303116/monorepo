package xyz.chen.member.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import xyz.chen.member.entity.Role;

@Repository
public interface RoleRepository extends BaseMapper<Role> {
}
