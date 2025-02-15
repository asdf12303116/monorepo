import { http } from "@/utils/http";
import { baseUrlApi } from "@/api/base";
import { ssoInfo } from "@/utils/sso";

export type UserResult = {
  success: boolean;
  data: {
    /** 用户名 */
    username: string;
    /** 当前登陆用户的角色 */
    roles: Array<string>;
    /** `token` */
    token: string;
    /** `accessToken`的过期时间（格式'xxxx/xx/xx xx:xx:xx'） */
    expires: Date;
  };
};
export type LoginResult = {
  success: boolean;
  message: string;
  body: string;
};

export type LoginUser = {
  success?: true;
  /** 用户名 */
  username: string;
  /** 当前登陆用户的角色 */
  roles: Array<string>;
  /** `token` */
  token: string;
  /** 过期时间（格式'xxxx/xx/xx xx:xx:xx'） */
  expires: Date;
};
export type RefreshTokenResult = {
  success: boolean;
  data: {
    /** `token` */
    accessToken: string;
    /** 用于调用刷新`accessToken`的接口时所需的`token` */
    refreshToken: string;
    /** `accessToken`的过期时间（格式'xxxx/xx/xx xx:xx:xx'） */
    expires: Date;
  };
};

/** 登录 */
export const getLogin = (data?: object) => {
  return http.request<UserResult>("post", "/login", { data });
};

/** 刷新token */
export const refreshTokenApi = (data?: object) => {
  return http.request<RefreshTokenResult>("post", "/refreshToken", { data });
};

export const login = (data?: object) => {
  return http.request<LoginResult>("post", baseUrlApi("/login"), { data });
};
export const refreshToken = (data?: object) => {
  return http.request<LoginResult>("get", baseUrlApi("/refreshToken"), {
    data
  });
};

export const ssoToken = (data: ssoInfo) => {
  return http.request<LoginResult>("get", baseUrlApi("/login/oauth2Callback"), {
    params: data
  });
};
export default {
  login,
  refreshToken,
  ssoToken
};
