package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.entity.CommunityComment;
import com.shopx.entity.CommunityPost;
import com.shopx.mapper.CommunityCommentMapper;
import com.shopx.mapper.CommunityPostMapper;
import com.shopx.service.CommunityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 社区服务实现类
 */
@Slf4j
@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityPostMapper, CommunityPost> implements CommunityService {
    
    private final CommunityCommentMapper commentMapper;
    
    public CommunityServiceImpl(CommunityCommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }
    
    @Override
    public CommunityPost createPost(CommunityPost post) {
        post.setStatus("PUBLISHED");
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setShareCount(0);
        post.setViewCount(0);
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        save(post);
        log.info("用户 {} 发布帖子: {}", post.getUserId(), post.getTitle());
        return post;
    }
    
    @Override
    public List<CommunityPost> getPosts(String type, String category, int page, int size) {
        LambdaQueryWrapper<CommunityPost> query = new LambdaQueryWrapper<CommunityPost>()
                .eq(CommunityPost::getStatus, "PUBLISHED")
                .orderByDesc(CommunityPost::getIsPinned)
                .orderByDesc(CommunityPost::getCreateTime);
        
        if (type != null && !type.isEmpty()) {
            query.eq(CommunityPost::getPostType, type);
        }
        
        if (category != null && !category.isEmpty()) {
            query.eq(CommunityPost::getCategory, category);
        }
        
        Page<CommunityPost> pageObj = new Page<>(page, size);
        Page<CommunityPost> result = page(pageObj, query);
        
        return result.getRecords();
    }
    
    @Override
    public CommunityPost getPostById(Long postId) {
        CommunityPost post = getById(postId);
        if (post != null) {
            // 增加浏览量
            post.setViewCount((post.getViewCount() != null ? post.getViewCount() : 0) + 1);
            updateById(post);
        }
        return post;
    }
    
    @Override
    public void likePost(Long postId, Long userId) {
        CommunityPost post = getById(postId);
        if (post != null) {
            post.setLikeCount((post.getLikeCount() != null ? post.getLikeCount() : 0) + 1);
            updateById(post);
            log.info("用户 {} 点赞帖子 {}", userId, postId);
        }
    }
    
    @Override
    public void unlikePost(Long postId, Long userId) {
        CommunityPost post = getById(postId);
        if (post != null && post.getLikeCount() != null && post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            updateById(post);
            log.info("用户 {} 取消点赞帖子 {}", userId, postId);
        }
    }
    
    @Override
    public CommunityComment addComment(CommunityComment comment) {
        comment.setStatus("PUBLISHED");
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.insert(comment);
        
        // 更新帖子评论数
        if (comment.getParentId() == null) {
            CommunityPost post = getById(comment.getPostId());
            if (post != null) {
                post.setCommentCount((post.getCommentCount() != null ? post.getCommentCount() : 0) + 1);
                updateById(post);
            }
        } else {
            // 更新父评论回复数
            CommunityComment parent = commentMapper.selectById(comment.getParentId());
            if (parent != null) {
                parent.setReplyCount((parent.getReplyCount() != null ? parent.getReplyCount() : 0) + 1);
                commentMapper.updateById(parent);
            }
        }
        
        log.info("用户 {} 评论帖子 {}", comment.getUserId(), comment.getPostId());
        return comment;
    }
    
    @Override
    public List<CommunityComment> getComments(Long postId, int page, int size) {
        LambdaQueryWrapper<CommunityComment> query = new LambdaQueryWrapper<CommunityComment>()
                .eq(CommunityComment::getPostId, postId)
                .eq(CommunityComment::getStatus, "PUBLISHED")
                .isNull(CommunityComment::getParentId) // 只获取顶级评论
                .orderByDesc(CommunityComment::getCreateTime);
        
        Page<CommunityComment> pageObj = new Page<>(page, size);
        Page<CommunityComment> result = commentMapper.selectPage(pageObj, query);
        
        return result.getRecords();
    }
    
    @Override
    public void likeComment(Long commentId, Long userId) {
        CommunityComment comment = commentMapper.selectById(commentId);
        if (comment != null) {
            comment.setLikeCount((comment.getLikeCount() != null ? comment.getLikeCount() : 0) + 1);
            commentMapper.updateById(comment);
            log.info("用户 {} 点赞评论 {}", userId, commentId);
        }
    }
    
    @Override
    public List<Map<String, Object>> getUserRanking(String type, int limit) {
        // 这里需要根据type（如环保贡献、发帖数等）查询用户排行
        // 简化实现：返回空列表
        List<Map<String, Object>> ranking = new ArrayList<>();
        return ranking;
    }
}

