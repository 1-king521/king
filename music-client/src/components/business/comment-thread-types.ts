export interface CommentNode extends Record<string, unknown> {
  id: string | number;
  children: CommentNode[];
}

export function buildCommentTree(list: unknown[]): CommentNode[] {
  if (!Array.isArray(list) || !list.length) return [];
  const map = new Map<string | number, CommentNode>();
  for (const raw of list) {
    const item = raw as Record<string, unknown>;
    if (!item || item.id == null) continue;
    const id = item.id as string | number;
    map.set(id, { ...item, id, children: [] } as CommentNode);
  }
  const roots: CommentNode[] = [];
  for (const raw of list) {
    const item = raw as Record<string, unknown>;
    if (!item || item.id == null) continue;
    const id = item.id as string | number;
    const node = map.get(id)!;
    const pid = item.parentId ?? item.parent_id;
    if (pid != null && pid !== "" && map.has(pid as string | number)) {
      map.get(pid as string | number)!.children.push(node);
    } else {
      roots.push(node);
    }
  }
  const sortByTime = (a: CommentNode, b: CommentNode) => {
    const ta = a.createTime ? new Date(a.createTime as string | number | Date).getTime() : 0;
    const tb = b.createTime ? new Date(b.createTime as string | number | Date).getTime() : 0;
    return ta - tb;
  };
  function sortTree(nodes: CommentNode[]) {
    nodes.sort(sortByTime);
    for (const n of nodes) {
      if (n.children.length) sortTree(n.children);
    }
  }
  sortTree(roots);
  return roots;
}

/** 扁平化整棵树（用于同步点赞状态等） */
export function flattenCommentTree(nodes: CommentNode[]): CommentNode[] {
  const out: CommentNode[] = [];
  function walk(ns: CommentNode[]) {
    for (const n of ns) {
      out.push(n);
      if (n.children?.length) walk(n.children);
    }
  }
  walk(nodes);
  return out;
}

/** 子树内评论总数（不含当前节点） */
export function descendantReplyCount(node: CommentNode): number {
  let n = 0;
  for (const c of node.children) {
    n += 1 + descendantReplyCount(c);
  }
  return n;
}
