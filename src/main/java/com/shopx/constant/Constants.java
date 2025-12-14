package com.shopx.constant;

/**
 * 系统常量类
 * 统一管理系统中的常量定义
 */
public class Constants {

    /**
     * 用户角色常量
     */
    public static class UserRole {
        public static final String USER = "USER";
        public static final String SELLER = "SELLER";
        public static final String ADMIN = "ADMIN";
    }

    /**
     * 权限常量
     */
    public static class Permission {
        // 用户权限
        public static final String USER_LIST = "user:list";
        public static final String USER_ADD = "user:add";
        public static final String USER_UPDATE = "user:update";
        public static final String USER_DELETE = "user:delete";
        
        // 商品权限
        public static final String PRODUCT_LIST = "product:list";
        public static final String PRODUCT_ADD = "product:add";
        public static final String PRODUCT_UPDATE = "product:update";
        public static final String PRODUCT_DELETE = "product:delete";
        
        // 订单权限
        public static final String ORDER_LIST = "order:list";
        public static final String ORDER_ADD = "order:add";
        public static final String ORDER_UPDATE = "order:update";
        public static final String ORDER_DELETE = "order:delete";
        
        // 回收权限
        public static final String RECYCLE_LIST = "recycle:list";
        public static final String RECYCLE_ADD = "recycle:add";
        public static final String RECYCLE_UPDATE = "recycle:update";
        public static final String RECYCLE_DELETE = "recycle:delete";
    }

    /**
     * 订单状态常量
     */
    public static class OrderStatus {
        public static final String PENDING = "PENDING";
        public static final String CONFIRMED = "CONFIRMED";
        public static final String SHIPPED = "SHIPPED";
        public static final String DELIVERED = "DELIVERED";
        public static final String CANCELLED = "CANCELLED";
        public static final String REFUNDED = "REFUNDED";
    }

    /**
     * 回收订单状态常量
     */
    public static class RecycleStatus {
        public static final String PENDING = "PENDING";
        public static final String SCHEDULED = "SCHEDULED";
        public static final String COMPLETED = "COMPLETED";
        public static final String CANCELLED = "CANCELLED";
    }

    /**
     * 协作购物会话状态常量
     */
    public static class SessionStatus {
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";
        public static final String ENDED = "ENDED";
    }

    /**
     * 环保活动状态常量
     */
    public static class ActivityStatus {
        public static final String UPCOMING = "UPCOMING";
        public static final String ONGOING = "ONGOING";
        public static final String COMPLETED = "COMPLETED";
    }

    /**
     * 环保等级常量
     */
    public static class EcoLevel {
        public static final String BRONZE = "Bronze";
        public static final String SILVER = "Silver";
        public static final String GOLD = "Gold";
        public static final String PLATINUM = "Platinum";
    }

    /**
     * 季节常量
     */
    public static class Season {
        public static final String SPRING = "SPRING";
        public static final String SUMMER = "SUMMER";
        public static final String AUTUMN = "AUTUMN";
        public static final String WINTER = "WINTER";
        public static final String ALL_SEASON = "ALL_SEASON";
    }

    /**
     * 缓存键常量
     */
    public static class CacheKey {
        public static final String USER_PREFIX = "shopx:user:";
        public static final String PRODUCT_PREFIX = "shopx:product:";
        public static final String SESSION_PREFIX = "shopx:session:";
        public static final String PERMISSION_PREFIX = "shopx:permission:";
        public static final String RECOMMENDATION_PREFIX = "shopx:recommendation:";
        public static final String LOCK_PREFIX = "shopx:lock:";
    }

    /**
     * 分页常量
     */
    public static class Page {
        public static final int DEFAULT_PAGE = 1;
        public static final int DEFAULT_SIZE = 20;
        public static final int MAX_SIZE = 100;
    }

    /**
     * 时间常量（秒）
     */
    public static class Time {
        public static final int MINUTE = 60;
        public static final int HOUR = 60 * MINUTE;
        public static final int DAY = 24 * HOUR;
        public static final int WEEK = 7 * DAY;
        public static final int MONTH = 30 * DAY;
    }

    /**
     * 文件类型常量
     */
    public static class FileType {
        public static final String[] IMAGE_TYPES = {"jpg", "jpeg", "png", "gif", "webp"};
        public static final String[] DOCUMENT_TYPES = {"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"};
        public static final String[] VIDEO_TYPES = {"mp4", "avi", "mov", "wmv", "flv"};
        public static final String[] AUDIO_TYPES = {"mp3", "wav", "flac", "aac", "ogg"};
    }

    /**
     * 消息类型常量
     */
    public static class MessageType {
        public static final String TEXT = "text";
        public static final String IMAGE = "image";
        public static final String VIDEO = "video";
        public static final String AUDIO = "audio";
        public static final String FILE = "file";
        public static final String PRODUCT_CARD = "product_card";
        public static final String SYSTEM = "system";
    }

    /**
     * WebSocket消息类型常量
     */
    public static class WebSocketMessageType {
        public static final String CHAT = "chat";
        public static final String JOIN = "join";
        public static final String LEAVE = "leave";
        public static final String ANNOTATION = "annotation";
        public static final String PRODUCT_CHANGE = "product-change";
        public static final String EXPERIENCE = "experience";
    }

