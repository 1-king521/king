package com.example.wyk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wyk.common.R;
import com.example.wyk.mapper.CommentMapper;
import com.example.wyk.mapper.AppUserMapper;
import com.example.wyk.mapper.UserSupportMapper;
import com.example.wyk.model.domain.AppUser;
import com.example.wyk.model.domain.Comment;
import com.example.wyk.model.domain.UserSupport;
import com.example.wyk.model.request.CommentRequest;
import com.example.wyk.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    private final CommentMapper commentMapper;

    private final AppUserMapper appUserMapper;

    private final UserSupportMapper userSupportMapper;

    @Override
    public R addComment(CommentRequest addCommentRequest) {
        Integer parentId = addCommentRequest.getParentId();
        if (parentId != null && parentId <= 0) {
            parentId = null;
        }
        if (parentId != null) {
            Comment parent = commentMapper.selectById(parentId);
            if (parent == null) {
                return R.error("被回复的评论不存在");
            }
            Byte reqType = addCommentRequest.getNowType();
            if (reqType != null && reqType == 0) {
                if (!Objects.equals(parent.getSongId(), addCommentRequest.getSongId())) {
                    return R.error("回复与歌曲不一致");
                }
            } else if (reqType != null && reqType == 1) {
                if (!Objects.equals(parent.getSongListId(), addCommentRequest.getSongListId())) {
                    return R.error("回复与歌单不一致");
                }
            }
        }

        Comment comment = new Comment();
        BeanUtils.copyProperties(addCommentRequest, comment);
        comment.setType(addCommentRequest.getNowType());
        comment.setParentId(parentId);
        if (commentMapper.insert(comment) > 0) {
            return R.success(parentId != null ? "回复成功" : "评论成功");
        } else {
            return R.error("评论失败");
        }
    }

    @Override
    public R updateCommentMsg(CommentRequest addCommentRequest) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(addCommentRequest, comment);
        if (commentMapper.updateById(comment) > 0) {
            return R.success("点赞成功");
        } else {
            return R.error("点赞失败");
        }
    }

    //    删除评论
    @Override
    public R deleteComment(Integer id) {
        List<Integer> ids = collectCommentTreeIds(id);
        if (ids.isEmpty()) {
            return R.error("删除失败");
        }
        userSupportMapper.delete(new QueryWrapper<UserSupport>().in("comment_id", ids));
        if (commentMapper.deleteBatchIds(ids) > 0) {
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    /** BFS 收集待删评论 id（含根节点及全部楼中楼回复） */
    private List<Integer> collectCommentTreeIds(Integer rootId) {
        if (rootId == null || rootId <= 0) {
            return Collections.emptyList();
        }
        List<Integer> order = new ArrayList<>();
        Deque<Integer> q = new ArrayDeque<>();
        q.add(rootId);
        while (!q.isEmpty()) {
            Integer cur = q.poll();
            order.add(cur);
            List<Comment> children = commentMapper.selectList(
                    new QueryWrapper<Comment>().eq("parent_id", cur));
            for (Comment c : children) {
                if (c.getId() != null) {
                    q.add(c.getId());
                }
            }
        }
        return order;
    }

    @Override
    public R commentOfSongId(Integer songId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_id", songId).orderByAsc("create_time");
        return R.success(null, buildCommentWithUser(commentMapper.selectList(queryWrapper)));
    }

    @Override
    public R commentOfSongListId(Integer songListId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_sheet_id", songListId).orderByAsc("create_time");
        return R.success(null, buildCommentWithUser(commentMapper.selectList(queryWrapper)));
    }

    private List<Map<String, Object>> buildCommentWithUser(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Integer> userIds = comments.stream()
                .map(Comment::getUserId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toCollection(HashSet::new));
        Map<Integer, AppUser> userMap = new LinkedHashMap<>();
        if (!userIds.isEmpty()) {
            QueryWrapper<AppUser> wrapper = new QueryWrapper<>();
            wrapper.in("id", userIds);
            userMap = appUserMapper.selectList(wrapper).stream()
                    .collect(Collectors.toMap(AppUser::getId, Function.identity(), (a, b) -> a, LinkedHashMap::new));
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Comment comment : comments) {
            AppUser appUser = userMap.get(comment.getUserId());
            Map<String, Object> commentMap = new LinkedHashMap<>();
            commentMap.put("id", comment.getId());
            commentMap.put("userId", comment.getUserId());
            commentMap.put("songId", comment.getSongId());
            commentMap.put("songListId", comment.getSongListId());
            commentMap.put("content", comment.getContent());
            commentMap.put("createTime", comment.getCreateTime());
            commentMap.put("type", comment.getType());
            commentMap.put("likeCount", comment.getLikeCount());
            commentMap.put("parentId", comment.getParentId());
            commentMap.put("username", appUser != null ? appUser.getUsername() : "");
            commentMap.put("avatar", appUser != null ? appUser.getAvatar() : "");
            result.add(commentMap);
        }
        return result;
    }
}
