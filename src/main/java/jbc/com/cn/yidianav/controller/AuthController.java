package jbc.com.cn.yidianav.controller;

import jbc.com.cn.yidianav.dto.ApiResponse;
import jbc.com.cn.yidianav.dto.LoginRequest;
import jbc.com.cn.yidianav.dto.LoginResponse;
import jbc.com.cn.yidianav.entity.User;
import jbc.com.cn.yidianav.service.UserService;
import jbc.com.cn.yidianav.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Optional<User> userOpt = userService.findByUsername(request.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(request.getPassword())) {
                String token = jwtUtil.generateToken(user.getUsername(), user.getId());

                String ip = getClientIp(httpRequest);
                String userAgent = httpRequest.getHeader("User-Agent");
                userService.recordLogin(user.getId(), ip, userAgent);

                LoginResponse response = new LoginResponse(token, user.getUsername(), user.getId());
                return ApiResponse.success(response);
            }
        }

        return ApiResponse.error("Invalid username or password");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
