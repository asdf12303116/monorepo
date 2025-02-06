package xyz.chen.member.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.chen.member.entity.OAuthRoleMap;
import xyz.chen.member.repository.OAuthRoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OAuthService extends ServiceImpl<OAuthRoleRepository, OAuthRoleMap> {
    public Optional<List<OAuthRoleMap>> queryRoles(List<String> groupIds) {
        List<OAuthRoleMap> oAuthRoleMaps = this.lambdaQuery().in(OAuthRoleMap::getGroupUUID, groupIds).list();
        return Optional.ofNullable(oAuthRoleMaps);
    }
}
