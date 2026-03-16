<script setup lang="ts">
import api from '@/api'
import { getLoginUser, setToken } from '@/utils/auth'

const ssoLogin = async () => {
  window.location.href = "/api/oauth2/authorization/azure-dev";
};
const ssoLogout = async () => {
  const base_url = "https://login.microsoftonline.com/common/oauth2/v2.0/logout";
  const redirect_url = "http://localhost:5173/"

  window.location.href = `${base_url}?post_logout_redirect_uri=${redirect_url}`;
};
const flushTest = async () => {
  api.refresh()
    .then(data => {
      const user = getLoginUser(data.body);
      setToken(user);
    })
}
</script>
<template>
  <div class="about">
    <h1>This is an about page</h1>
    <el-button @click="ssoLogin">SSO-Login</el-button>
    <el-button @click="flushTest">Flush</el-button>
    <el-button @click="ssoLogout">SSO-Logout</el-button>
  </div>
</template>

<style>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
  }
}
</style>
