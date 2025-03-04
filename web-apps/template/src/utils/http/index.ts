// src/api/request.ts
import axios, { type AxiosError, type AxiosInstance, type AxiosRequestConfig } from 'axios'
import { getLoginUser, getToken, setToken } from '@/utils/auth'
import Progress from '@/utils/progress'
const refreshTokenTime = 30 * 60  * 1000;


import api from '@/api'
import sessionUtils from '@/utils/session'


const instance: AxiosInstance = axios.create({
  baseURL: '/api', // 根据实际情况配置
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
instance.interceptors.request.use(
   async (config) => {
    Progress.start();
    const whiteList = ["/login", "/login/oauth2Callback"];
    const url = config.url ? config.url : ''
    return whiteList.some(v => url.indexOf(v) > -1)
      ? config
      : new Promise(resolve => {
        // 添加认证 token
        const user = getToken();
        if (user) {
          const now = new Date().getTime();
          const expired = user.expires.getTime() - now <= 0;
          const needRefresh =
            user.expires.getTime() - now >= 0 &&
            user.expires.getTime() - now <= refreshTokenTime;
          if (needRefresh) {
            console.log('needRefresh',needRefresh)
            const isRefreshing = sessionStorage.getItem('isRefreshing')
            if (isRefreshing !== '1') {
              sessionStorage.setItem('isRefreshing', '1');
              // token过期刷新
              api.refresh()
                .then(data => {
                  const user = getLoginUser(data.body);
                  setToken(user);
                  config.headers["Authorization"] = `Bearer ${user.token}`;
                })
                .finally(() => {
                  sessionUtils.delItem('isRefreshing');
                });
            }
            resolve(config);
          } else {
            config.headers["Authorization"] = `Bearer ${user.token}`;
            resolve(config);
          }
        } else {
          resolve(config);
        }
      })
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
instance.interceptors.response.use(
  (response) => {
    Progress.done();
    // 处理标准响应结构
    const res = response.data as ApiResponse;
    if (res.code !== 20000) {
      // 业务逻辑错误处理
      const error = new Error(res.message || 'Unknown error');
      return Promise.reject(error);
    }
    return response.data; // 返回实际有效数据
  },
  (error: AxiosError) => {
    Progress.done();
    // 处理 HTTP 错误状态码
    let errorMessage = 'Network Error';
    if (error.response) {
      switch (error.response.status) {
        case 401:
          errorMessage = 'Unauthorized';
          break;
        case 403:
          errorMessage = 'Forbidden';
          break;
        case 404:
          errorMessage = 'Resource not found';
          break;
        default:
          errorMessage = `Error ${error.response.status}`;
      }
    }
    return Promise.reject(new Error(errorMessage));
  }
);

// 继续在 src/api/request.ts 中添加

// 核心请求函数
async function request<T>(config: AxiosRequestConfig): Promise<T> {
  return instance.request<ApiResponse<T>>(config)
    .then(response => response as T)
    .catch((error) => {
      // 统一错误提示（可选）
      console.error('Request Error:', error.message);
      throw error;
    });
}

// 封装常用方法
export function get<T>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
  return request<T>({ method: 'GET', url, params, ...config });
}

export function post<T, U>(url: string, data?: U, config?: AxiosRequestConfig): Promise<T> {
  return request<T>({ method: 'POST', url, data, ...config });
}

export function put<T, U>(url: string, data?: U, config?: AxiosRequestConfig): Promise<T> {
  return request<T>({ method: 'PUT', url, data, ...config });
}

export function del<T>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
  return request<T>({ method: 'DELETE', url, params, ...config });
}

const http = {
  get,post,put,del
}
export default http
