<template>
  <AuthLayout title="登录" pageClass="sign-in-page">
    <el-tabs v-model="activeTab" class="sign-in-tabs" stretch @tab-change="onTabChange">
      <el-tab-pane label="账号登录" name="account" />
      <el-tab-pane label="邮箱登录" name="email" />
      <el-tab-pane label="手机验证码" name="phone" />
    </el-tabs>
    <el-form ref="signInForm" status-icon :model="registerForm" :rules="signInRules">
      <el-form-item v-show="activeTab === 'account'" prop="username">
        <el-input placeholder="用户名" v-model="registerForm.username" />
      </el-form-item>
      <el-form-item v-show="activeTab === 'email'" prop="email">
        <el-input placeholder="邮箱" v-model="registerForm.email" />
      </el-form-item>
      <el-form-item v-show="activeTab === 'phone'" prop="phone">
        <el-input placeholder="手机号" v-model="registerForm.phone" maxlength="11" clearable />
      </el-form-item>
      <el-form-item v-show="activeTab === 'phone'" prop="smsCode">
        <div class="sms-code-row">
          <el-input
            placeholder="短信验证码"
            v-model="registerForm.smsCode"
            maxlength="6"
            clearable
            @keyup.enter="handleLoginIn"
          />
          <el-button
            class="sms-code-row__btn"
            :disabled="smsCountdown > 0 || sendingSms"
            @click="handleSendSmsCode"
          >
            {{ smsCountdown > 0 ? `${smsCountdown}s 后重试` : "获取验证码" }}
          </el-button>
        </div>
      </el-form-item>
      <el-form-item v-show="activeTab !== 'phone'" prop="password">
        <el-input type="password" placeholder="密码" v-model="registerForm.password" @keyup.enter="handleLoginIn" />
      </el-form-item>
      <el-form-item class="sign-btn--submit">
        <el-button type="primary" class="sign-submit-btn" @click="handleLoginIn">登录</el-button>
      </el-form-item>
      <div class="sign-switch sign-switch--footer">
        <div class="sign-switch__left">
          <span class="sign-switch__text">还没有账号？</span>
          <button type="button" class="sign-switch__link sign-switch__link--with-icon" @click="handleSignUp">
            立即注册
          </button>
        </div>
        <button type="button" class="sign-switch__link sign-switch__link--with-icon" @click="handleForgotPassword">
          <KeyRound class="sign-switch__icon" aria-hidden="true" />
          忘记密码
        </button>
      </div>
    </el-form>
  </AuthLayout>
</template>

<script lang="ts" setup>
import { computed, onUnmounted, reactive, ref } from "vue";
import { KeyRound } from "lucide-vue-next";
import { ElMessage } from "element-plus";
import AuthLayout from "@/components/auth/AuthLayout.vue";
import { useConfigureStore } from "@/store/configure";
import { useUserStore } from "@/store/user";
import { fetchSignIn, fetchSendLoginSmsCode, fetchSmsSignIn } from "@/api/auth";
import { fetchCurrentUserProfile } from "@/api/user";
import { elMessageTypeFromResponse } from "@/api/types";
import { NavLabel, RouterName } from "@/enums";
import { validatePassword } from "@/utils/validate";
import { useAppActions } from "@/composables/useAppActions";
import { useNavigation } from "@/composables/useNavigation";
import { useRoute } from "vue-router";

type LoginTab = "account" | "email" | "phone";

const route = useRoute();
const { routerManager } = useAppActions();
const { changeActiveNav } = useNavigation();
const signInForm = ref(null);
const userStore = useUserStore();
const configureStore = useConfigureStore();

const activeTab = ref<LoginTab>("account");
const smsCountdown = ref(0);
const sendingSms = ref(false);
let smsTimer: ReturnType<typeof setInterval> | null = null;

const registerForm = reactive({
  username: "",
  email: "",
  password: "",
  phone: "",
  smsCode: "",
});

/** 与后端 sendCode 一致：11 位且以 1 开头（不要求真实号段 3–9，便于测试数据） */
const CN_PHONE_PATTERN = /^1\d{10}$/;

const validateName = (_rule: any, value: string, callback: (e?: Error) => void) => {
  if (!value || !value.trim()) {
    callback(new Error("用户名不能为空"));
  } else {
    callback();
  }
};

const validatePhone = (_rule: any, value: string, callback: (e?: Error) => void) => {
  const v = value?.trim() ?? "";
  if (!v) {
    callback(new Error("请输入手机号"));
  } else if (!CN_PHONE_PATTERN.test(v)) {
    callback(new Error("请输入正确的手机号"));
  } else {
    callback();
  }
};

const validateSmsCode = (_rule: any, value: string, callback: (e?: Error) => void) => {
  const v = value?.trim() ?? "";
  if (!v) {
    callback(new Error("请输入验证码"));
  } else if (!/^\d{6}$/.test(v)) {
    callback(new Error("验证码为 6 位数字"));
  } else {
    callback();
  }
};

const signInRules = computed(() => ({
  username:
    activeTab.value === "account" ? [{ validator: validateName, trigger: "blur" }] : [],
  email:
    activeTab.value === "email"
      ? [
          { required: true, message: "请输入邮箱", trigger: "blur" },
          {
            type: "email",
            message: "请输入正确的邮箱地址",
            trigger: ["blur", "change"],
          },
        ]
      : [],
  phone: activeTab.value === "phone" ? [{ validator: validatePhone, trigger: "blur" }] : [],
  smsCode: activeTab.value === "phone" ? [{ validator: validateSmsCode, trigger: "blur" }] : [],
  password:
    activeTab.value === "account" || activeTab.value === "email"
      ? [{ validator: validatePassword, trigger: "blur" }]
      : [],
}));

