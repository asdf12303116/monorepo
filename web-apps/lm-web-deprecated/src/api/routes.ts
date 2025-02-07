// import { http } from "@/utils/http";
//
// type Result = {
//   success: boolean;
//   data: Array<any>;
// };

export const getAsyncRoutes = () => {
  // return http.request<Result>("get", "/getAsyncRoutes");
  return new Promise(resolve => {
    setTimeout(
      () =>
        resolve({
          data: []
        }),
      200
    );
  });
};

export default {
  getAsyncRoutes
};
