import router from "./routes";
import user from "./user";
import { baseUrlApi } from "@/api/base";

export const api = {
  baseUrlApi,
  ...router,
  ...user
};
