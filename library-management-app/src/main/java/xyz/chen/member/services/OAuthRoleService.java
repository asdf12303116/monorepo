package xyz.chen.member.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.chen.member.entity.OAuthRoleMap;
import xyz.chen.member.entity.Role;
import xyz.chen.member.repository.OAuthRoleRepository;
import xyz.chen.member.repository.RoleRepository;

import java.util.List;

@Component
public class OAuthRoleService extends ServiceImpl<OAuthRoleRepository, OAuthRoleMap> {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getRolesByGroups(List<String> groups) {
        List<Long> roleIds = this.lambdaQuery().in(OAuthRoleMap::getGroupUUID, groups)
                .select(OAuthRoleMap::getRoleId).list().stream().map(OAuthRoleMap::getRoleId).toList();


        return roleRepository.selectBatchIds(roleIds);

    }


}