    /**
     * 业务规则常量
     */
    public static class BusinessRule {
        public static final int MAX_LOGIN_ATTEMPTS = 5;
        public static final int LOCKOUT_DURATION_MINUTES = 15;
        public static final int SESSION_TIMEOUT_MINUTES = 30;
        public static final int MAX_FILE_SIZE_MB = 10;
        public static final int PASSWORD_MIN_LENGTH = 6;
        public static final int PASSWORD_MAX_LENGTH = 20;
        public static final int USERNAME_MIN_LENGTH = 3;
        public static final int USERNAME_MAX_LENGTH = 20;
    }

    /**
     * 响应码常量
     */
    public static class ResponseCode {
        public static final int SUCCESS = 200;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int METHOD_NOT_ALLOWED = 405;
        public static final int CONFLICT = 409;
        public static final int INTERNAL_SERVER_ERROR = 500;
        public static final int SERVICE_UNAVAILABLE = 503;
    }

    /**
     * 响应消息常量
     */
    public static class ResponseMessage {
        public static final String SUCCESS = "操作成功";
        public static final String FAILED = "操作失败";
        public static final String PARAM_ERROR = "参数错误";
        public static final String UNAUTHORIZED = "未授权访问";
        public static final String FORBIDDEN = "权限不足";
        public static final String NOT_FOUND = "资源不存在";
        public static final String SERVER_ERROR = "服务器内部错误";
        public static final String SERVICE_UNAVAILABLE = "服务暂不可用";
    }

    /**
     * 正则表达式常量
     */
    public static class Regex {
        public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        public static final String PHONE = "^1[3-9]\\d{9}$";
        public static final String USERNAME = "^[a-zA-Z0-9_]{3,20}$";
        public static final String PASSWORD = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,20}$";
        public static final String URL = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";
    }

    /**
     * A/B测试状态常量
     */
    public static class ABTestStatus {
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";
        public static final String ENDED = "ENDED";
    }

    /**
     * 退货订单状态常量
     */
    public static class ReturnOrderStatus {
        public static final String PENDING = "PENDING";
        public static final String APPROVED = "APPROVED";
        public static final String REJECTED = "REJECTED";
        public static final String RETURNING = "RETURNING";
        public static final String RETURNED = "RETURNED";
        public static final String REFUNDED = "REFUNDED";
        public static final String CANCELLED = "CANCELLED";
    }

    /**
     * 价格保护状态常量
     */
    public static class PriceProtectionStatus {
        public static final String PENDING = "PENDING";
        public static final String APPROVED = "APPROVED";
        public static final String REJECTED = "REJECTED";
        public static final String COMPLETED = "COMPLETED";
    }

    /**
     * 物流状态常量
     */
    public static class LogisticsStatus {
        public static final String PENDING = "PENDING";
        public static final String PICKED_UP = "PICKED_UP";
        public static final String IN_TRANSIT = "IN_TRANSIT";
        public static final String OUT_FOR_DELIVERY = "OUT_FOR_DELIVERY";
        public static final String DELIVERED = "DELIVERED";
        public static final String EXCEPTION = "EXCEPTION";
    }

    /**
     * 库存预订状态常量
     */
    public static class StockReservationStatus {
        public static final String PENDING = "PENDING";
        public static final String CONFIRMED = "CONFIRMED";
        public static final String CANCELLED = "CANCELLED";
        public static final String EXPIRED = "EXPIRED";
    }

    /**
     * 工单状态常量
     */
    public static class TicketStatus {
        public static final String OPEN = "OPEN";
        public static final String IN_PROGRESS = "IN_PROGRESS";
        public static final String RESOLVED = "RESOLVED";
        public static final String CLOSED = "CLOSED";
    }

    /**
     * 工单优先级常量
     */
    public static class TicketPriority {
        public static final String LOW = "LOW";
        public static final String MEDIUM = "MEDIUM";
        public static final String HIGH = "HIGH";
        public static final String URGENT = "URGENT";
    }

    /**
     * 工单类型常量
     */
    public static class TicketType {
        public static final String ORDER = "ORDER";
        public static final String PRODUCT = "PRODUCT";
        public static final String PAYMENT = "PAYMENT";
        public static final String LOGISTICS = "LOGISTICS";
        public static final String RETURN = "RETURN";
        public static final String OTHER = "OTHER";
    }

    /**
     * 评价状态常量
     */
    public static class ReviewStatus {
        public static final String PENDING = "PENDING";
        public static final String APPROVED = "APPROVED";
        public static final String REJECTED = "REJECTED";
    }

    /**
     * 算法类型常量
     */
    public static class AlgorithmType {
        public static final String HYBRID = "hybrid";
        public static final String COLLABORATIVE = "collaborative";
        public static final String CONTENT_BASED = "content";
        public static final String DEFAULT = HYBRID;
    }

    /**
     * AI提供商常量
     */
    public static class AIProvider {
        public static final String OPENAI = "openai";
        public static final String CLAUDE = "claude";
        public static final String CUSTOM = "custom";
        public static final String DEFAULT = OPENAI;
    }

    /**
     * 商品审核状态常量
     */
    public static class ProductAuditStatus {
        public static final String PENDING = "PENDING";
        public static final String APPROVED = "APPROVED";
        public static final String REJECTED = "REJECTED";
    }

    /**
     * 账户状态常量
     */
    public static class AccountStatus {
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";
        public static final String LOCKED = "LOCKED";
        public static final String DELETED = "DELETED";
    }
}
