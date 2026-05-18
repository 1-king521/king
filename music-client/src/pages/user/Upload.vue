<template>
  <div class="upload">
    <el-upload
      drag
      :action="uploadUrl()"
      :headers="uploadHeaders"
      :show-file-list="false"
      :on-success="handleAvatarSuccess"
      :on-error="handleAvatarError"
      :before-upload="beforeAvatarUpload"
    >
      <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
      <div class="el-upload__text">将文件拖到此处或点击上传</div>
      <template #tip>
        <p class="el-upload__tip">
          只能上传 {{ uploadTypes.join("、") }} 文件, 且不超过10M
        </p>
      </template>
    </el-upload>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from "vue";
import { ElMessage } from "element-plus";
import type { UploadRawFile } from "element-plus";
import { UploadFilled } from "@element-plus/icons-vue";
import { useUserStore } from "@/store/user";
import { useConfigureStore } from "@/store/configure";
import { fetchUploadUrl } from "@/api/user";
import { elMessageTypeFromResponse } from "@/api/types";

const userStore = useUserStore();
const configureStore = useConfigureStore();

const uploadTypes = ref(["jpg", "jpeg", "png", "gif"]);
const userId = computed(() => userStore.userId);

/** el-upload 直连 action，不走 axios 拦截器，须显式带与 {@link LoginCheckFilter} 一致的 JWT */
const uploadHeaders = computed<Record<string, string>>(() => {
  const t = configureStore.token;
  return t ? { token: t } : {};
});

function uploadUrl() {
  return fetchUploadUrl(userId.value);
}

const maxAvatarMb = 10;

function beforeAvatarUpload(file: UploadRawFile) {
  if (!configureStore.token) {
    ElMessage.error("请先登录后再上传头像");
    return false;
  }
  if (userId.value === "" || userId.value === null || userId.value === undefined) {
    ElMessage.error("请先登录后再上传头像");
    return false;
  }

  const sizeMb = file.size / 1024 / 1024;
  const isExistFileType = uploadTypes.value.includes(file.type.replace(/^image\//, ""));

  if (sizeMb <= 0 || sizeMb > maxAvatarMb) {
    ElMessage.error(`图片大小范围是 0~${maxAvatarMb}MB!`);
    return false;
  }
  if (!isExistFileType) {
    ElMessage.error(`图片只支持 ${uploadTypes.value.join("、")} 格式!`);
    return false;
  }

  return true;
}

function handleAvatarError() {
  ElMessage.error("头像上传失败，请检查网络或稍后重试");
}

function handleAvatarSuccess(response: {
  message: string;
  success: boolean;
  type?: string;
  data?: { url?: string };
}) {
  ElMessage({
    message: response.message,
    type: elMessageTypeFromResponse(response),
  });

  if (response.success) {
    const avatarUrl = response && response.data ? response.data.url : "";
    if (avatarUrl) userStore.setUserPic(avatarUrl);
  }
}
</script>

<style scoped>
.upload {
  width: 100%;
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
