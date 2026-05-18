<template>
  <div class="header">
    <div class="collapse-btn" @click="collapseChage">
      <el-icon v-if="!collapse"><expand /></el-icon>
      <el-icon v-else><fold /></el-icon>
    </div>
    <div class="logo">{{ nusicName }}</div>
    <div class="header-right">
      <div class="header-user-con">
        <div class="user-avatar">
          <el-upload
            class="header-avatar-upload"
            :action="adminAvatarUploadAction"
            :headers="getUploadHeaders()"
            :with-credentials="true"
            name="file"
            :show-file-list="false"
            :on-success="handleAdminAvatarSuccess"
            :before-upload="beforeImgUpload"
          >
            <img :src="attachImageUrl(userPic)" alt="" />
          </el-upload>
        </div>
        <el-dropdown class="user-name" trigger="click" @command="handleCommand">
          <span class="el-dropdown-link">
            {{ username }}
            <el-icon class="dropdown-caret"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="loginout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, computed, ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useAppStore } from "@/stores/app";
import mixin from "@/mixins/mixin";
import { ArrowDown, Expand, Fold } from "@element-plus/icons-vue";
import emitter from "@/utils/emitter";
import { attachImageUrl, getAdminAvatarUploadUrl } from "@/api/url";
import { ADMIN_TOKEN_KEY, MANAGE_ADMIN_AVATAR_KEY, getUploadHeaders } from "@/utils/request";
import { RouterName, MUSICNAME } from "@/enums";

export default defineComponent({
  components: {
    ArrowDown,
    Expand,
    Fold,
  },
  setup() {
    const { routerManager, beforeImgUpload } = mixin();
    const appStore = useAppStore();

    const collapse = ref(true);
    const username = ref("admin");
    const userPic = computed(() => appStore.userPic);
    const nusicName = ref(MUSICNAME);

    const adminAvatarUploadAction = getAdminAvatarUploadUrl();

    function handleAdminAvatarSuccess(res: ResponseBody) {
      const raw = res?.data as { url?: string } | undefined;
      const url = raw && typeof raw.url === "string" ? raw.url.trim() : "";
      if (res?.success && url) {
        appStore.setUserPic(url);
        try {
          localStorage.setItem(MANAGE_ADMIN_AVATAR_KEY, url);
        } catch {
          /* private mode / quota */
        }
      }
      ElMessage({
        message: res?.message ?? (res?.success ? "上传成功" : "上传失败"),
        type: res?.success ? "success" : "warning",
      });
    }

    onMounted(() => {
      if (document.body.clientWidth < 1500) {
        collapseChage();
      }
    });

    // 侧边栏折叠
    function collapseChage() {
      collapse.value = !collapse.value;
      emitter.emit("collapse", collapse.value);
    }
    // 用户名下拉菜单选择事件
    function handleCommand(command) {
      if (command === "loginout") {
        sessionStorage.removeItem(ADMIN_TOKEN_KEY);
        try {
          localStorage.removeItem(MANAGE_ADMIN_AVATAR_KEY);
        } catch {
          /* ignore */
        }
        appStore.setUserPic("/img/avatorImages/user.jpg");
        routerManager(RouterName.SignIn, { path: RouterName.SignIn });
      }
    }
    return {
      nusicName,
      username,
      userPic,
      collapse,
      collapseChage,
      handleCommand,
      attachImageUrl,
      getUploadHeaders,
      beforeImgUpload,
      adminAvatarUploadAction,
      handleAdminAvatarSuccess,
    };
  },
});
</script>
<style scoped>
.header {
  position: relative;
  z-index: 100;
  width: 100%;
  height: 60px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  font-size: 20px;
  color: #2c3e50;
  background: #ffff;
  box-shadow: 0px 0px 8px 2px rgba(0, 0, 0, 0.3);
}

.collapse-btn {
  display: flex;
  padding: 0 21px;
  cursor: pointer;
}

.header .logo {
  width: 250px;
  font-weight: bold;
}

.header-right {
  position: absolute;
  right: 0;
  padding-right: 30px;
}

.header-user-con {
  display: flex;
  align-items: center;
}

.user-name {
  margin-left: 10px;
}

.user-avatar img {
  display: block;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
}

.header-avatar-upload {
  display: inline-block;
}

.header-avatar-upload :deep(.el-upload) {
  border: none;
  cursor: pointer;
}

.el-dropdown-link {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.dropdown-caret {
  font-size: 12px;
}

.el-dropdown-menu__item {
  text-align: center;
}
</style>
