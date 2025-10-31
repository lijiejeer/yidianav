package jbc.com.cn.yidianav.controller;

import jbc.com.cn.yidianav.dto.ApiResponse;
import jbc.com.cn.yidianav.entity.FriendLink;
import jbc.com.cn.yidianav.service.FriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/friendlinks")
public class AdminFriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    @GetMapping
    public ApiResponse<List<FriendLink>> getAllFriendLinks() {
        return ApiResponse.success(friendLinkService.getAllFriendLinks());
    }

    @GetMapping("/{id}")
    public ApiResponse<FriendLink> getFriendLinkById(@PathVariable Long id) {
        Optional<FriendLink> link = friendLinkService.getFriendLinkById(id);
        return link.map(ApiResponse::success)
                .orElse(ApiResponse.error("Friend link not found"));
    }

    @PostMapping
    public ApiResponse<FriendLink> createFriendLink(@RequestBody FriendLink link) {
        FriendLink created = friendLinkService.createFriendLink(link);
        return ApiResponse.success("Friend link created successfully", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<FriendLink> updateFriendLink(@PathVariable Long id, @RequestBody FriendLink link) {
        FriendLink updated = friendLinkService.updateFriendLink(id, link);
        if (updated != null) {
            return ApiResponse.success("Friend link updated successfully", updated);
        }
        return ApiResponse.error("Friend link not found");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteFriendLink(@PathVariable Long id) {
        if (friendLinkService.deleteFriendLink(id)) {
            return ApiResponse.success("Friend link deleted successfully");
        }
        return ApiResponse.error("Friend link not found");
    }
}