function clearSmsTimer() {
  if (smsTimer !== null) {
    clearInterval(smsTimer);
    smsTimer = null;
  }
}

function startSmsCountdown(seconds: number) {
  clearSmsTimer();
  smsCountdown.value = seconds;
  smsTimer = setInterval(() => {
    smsCountdown.value -= 1;
    if (smsCountdown.value <= 0) {
      clearSmsTimer();
      smsCountdown.value = 0;
    }
  }, 1000);
}

onUnmounted(() => {
  clearSmsTimer();
});

function onTabChange() {
  registerForm.username = "";
  registerForm.email = "";
  registerForm.password = "";
  registerForm.phone = "";
  registerForm.smsCode = "";
  const form = signInForm.value as { clearValidate: () => void } | null;
  if (form) {
    form.clearValidate();
  }
}

function loginAccount(): string {
  if (activeTab.value === "phone") {
    return registerForm.phone.trim();
  }
  return activeTab.value === "account" ? registerForm.username.trim() : registerForm.email.trim();
}

async function completeSessionAfterJwt(jwt: string) {
  configureStore.setToken(jwt);
  const res = await fetchCurrentUserProfile();
  if (!res.success || res.data == null) {
    configureStore.setToken("");
    ElMessage({
      message: res.message || "已登录但无法拉取用户信息，请稍后重试",
      type: "warning",
    });
    return;
  }
  const user = res.data;
  userStore.setUserId(user.id);
  userStore.setUsername(user.username);
  userStore.setUserPic(user.avatar ?? "");
  changeActiveNav(NavLabel.Home);
  const redirect = typeof route.query.redirect === "string" ? route.query.redirect : "";
  if (redirect && redirect.startsWith("/") && !redirect.startsWith("//")) {
    routerManager(redirect, { path: redirect });
  } else {
    routerManager(RouterName.Home, { path: RouterName.Home });
  }
}

async function handleSendSmsCode() {
  if (!signInForm.value || activeTab.value !== "phone") return;
  try {
    await (signInForm.value as any).validateField("phone");
  } catch {
    return;
  }
  sendingSms.value = true;
  try {
    const res = await fetchSendLoginSmsCode(registerForm.phone.trim());
    ElMessage({
      message: res.message,
      type: elMessageTypeFromResponse(res),
    });
    if (res.success) {
      startSmsCountdown(60);
    }
  } finally {
    sendingSms.value = false;
  }
}

async function handleLoginIn() {
  if (!signInForm.value) return;
  try {
    await (signInForm.value as any).validate();
  } catch {
    return;
  }

  let result;
  if (activeTab.value === "phone") {
    result = await fetchSmsSignIn({
      phone: registerForm.phone.trim(),
      code: registerForm.smsCode.trim(),
    });
  } else {
    result = await fetchSignIn({
      username: loginAccount(),
      password: registerForm.password,
    });
  }

  ElMessage({
    message: result.message,
    type: elMessageTypeFromResponse(result),
  });

  if (result.success) {
    const jwt = typeof result.data === "string" ? result.data : "";
    if (!jwt) {
      ElMessage({
        message: "登录返回数据异常，请重试",
        type: "warning",
      });
      return;
    }
    await completeSessionAfterJwt(jwt);
  }
}

function handleSignUp() {
  routerManager(RouterName.SignUp, { path: RouterName.SignUp });
}

function handleForgotPassword() {
  const query: Record<string, string> = {};
  if (activeTab.value === "email" && registerForm.email.trim()) {
    query.email = registerForm.email.trim();
  }
  routerManager(RouterName.ForgotPassword, {
    path: RouterName.ForgotPassword,
    query,
  });
}
</script>

<style lang="scss" scoped>
.sms-code-row {
  display: flex;
  width: 100%;
  gap: 10px;
  align-items: stretch;
}

.sms-code-row:deep(.el-input) {
  flex: 1;
  min-width: 0;
}

.sms-code-row__btn {
  flex-shrink: 0;
  white-space: nowrap;
}

.sign-in-tabs {
  margin-bottom: 16px;
}

.sign-in-tabs:deep(.el-tabs__header) {
  margin-bottom: 0;
}

.sign-in-tabs:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}

.sign-in-tabs:deep(.el-tabs__item) {
  flex: 1;
  padding: 0 12px;
}

.sign-in-tabs:deep(.el-tabs__nav) {
  width: 100%;
}

.sign-in-tabs:deep(.el-tabs__active-bar) {
  transition: transform 0.2s ease;
}

.sign-btn--submit {
  margin-bottom: 10px;
}

.sign-submit-btn {
  width: 100%;
}

.sign-btn--submit:deep(.el-form-item__content) {
  display: flex;
  width: 100%;
}

.sign-switch {
  display: flex;
  align-items: center;
  margin-top: 4px;
  font-size: 14px;
  line-height: 20px;
}

.sign-switch--footer {
  justify-content: space-between;
  width: 100%;
  gap: 12px;
  flex-wrap: wrap;
}

.sign-switch__left {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
}

.sign-switch__text {
  color: #909399;
}

.sign-switch__link {
  padding: 0;
  border: 0;
  background: transparent;
  color: #409eff;
  font-size: 14px;
  line-height: 20px;
  cursor: pointer;
}

.sign-switch__link--with-icon {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.sign-switch__icon {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
}

.sign-switch__link:hover {
  color: #66b1ff;
}
</style>
