import api from '@/api'
import sessionUtils from '@/utils/session'
import Cookies from "js-cookie";
import { getLoginUser, removeToken, sessionKey, setToken, TokenKey } from '@/utils/auth'
export const callback_uri = '/v1/callback'


export const checkSso = (params : ssoInfo) => {
  const must = ["code", "state", "session_state"];
  const mustLength = must.length;
  if (Object.keys(params).length !== mustLength) return;

  // url 参数满足 must 里的全部值，才判定为单点登录，避免非单点登录时刷新页面无限循环
  let sso = [];
  let start = 0;

  while (start < mustLength) {
    if (Object.keys(params).includes(must[start]) && sso.length <= mustLength) {
      sso.push(must[start]);
    } else {
      sso = [];
    }
    start++;
  }
  if (sso.length === mustLength) {
    // 判定为单点登录

    // 清空本地旧信息
    removeToken();

    api.ssoToken(params).then(data => {

      if (data.success) {
        const user = getLoginUser(data.body);
        setToken(user);
      }

    }).finally(()=> {
      const newUrl = `${location.origin}`;
      window.location.replace(newUrl);
    })
  } else {
    return;
  }
}
