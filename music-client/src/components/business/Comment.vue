<template>
  <section class="comment-panel">
    <div class="comment-editor">
      <div v-if="replyingTo" class="comment-editor__reply-hint">
        正在回复 <strong>@{{ replyingTo.username }}</strong>
        <el-button link type="primary" class="comment-editor__cancel-reply" @click="cancelReply">取消</el-button>
      </div>
      <div class="comment-editor__controls">
        <el-input
          class="comment-editor__input"
          type="textarea"
          :placeholder="editorPlaceholder"
          :rows="2"
          v-model="textarea"
        />
        <el-button class="comment-editor__submit" type="primary" :disabled="!textarea.trim()" @click="submitComment()">
          {{ replyingTo ? "发表回复" : "发表评论" }}
        </el-button>
      </div>
    </div>
    <ul v-if="nestedTree.length" class="comment-list">
      <CommentThreadNode v-for="node in nestedTree" :key="node.id" :node="node" :depth="0" />
    </ul>
    <el-empty v-else description="暂无评论"></el-empty>
  </section>
</template>

<script lang="ts" setup>
import { ref, toRefs, computed, watch, provide } from "vue";
import { ElMessage } from "element-plus";
import { useUserStore } from "../../store/user";
import {
  fetchDeleteComment,
  fetchDeleteUserSupport,
  fetchInsertUserSupport,
  fetchSetComment,
  fetchSetSupport,
  fetchTestAlreadySupport,
} from "../../api/comment";
import { elMessageTypeFromResponse } from "@/api/types";
import { useAppActions } from "../../composables/useAppActions";
import { buildCommentTree, flattenCommentTree, type CommentNode } from "./comment-thread-types";
import { COMMENT_ACTIONS_KEY } from "./comment-inject";
import CommentThreadNode from "./CommentThreadNode.vue";

const userStore = useUserStore();
const { checkStatus } = useAppActions();

const props = defineProps({
  playId: Number || String,
  type: Number,
  commentList: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits<{
  (e: "refresh"): void;
}>();

const { playId, type } = toRefs(props);
const textarea = ref("");
const replyingTo = ref<{ id: string | number; username: string } | null>(null);

const userId = computed(() => userStore.userId);
const supportCommentIdSet = ref<Set<string | number>>(new Set());

const nestedTree = computed(() => buildCommentTree(Array.isArray(props.commentList) ? (props.commentList as unknown[]) : []));

const allCommentsFlat = computed(() => flattenCommentTree(nestedTree.value));

const editorPlaceholder = computed(() =>
  replyingTo.value ? `回复 @${replyingTo.value.username}…` : "期待您的精彩评论...",
);

function cancelReply() {
  replyingTo.value = null;
}

function startReply(item: CommentNode) {
  if (!checkStatus()) return;
  replyingTo.value = { id: item.id, username: String(item.username ?? "用户") };
}

function isSupported(commentId: string | number) {
  return supportCommentIdSet.value.has(commentId);
}

provide(COMMENT_ACTIONS_KEY, {
  get userId() {
    return userId.value;
  },
  isSupported,
  startReply,
  setSupport: (id: string | number, likeCount: number) => {
    void setSupport(id, likeCount, userId.value);
  },
  deleteComment: (id: string | number) => {
    void deleteComment(id);
  },
});

async function syncSupportState() {
  const comments = allCommentsFlat.value;
  if (!userId.value || !comments.length) {
    supportCommentIdSet.value = new Set();
    return;
  }

  const nextSet = new Set<string | number>();
  await Promise.all(
    comments.map(async (item) => {
      const commentId = item && item.id;
      if (!commentId) return;
      try {
        const result = await fetchTestAlreadySupport({
          commentId,
          userId: userId.value,
        });
        if (result && result.data) {
          nextSet.add(commentId);
        }
      } catch {
        /* ignore */
      }
    }),
  );
  supportCommentIdSet.value = nextSet;
}

watch(
  [allCommentsFlat, userId],
  () => {
    void syncSupportState();
  },
  { immediate: true },
);

async function submitComment() {
  if (!checkStatus()) return;

  let songListId = null;
  let songId = null;
  let nowType = null;
  if (type.value === 1) {
    nowType = 1;
    songListId = `${playId.value}`;
  } else if (type.value === 0) {
    nowType = 0;
    songId = `${playId.value}`;
  }

  const content = textarea.value;
  const parentId = replyingTo.value ? replyingTo.value.id : null;
  const result = await fetchSetComment({
    userId: userId.value,
    content,
    songId,
    songListId,
    nowType,
    parentId,
  });
  ElMessage({
    message: result.message,
    type: elMessageTypeFromResponse(result),
  });

  if (result.success) {
    textarea.value = "";
    replyingTo.value = null;
    emit("refresh");
  }
}

async function deleteComment(id: string | number) {
  const result = await fetchDeleteComment(id);
  ElMessage({
    message: result.message,
    type: elMessageTypeFromResponse(result),
  });

  if (result.success) {
    emit("refresh");
  }
}

async function setSupport(id: string | number, likeCount: number, uid: string | number) {
  if (!checkStatus()) return;
  if (!uid) return;

  try {
    let result = null;
    let operatorR = null;
    const commentId = id;
    const r = await fetchTestAlreadySupport({ commentId, userId: uid });
    if (!r.success) {
      ElMessage({
        message: r.message,
        type: elMessageTypeFromResponse(r),
      });
      return;
    }

    const wasSupported = !!r.data;
    let nextLike = likeCount;
    if (wasSupported) {
      nextLike = likeCount - 1;
      operatorR = await fetchDeleteUserSupport({ commentId, userId: uid });
      result = await fetchSetSupport({ id, likeCount: nextLike });
      supportCommentIdSet.value.delete(id);
    } else {
      nextLike = likeCount + 1;
      operatorR = await fetchInsertUserSupport({ commentId, userId: uid });
      result = await fetchSetSupport({ id, likeCount: nextLike });
      supportCommentIdSet.value.add(id);
    }
    if (result.success && operatorR.success) {
      ElMessage({
        message: wasSupported ? "已取消点赞" : "点赞成功",
        type: "success",
      });
      emit("refresh");
    } else {
      ElMessage({
        message: (result && result.message) || (operatorR && operatorR.message) || "操作失败",
        type: "error",
      });
    }
  } catch (error) {
    const err = error as { data?: { message?: string }; response?: { data?: { message?: string } } };
    const fallbackMessage =
      (err.data && err.data.message) ||
      (err.response && err.response.data && err.response.data.message) ||
      "点赞操作失败";
    ElMessage({
      message: fallbackMessage,
      type: "error",
    });
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.comment-editor {
  margin-bottom: 16px;

  &__reply-hint {
    font-size: 14px;
    color: #606266;
    margin-bottom: 8px;

    strong {
      color: #409eff;
    }
  }

  &__cancel-reply {
    margin-left: 8px;
    vertical-align: baseline;
  }

  &__controls {
    display: flex;
    align-items: flex-end;
    gap: 12px;
  }

  &__input {
    flex: 1;
    min-width: 0;
  }

  &__submit {
    flex-shrink: 0;
    height: 52px;
  }
}

.comment-list {
  width: 100%;
  list-style: none;
  padding: 0;
  margin: 0;
}

.icon {
  @include icon(1em);
}
</style>
