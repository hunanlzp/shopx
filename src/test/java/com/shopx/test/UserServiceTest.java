package com.shopx.test;

import com.shopx.entity.User;
import com.shopx.service.UserService;
import com.shopx.service.impl.UserServiceImpl;
import com.shopx.mapper.UserMapper;
import com.shopx.cache.CacheManager;
import com.shopx.util.PasswordUtil;
import com.shopx.validation.ValidationUtils;
import com.shopx.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试类
 */
@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi"); // 加密后的密码
        testUser.setRole("USER");
        testUser.setAvatar("https://example.com/avatar.jpg");
        testUser.setEnabled(true);
        testUser.setFollowerCount(10);
        testUser.setFollowingCount(5);
        testUser.setSustainabilityScore(100);
        testUser.setRecycleCount(3);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void testAuthenticate_Success() {
        // Given
        String username = "testuser";
        String password = "password123";
        when(userMapper.selectOne(any())).thenReturn(testUser);

        // When
        User result = userService.authenticate(username, password);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userMapper).selectOne(any());
    }

    @Test
    void testAuthenticate_InvalidUsername() {
        // Given
        String username = "nonexistent";
        String password = "password123";
        when(userMapper.selectOne(any())).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            userService.authenticate(username, password);
        });
        verify(userMapper).selectOne(any());
    }

    @Test
    void testAuthenticate_InvalidPassword() {
        // Given
        String username = "testuser";
        String password = "wrongpassword";
        when(userMapper.selectOne(any())).thenReturn(testUser);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            userService.authenticate(username, password);
        });
        verify(userMapper).selectOne(any());
    }

    @Test
    void testCreateUser_Success() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password123");
        newUser.setRole("USER");

        when(userMapper.selectOne(any())).thenReturn(null); // 用户名不存在
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // When
        User result = userService.createUser(newUser);

        // Then
        assertNotNull(result);
        assertEquals(newUser.getUsername(), result.getUsername());
        assertEquals(newUser.getEmail(), result.getEmail());
        assertNotNull(result.getPassword()); // 密码应该被加密
        verify(userMapper).selectOne(any());
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void testCreateUser_UsernameExists() {
        // Given
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password123");

        when(userMapper.selectOne(any())).thenReturn(testUser); // 用户名已存在

        // When & Then
        assertThrows(BusinessException.class, () -> {
            userService.createUser(newUser);
        });
        verify(userMapper).selectOne(any());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void testGetUserById_Success() {
        // Given
        Long userId = 1L;
        when(userMapper.selectById(userId)).thenReturn(testUser);

        // When
        User result = userService.getUserById(userId);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userMapper).selectById(userId);
    }

    @Test
    void testGetUserById_NotFound() {
        // Given
        Long userId = 999L;
        when(userMapper.selectById(userId)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            userService.getUserById(userId);
        });
        verify(userMapper).selectById(userId);
    }

    @Test
    void testUpdateUser_Success() {
        // Given
        Long userId = 1L;
        User updateUser = new User();
        updateUser.setEmail("updated@example.com");
        updateUser.setAvatar("https://example.com/new-avatar.jpg");

        when(userMapper.selectById(userId)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        User result = userService.updateUser(userId, updateUser);

        // Then
        assertNotNull(result);
        assertEquals(updateUser.getEmail(), result.getEmail());
        assertEquals(updateUser.getAvatar(), result.getAvatar());
        verify(userMapper).selectById(userId);
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        // Given
        Long userId = 999L;
        User updateUser = new User();
        updateUser.setEmail("updated@example.com");

        when(userMapper.selectById(userId)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            userService.updateUser(userId, updateUser);
        });
        verify(userMapper).selectById(userId);
        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    void testChangePassword_Success() {
        // Given
        Long userId = 1L;
        String oldPassword = "password123";
        String newPassword = "newpassword123";

        when(userMapper.selectById(userId)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        userService.changePassword(userId, oldPassword, newPassword);

        // Then
        verify(userMapper).selectById(userId);
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testChangePassword_InvalidOldPassword() {
        // Given
        Long userId = 1L;
        String oldPassword = "wrongpassword";
        String newPassword = "newpassword123";

        when(userMapper.selectById(userId)).thenReturn(testUser);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            userService.changePassword(userId, oldPassword, newPassword);
        });
        verify(userMapper).selectById(userId);
        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    void testDisableUser_Success() {
        // Given
        Long userId = 1L;
        when(userMapper.selectById(userId)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        userService.disableUser(userId);

        // Then
        verify(userMapper).selectById(userId);
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testEnableUser_Success() {
        // Given
        Long userId = 1L;
        testUser.setEnabled(false);
        when(userMapper.selectById(userId)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        userService.enableUser(userId);

        // Then
        verify(userMapper).selectById(userId);
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testGetUserPermissions_Success() {
        // Given
        Long userId = 1L;
        when(userMapper.selectById(userId)).thenReturn(testUser);

        // When
        List<String> permissions = userService.getUserPermissions(userId);

        // Then
        assertNotNull(permissions);
        assertFalse(permissions.isEmpty());
        verify(userMapper).selectById(userId);
    }

    @Test
    void testGetUserRoles_Success() {
        // Given
        Long userId = 1L;
        when(userMapper.selectById(userId)).thenReturn(testUser);

        // When
        List<String> roles = userService.getUserRoles(userId);

        // Then
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
        assertTrue(roles.contains("USER"));
        verify(userMapper).selectById(userId);
    }

    @Test
    void testGetAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userMapper.selectList(any())).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers(1, 20);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        verify(userMapper).selectList(any());
    }

    @Test
    void testSearchUsers_Success() {
        // Given
        String keyword = "test";
        List<User> users = Arrays.asList(testUser);
        when(userMapper.selectList(any())).thenReturn(users);

        // When
        List<User> result = userService.searchUsers(keyword, 1, 20);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        verify(userMapper).selectList(any());
    }
}
