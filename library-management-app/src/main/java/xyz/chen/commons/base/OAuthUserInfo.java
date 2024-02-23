package xyz.chen.commons.base;

import java.util.List;

public record OAuthUserInfo(String username, String showName, String uuid, String email, List<String> groups) {
}
