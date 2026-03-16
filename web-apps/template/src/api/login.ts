import http from '@/utils/http'


const ssoToken = (data: ssoInfo) => {
  return http.get<LoginResult>( "/login/oauth2Callback",data)
};

const ssoTokenPost = (data: ssoInfo) => {
  console.log(data)
  return http.post<LoginResult,ssoInfo>( "/login/oauth2CallbackPost",data)
};

const refresh = () => {
  return http.get<LoginResult>( "/refreshToken")
};

const logout = () => {
  return http.get<LoginResult>( "/logout")
};

export default {
  ssoToken,
  ssoTokenPost,
  refresh,
}
