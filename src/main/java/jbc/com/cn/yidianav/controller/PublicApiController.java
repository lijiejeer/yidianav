package jbc.com.cn.yidianav.controller;

import jbc.com.cn.yidianav.dto.ApiResponse;
import jbc.com.cn.yidianav.dto.CategoryDTO;
import jbc.com.cn.yidianav.entity.Advertisement;
import jbc.com.cn.yidianav.entity.Card;
import jbc.com.cn.yidianav.entity.FriendLink;
import jbc.com.cn.yidianav.service.AdvertisementService;
import jbc.com.cn.yidianav.service.CardService;
import jbc.com.cn.yidianav.service.CategoryService;
import jbc.com.cn.yidianav.service.FriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicApiController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CardService cardService;

    @Autowired
    private AdvertisementService advertisementService;

    @Autowired
    private FriendLinkService friendLinkService;

    @GetMapping("/categories")
    public ApiResponse<List<CategoryDTO>> getCategories() {
        return ApiResponse.success(categoryService.getTreeStructure());
    }

    @GetMapping("/cards")
    public ApiResponse<List<Card>> getAllCards() {
        return ApiResponse.success(cardService.getAllCards());
    }

    @GetMapping("/cards/category/{categoryId}")
    public ApiResponse<List<Card>> getCardsByCategory(@PathVariable Long categoryId) {
        return ApiResponse.success(cardService.getCardsByCategory(categoryId));
    }

    @GetMapping("/cards/search")
    public ApiResponse<List<Card>> searchCards(@RequestParam String keyword) {
        return ApiResponse.success(cardService.searchCards(keyword));
    }

    @GetMapping("/ads")
    public ApiResponse<List<Advertisement>> getAds() {
        return ApiResponse.success(advertisementService.getEnabledAds());
    }

    @GetMapping("/ads/{position}")
    public ApiResponse<List<Advertisement>> getAdsByPosition(@PathVariable String position) {
        return ApiResponse.success(advertisementService.getAdsByPosition(position));
    }

    @GetMapping("/friendlinks")
    public ApiResponse<List<FriendLink>> getFriendLinks() {
        return ApiResponse.success(friendLinkService.getAllFriendLinks());
    }
}
