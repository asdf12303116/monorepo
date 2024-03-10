package xyz.chen.member.services;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.chen.member.entity.Role;
import xyz.chen.member.entity.dto.RoleDto;
import xyz.chen.member.entity.dto.RoleSearchDto;
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

    public Page<Role> searchRoles(RoleSearchDto roleSearchDto) {
        Page<Role> Page = new Page<>(roleSearchDto.pageInfo().getPageNum(), roleSearchDto.pageInfo().getPageSize());
        return baseMapper.selectPage(Page, lambdaQuery()
                        .eq(StrUtil.isNotEmpty(String.valueOf(roleSearchDto.roleId())),Role::getId,roleSearchDto.roleId())
                .likeRight(StrUtil.isNotEmpty(roleSearchDto.roleName()),Role::getRoleName,roleSearchDto.roleName())
                .likeRight(StrUtil.isNotEmpty(roleSearchDto.roleCode()),Role::getRoleCode,roleSearchDto.roleCode())
                .getWrapper()
        );

    };

    public Boolean isRoleExist(Long roleId) {
        return lambdaQuery().eq(Role::getId, roleId).exists();
    }

    @Transactional
    public void saveRole(RoleDto roleDto) {
        Role role = new Role();
        BeanUtils.copyProperties(roleDto, role);
        saveOrUpdate(role);
    }


}
