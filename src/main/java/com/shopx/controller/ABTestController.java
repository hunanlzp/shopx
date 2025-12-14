package com.shopx.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.shopx.annotation.ApiVersion;
import com.shopx.entity.ABTest;
import com.shopx.entity.ApiResponse;
import com.shopx.service.ABTestService;
import com.shopx.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * A/B测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/ab-test")
@ApiVersion("v1")
@Tag(name = "A/B测试", description = "A/B测试相关API")
public class ABTestController {
    
    @Autowired(required = false)
    private ABTestService abTestService;
    
    /**
     * 创建A/B测试
     */
    @Operation(summary = "创建A/B测试", description = "创建新的A/B测试")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功")
    })
    @SaCheckPermission("admin")
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<ABTest>> createABTest(
            @Parameter(description = "A/B测试信息", required = true) @RequestBody ABTest abTest) {
        
        try {
            ABTest created = abTestService.createABTest(abTest);
            return ResponseUtil.success("A/B测试创建成功", created);
        } catch (Exception e) {
            log.error("创建A/B测试失败", e);
            return ResponseUtil.error("创建A/B测试失败，请稍后重试");
        }
    }
    
    /**
     * 获取A/B测试统计
     */
    @Operation(summary = "获取A/B测试统计", description = "获取A/B测试的统计结果")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @SaCheckPermission("admin")
    @GetMapping("/test/{testId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTestStats(
            @Parameter(description = "测试ID", required = true) @PathVariable Long testId) {
        
        try {
            Map<String, Object> stats = abTestService.getTestStats(testId);
            return ResponseUtil.success("获取A/B测试统计成功", stats);
        } catch (Exception e) {
            log.error("获取A/B测试统计失败", e);
            return ResponseUtil.error("获取A/B测试统计失败，请稍后重试");
        }
    }
    
    /**
     * 获取所有A/B测试
     */
    @Operation(summary = "获取所有A/B测试", description = "获取所有A/B测试列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @SaCheckPermission("admin")
    @GetMapping("/tests")
    public ResponseEntity<ApiResponse<List<ABTest>>> getAllTests() {
        
        try {
            List<ABTest> tests = abTestService.getAllTests();
            return ResponseUtil.success("获取A/B测试列表成功", tests);
        } catch (Exception e) {
            log.error("获取A/B测试列表失败", e);
            return ResponseUtil.error("获取A/B测试列表失败，请稍后重试");
        }
    }
    
    /**
     * 更新测试状态
     */
    @Operation(summary = "更新测试状态", description = "更新A/B测试的状态")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功")
    })
    @SaCheckPermission("admin")
    @PutMapping("/test/{testId}/status")
    public ResponseEntity<ApiResponse<Void>> updateTestStatus(
            @Parameter(description = "测试ID", required = true) @PathVariable Long testId,
            @Parameter(description = "新状态", required = true) @RequestParam String status) {
        
        try {
            abTestService.updateTestStatus(testId, status);
            return ResponseUtil.success("测试状态更新成功", null);
        } catch (Exception e) {
            log.error("更新测试状态失败", e);
            return ResponseUtil.error("更新测试状态失败，请稍后重试");
        }
    }
}

