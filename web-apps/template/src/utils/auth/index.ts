import Cookies from 'js-cookie'
import sessionUtils from '@/utils/session'
export const sessionKey = "user-info";
export const TokenKey = "authorized-token";

export function getToken(): LoginUser | null {
  const rawData = Cookies.get(TokenKey)
    ? JSON.parse(<string>Cookies.get(TokenKey))
    : sessionUtils.getItem(sessionKey);
  if (!rawData) return null;
  if (rawData.expires instanceof Date) {
    return rawData;
  } else {
    rawData.expires = new Date(rawData.expires);
    return rawData;
  }
}

export function removeToken() {
  Cookies.remove(TokenKey);
  sessionUtils.delItem(sessionKey);
}

export function setToken(data: LoginUser) {
  // let expires = 0;
  const { expires, token } = data;
  const cookieString = JSON.stringify({ token, expires });

  expires.getTime() > Date.now()
    ? Cookies.set(TokenKey, cookieString, {
      expires: (expires.getTime() - Date.now()) / 7200000
    })
    : Cookies.set(TokenKey, cookieString);

  function setSessionKey(username: string, roles: Array<string>) {
    sessionUtils.setItem(sessionKey, {
      token,
      expires,
      username,
      roles
    });
  }

  if (data.username && data.roles) {
    const { username, roles } = data;
    setSessionKey(username, roles);
  } else {
    const username =
      sessionUtils.getItem<jwtToken>(sessionKey)?.username ?? "";
    const roles =
      sessionUtils.getItem<jwtToken>(sessionKey)?.roles ?? [];
    setSessionKey(username, roles);
  }

}

export const getLoginUser = (token: string): LoginUser => {
  const jwtParts = token.split(".");
  const payload: jwtPayload = JSON.parse(atob(jwtParts[1]));
  return {
    success: true,
    username: payload.userName,
    roles: payload.roles.split(","),
    token: token,
    expires: new Date(payload.exp * 1000)
  };
};
