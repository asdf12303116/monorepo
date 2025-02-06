package xyz.chen.member.entity.dto;

import xyz.chen.commons.base.PageInfo;

public record UserRoleSearchDto(Long id,Long roleId,Long  userId,String roleName, String roleCode,PageInfo pageInfo) {
}
