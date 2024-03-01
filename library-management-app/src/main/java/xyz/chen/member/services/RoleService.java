package xyz.chen.member.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.chen.member.entity.Role;
import xyz.chen.member.repository.RoleRepository;

import java.util.List;

@Service
public class RoleService extends ServiceImpl<RoleRepository, Role> {


    public List<Role> getRolesByIds(List<Long> roleIds) {
        return getBaseMapper().selectBatchIds(roleIds);
    }

    @Transactional
    public void removeRolesByIds(List<Long> roleIds) {
        removeByIds(roleIds);
    }

    public Page<Role> getAllRoles() {
        Page<Role> Page = new Page<>(1, 10);
        return baseMapper.selectPage(Page, lambdaQuery().getWrapper());
    }



}
