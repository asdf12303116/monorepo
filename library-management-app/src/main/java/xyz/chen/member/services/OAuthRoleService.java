package xyz.chen.member.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.chen.commons.config.ConfigData;
import xyz.chen.member.entity.OAuthRoleMap;
import xyz.chen.member.entity.Role;
import xyz.chen.member.repository.OAuthRoleRepository;
import xyz.chen.member.repository.RoleRepository;

import java.util.List;

@Service
public class OAuthRoleService extends ServiceImpl<OAuthRoleRepository, OAuthRoleMap> {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ConfigData configData;

    public List<Role> getRolesByGroups(List<String> groups) {
        List<Long> roleIds = this.lambdaQuery()
                .select(OAuthRoleMap::getRoleId)
                .in(OAuthRoleMap::getGroupUUID, groups)
                .list().stream().map(OAuthRoleMap::getRoleId).toList();
        if (roleIds.isEmpty()) {
            return roleRepository.selectBatchIds(List.of(configData.getDefaultOauthRole()));
        }

        return roleRepository.selectBatchIds(roleIds);

    }

    public List<Long> getGroupsRoleIds() {
        return lambdaQuery()
                .select(OAuthRoleMap::getRoleId)
                .list().stream().map(OAuthRoleMap::getRoleId).toList();
    }


}
