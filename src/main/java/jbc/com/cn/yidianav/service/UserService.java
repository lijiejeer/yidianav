package jbc.com.cn.yidianav.service;

import jbc.com.cn.yidianav.entity.LoginHistory;
import jbc.com.cn.yidianav.entity.User;
import jbc.com.cn.yidianav.repository.LoginHistoryRepository;
import jbc.com.cn.yidianav.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        return userRepository.save(user);
    }

    public User updateUser(Long id, String email, String password) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (email != null) {
                user.setEmail(email);
            }
            if (password != null) {
                user.setPassword(password);
            }
            return userRepository.save(user);
        }
        return null;
    }

    public void recordLogin(Long userId, String ip, String userAgent) {
        LoginHistory history = new LoginHistory();
        history.setUserId(userId);
        history.setLoginIp(ip);
        history.setUserAgent(userAgent);
        history.setLoginTime(LocalDateTime.now());
        loginHistoryRepository.save(history);
    }

    public List<LoginHistory> getLoginHistory(Long userId, int limit) {
        if (limit > 0) {
            return loginHistoryRepository.findTop10ByUserIdOrderByLoginTimeDesc(userId);
        }
        return loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(userId);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
