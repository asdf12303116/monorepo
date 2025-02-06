package xyz.chen.member.entity.dto;

import xyz.chen.commons.base.PageInfo;

public record RoleSearchDto(Long roleId, String roleName, String roleCode, String roleDesc, PageInfo pageInfo) {
}
