type jwtToken = {
  accessToken: string;
  expires: Date;
  refreshToken: string;
  username?: string;
  roles?: Array<string>;
}
type jwtPayload = {
  exp: number;
  iat: number;
  jti: string;
  roles: string;
  sub: number;
  userName: string;
};
type LoginResult = {
  success: boolean;
  message: string;
  body: string;
};
type LoginUser = {
  success?: true;
  username: string;
  roles: Array<string>;
  token: string;
  expires: Date;
};
type ssoInfo = {
  code: string;
  state: string;
  session_state: string;
};
