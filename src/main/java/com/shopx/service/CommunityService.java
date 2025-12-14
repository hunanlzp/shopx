package com.shopx.service;

import com.shopx.entity.CommunityComment;
import com.shopx.entity.CommunityPost;

import java.util.List;
import java.util.Map;

/**
 * 社区服务接口
 */
public interface CommunityService {
    
    /**
     * 发布帖子
     */
    CommunityPost createPost(CommunityPost post);
    
    /**
     * 获取帖子列表
     */
    List<CommunityPost> getPosts(String type, String category, int page, int size);
    
    /**
     * 获取帖子详情
     */
    CommunityPost getPostById(Long postId);
    
    /**
     * 点赞帖子
     */
    void likePost(Long postId, Long userId);
    
    /**
     * 取消点赞
     */
    void unlikePost(Long postId, Long userId);
    
    /**
     * 添加评论
     */
    CommunityComment addComment(CommunityComment comment);
    
    /**
     * 获取评论列表
     */
    List<CommunityComment> getComments(Long postId, int page, int size);
    
    /**
     * 点赞评论
     */
    void likeComment(Long commentId, Long userId);
    
    /**
     * 获取用户排行榜
     */
    List<Map<String, Object>> getUserRanking(String type, int limit);
}

