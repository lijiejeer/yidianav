package jbc.com.cn.yidianav.controller;

import jbc.com.cn.yidianav.dto.ApiResponse;
import jbc.com.cn.yidianav.entity.LoginHistory;
import jbc.com.cn.yidianav.entity.User;
import jbc.com.cn.yidianav.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ApiResponse<User> getCurrentUser(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        Optional<User> user = userService.findByUsername(username);
        return user.map(ApiResponse::success)
                .orElse(ApiResponse.error("User not found"));
    }

    @PutMapping("/password")
    public ApiResponse<String> changePassword(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        String newPassword = request.get("newPassword");
        
        if (newPassword == null || newPassword.length() < 6) {
            return ApiResponse.error("Password must be at least 6 characters");
        }

        User user = userService.updateUser(userId, null, newPassword);
        if (user != null) {
            return ApiResponse.success("Password updated successfully");
        }
        return ApiResponse.error("Failed to update password");
    }

    @GetMapping("/login-history")
    public ApiResponse<List<LoginHistory>> getLoginHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<LoginHistory> history = userService.getLoginHistory(userId, 10);
        return ApiResponse.success(history);
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<LoginHistory> history = userService.getLoginHistory(userId, 0);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLogins", history.size());
        if (!history.isEmpty()) {
            stats.put("lastLogin", history.get(0).getLoginTime());
            stats.put("lastIp", history.get(0).getLoginIp());
        }
        
        return ApiResponse.success(stats);
    }
}
