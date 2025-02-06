package xyz.chen.member.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import xyz.chen.member.entity.User;

@Repository
public interface UserRepository extends BaseMapper<User> {
}
