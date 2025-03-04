import http from '@/utils/http'


const ssoToken = (data: ssoInfo) => {
  return http.get<LoginResult>( "/login/oauth2Callback",data)
};

const refresh = () => {
  return http.get<LoginResult>( "/refreshToken")
};

export default {
  ssoToken,
  refresh,
}
