import login from './login'
export const baseUrlApi = (url: string) => `/api${url}`;


const api = {
  ...login,
}
export default api
