package xyz.chen.member.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import xyz.chen.member.entity.UserRole;

import java.util.List;

@Repository
public interface UserRoleRepository extends BaseMapper<UserRole> {

    void insertBatch(List<UserRole> userRoles);
}
