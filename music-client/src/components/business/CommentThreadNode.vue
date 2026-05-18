<template>
  <li class="comment-thread-node" :class="{ 'is-nested': depth > 0 }" :style="rowDepthStyle(depth)">
    <div class="comment-thread-node__row">
      <el-image class="comment-list__avatar" fit="contain" :src="attachImageUrl(node.avatar)" />
      <div class="comment-list__content">
        <ul>
          <li class="comment-list__name">{{ node.username }}</li>
          <li class="comment-list__time">{{ formatDate(node.createTime) }}</li>
          <li class="comment-list__text">{{ node.content }}</li>
        </ul>
      </div>
      <div class="comment-list__actions">
        <el-button link type="primary" class="comment-reply-btn" @click.stop="actions.startReply(node)">回复</el-button>
        <div
          class="comment-list__like"
          :class="{ 'is-active': actions.isSupported(node.id) }"
          @click="actions.setSupport(node.id, Number(node.likeCount) || 0)"
        >
          <div class="comment-like"><ThumbsUp class="comment-like-icon" /> {{ node.likeCount }}</div>
        </div>
        <Trash2
          v-if="node.userId === actions.userId"
          class="comment-delete-icon"
          @click.stop="actions.deleteComment(node.id)"
        />
      </div>
    </div>

    <div v-if="node.children.length" class="comment-thread-node__collapse">
      <button type="button" class="comment-thread-node__toggle" @click="repliesExpanded = !repliesExpanded">
        <ChevronRight class="comment-thread-node__chevron" :class="{ 'is-open': repliesExpanded }" />
        <span v-if="repliesExpanded">收起 {{ replyTotal }} 条回复</span>
        <span v-else>展开 {{ replyTotal }} 条回复</span>
      </button>
    </div>

    <ul v-show="repliesExpanded && node.children.length" class="comment-thread-node__children">
      <CommentThreadNode v-for="child in node.children" :key="child.id" :node="child" :depth="depth + 1" />
    </ul>
  </li>
</template>

<script lang="ts" setup>
import { ref, computed, inject } from "vue";
import CommentThreadNode from "./CommentThreadNode.vue";
import { ThumbsUp, Trash2, ChevronRight } from "lucide-vue-next";
import { attachImageUrl, formatDate } from "@/utils";
import type { CommentNode } from "./comment-thread-types";
import { descendantReplyCount } from "./comment-thread-types";
import { COMMENT_ACTIONS_KEY, type CommentActions } from "./comment-inject";

const props = defineProps<{
  node: CommentNode;
  depth: number;
}>();

const actions = inject(COMMENT_ACTIONS_KEY) as CommentActions;

const repliesExpanded = ref(true);

const replyTotal = computed(() => descendantReplyCount(props.node));

function rowDepthStyle(depth: number) {
  if (!depth) return {};
  return { marginLeft: `${Math.min(depth, 8) * 16}px` };
}
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.comment-thread-node {
  list-style: none;
  border-bottom: solid 1px rgba(0, 0, 0, 0.1);
  padding: 15px 0 8px;
  transition: margin 0.15s ease;

  &.is-nested {
    border-left: 3px solid rgba(64, 158, 255, 0.25);
    padding-left: 10px;
    margin-top: 4px;
    background: rgba(247, 248, 250, 0.6);
  }

  &__row {
    display: flex;
    align-items: flex-start;
  }

  &__collapse {
    margin: 6px 0 0 50px;
  }

  &__toggle {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    padding: 4px 8px;
    border: none;
    border-radius: 6px;
    background: rgba(64, 158, 255, 0.12);
    color: #409eff;
    font-size: 13px;
    cursor: pointer;
    line-height: 1.4;

    &:hover {
      background: rgba(64, 158, 255, 0.2);
    }
  }

  &__chevron {
    width: 16px;
    height: 16px;
    flex-shrink: 0;
    transition: transform 0.2s ease;

    &.is-open {
      transform: rotate(90deg);
    }
  }

  &__children {
    list-style: none;
    padding: 0;
    margin: 0;
  }
}

.comment-list__avatar {
  width: 50px;
  flex-shrink: 0;
}

.comment-list__content {
  padding: 0 20px;
  flex: 1;
  min-width: 0;

  ul {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .comment-list__time {
    font-size: 0.6rem;
    color: rgba(0, 0, 0, 0.5);
  }

  .comment-list__name {
    color: rgba(0, 0, 0, 0.5);
  }

  .comment-list__text {
    font-size: 1rem;
    word-break: break-word;
  }
}

.comment-list__actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
  font-size: 0.875rem;

  .comment-list__like {
    cursor: pointer;
    color: rgba(0, 0, 0, 0.55);
    display: inline-flex;
    align-items: center;

    &.is-active {
      color: $color-blue;
    }
  }

  .comment-like {
    display: inline-flex;
    align-items: center;
    gap: 4px;
  }

  .comment-like-icon {
    width: 16px;
    height: 16px;
  }

  .comment-delete-icon {
    width: 16px;
    height: 16px;
    cursor: pointer;
    color: rgba(0, 0, 0, 0.45);
  }

  .comment-reply-btn {
    padding: 0 4px;
    font-size: 13px;
  }
}
</style>
