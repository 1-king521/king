<template>
  <div id="app">
    <router-view v-slot="{ Component }">
      <component v-if="isLoginRoute" :is="Component" />
      <wyk-layout v-else-if="Component">
        <component :is="Component" />
      </wyk-layout>
    </router-view>
  </div>
</template>

<script lang="ts" setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import WykLayout from "@/components/layouts/WykLayout.vue";
import { useAppStore } from "@/stores/app";
import { MANAGE_ADMIN_AVATAR_KEY } from "@/utils/request";

const route = useRoute();
const isLoginRoute = computed(() => route.path === "/");


const appStore = useAppStore();

const raw = sessionStorage.getItem("dataStore");
if (raw) {
  try {
    appStore.$patch(JSON.parse(raw));
  } catch {
    /* ignore invalid session payload */
  }
}
/** 管理员本地上传的头像路径（persist），覆盖 dataStore 里默认占位图 */
const savedAvatar = typeof localStorage !== "undefined" ? localStorage.getItem(MANAGE_ADMIN_AVATAR_KEY) : null;
if (savedAvatar && typeof savedAvatar === "string" && savedAvatar.trim()) {
  appStore.setUserPic(savedAvatar.trim());
}

window.addEventListener("beforeunload", () => {
  sessionStorage.setItem("dataStore", JSON.stringify(appStore.$state));
});
</script>
