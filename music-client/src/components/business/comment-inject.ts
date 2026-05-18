import type { CommentNode } from "./comment-thread-types";

export interface CommentActions {
  userId: string | number;
  isSupported: (commentId: string | number) => boolean;
  startReply: (item: CommentNode) => void;
  setSupport: (id: string | number, likeCount: number) => void;
  deleteComment: (id: string | number) => void;
}

/** 不用 InjectionKey，避免部分环境下 vue 类型未导出该符号 */
export const COMMENT_ACTIONS_KEY = Symbol("commentActions");
