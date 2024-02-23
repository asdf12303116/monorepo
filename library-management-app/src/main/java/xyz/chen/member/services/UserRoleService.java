package xyz.chen.member.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.chen.member.entity.UserRole;
import xyz.chen.member.repository.UserRoleRepository;

import java.util.List;

@Service
public class UserRoleService extends ServiceImpl<UserRoleRepository, UserRole> {


    public void batchSave(List<UserRole> userRoles) {
        this.baseMapper.insertBatch(userRoles);
    }
}
