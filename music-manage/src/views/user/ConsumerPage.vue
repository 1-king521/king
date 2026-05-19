<template>
  <div class="consumer-page">
    <div class="container">
      <div class="handle-box">
        <el-button @click="deleteAll">批量删除</el-button>
        <el-input v-model="searchWord" placeholder="筛选用户"></el-input>
      </div>

      <el-table height="550px" border size="small" :data="data" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="40" align="center"></el-table-column>
        <el-table-column label="ID" prop="id" width="50" align="center"></el-table-column>
        <el-table-column label="用户头像" width="130" align="center">
          <template v-slot="scope">
            <div class="consumer-avatar-col">
              <img :src="attachImageUrl(scope.row.avatar)" class="consumer-avatar-img" alt="" />
              <el-upload
                :action="userAvatarUploadUrl(scope.row.id)"
                :headers="getUploadHeaders()"
                :with-credentials="true"
                name="file"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeImgUpload"
              >
                <el-button type="primary" size="small" native-type="button">更新头像</el-button>
              </el-upload>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="用户名" prop="username" width="80" align="center"></el-table-column>
        <el-table-column label="性别" width="50" align="center">
          <template v-slot="scope">
            <div>{{ changeSex(scope.row.sex) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="手机号码" prop="phoneNum" width="120" align="center"></el-table-column>
        <el-table-column label="邮箱" prop="email" width="120" align="center"></el-table-column>
        <el-table-column label="生日" width="120" align="center">
          <template v-slot="scope">
            <div>{{ getBirth(scope.row.birth) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="签名" prop="introduction"></el-table-column>
        <el-table-column label="地区" prop="location" width="70" align="center"></el-table-column>
        <el-table-column label="操作" width="90" align="center">
          <template v-slot="scope">
            <el-button type="danger" @click="deleteRow(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="pagination" background layout="total, prev, pager, next" :current-page="currentPage"
        :page-size="pageSize" :total="tableData.length" @current-change="handleCurrentChange">
      </el-pagination>
    </div>

    <!-- 删除提示框 -->
    <wyk-del-dialog :delVisible="delVisible" @confirm="confirm" @cancelRow="delVisible = $event"></wyk-del-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { ElMessage } from "element-plus";
import { attachImageUrl, attachApiUrl, getAllUser, deleteUser } from "@/api";
import WykDelDialog from "@/components/dialog/WykDelDialog.vue";
import { getBirth } from "@/utils";
import { getUploadHeaders } from "@/utils/request";
import mixin from "@/mixins/mixin";

const { beforeImgUpload } = mixin();

/** 用户列表行；性别编码与前台注册一致：0 女、1 男、2 保密（歌手页 mixin 中 2 为「组合」，不可混用） */
interface ConsumerRow {
  id: number;
  avatar?: string;
  username: string;
  sex?: number | string;
  phoneNum?: string;
  email?: string;
  birth?: string;
  introduction?: string;
  location?: string;
}

function changeSex(value: number | string | undefined) {
  if (value === 0) return "女";
  if (value === 1) return "男";
  if (value === 2) return "保密";
  if (value === 3) return "不明";
  if (value === "男" || value === "女" || value === "保密") return value;
}

const tableData = ref<ConsumerRow[]>([]);
const tempDate = ref<ConsumerRow[]>([]);
const pageSize = ref(5);
const currentPage = ref(1);

const data = computed(() =>
  tableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value),
);

const searchWord = ref("");
watch(searchWord, () => {
  if (searchWord.value === "") {
    tableData.value = [...tempDate.value];
  } else {
    tableData.value = tempDate.value.filter((item) => item.username.includes(searchWord.value));
  }
});

async function getData() {
  try {
    const result = await getAllUser();
    if (!result.success) {
      ElMessage.error(result.message || "加载用户列表失败");
      tableData.value = [];
      tempDate.value = [];
      currentPage.value = 1;
      return;
    }
    const rows = Array.isArray(result.data) ? (result.data as ConsumerRow[]) : [];
    if (!Array.isArray(result.data)) {
      ElMessage.warning("用户列表数据格式异常");
    }
    tableData.value = rows;
    tempDate.value = [...rows];
    currentPage.value = 1;
  } catch {
    ElMessage.error("请求用户列表失败，请检查网络或是否已登录后台");
    tableData.value = [];
    tempDate.value = [];
    currentPage.value = 1;
  }
}

void getData();

function userAvatarUploadUrl(id: number) {
  return attachApiUrl(`/user/avatar/update?id=${id}`);
}

function handleAvatarSuccess(response: ResponseBody) {
  ElMessage({
    message: response.message,
    type: response.type as "success" | "warning" | "info" | "error",
  });
  if (response.success) void getData();
}

function handleCurrentChange(val: number) {
  currentPage.value = val;
}

const idx = ref(-1);
const multipleSelection = ref<ConsumerRow[]>([]);
const delVisible = ref(false);

async function confirm() {
  const result = await deleteUser(idx.value);
  ElMessage({
    message: result.message,
    type: result.type as any,
  });
  if (result.success) await getData();
  delVisible.value = false;
}

function deleteRow(id: number) {
  idx.value = id;
  delVisible.value = true;
}

function handleSelectionChange(val: ConsumerRow[]) {
  multipleSelection.value = val;
}

function deleteAll() {
  for (const item of multipleSelection.value) {
    deleteRow(item.id);
    void confirm();
  }
  multipleSelection.value = [];
}
</script>

<style scoped>
.consumer-avatar-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.consumer-avatar-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}
</style>
