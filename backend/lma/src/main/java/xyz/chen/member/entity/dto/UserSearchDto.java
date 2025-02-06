package xyz.chen.member.entity.dto;

import xyz.chen.commons.base.PageInfo;

public record UserSearchDto(String username, String nickName, Long id, String oauth_uuid, PageInfo pageInfo) {
}
