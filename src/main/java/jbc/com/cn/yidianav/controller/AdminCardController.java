package jbc.com.cn.yidianav.controller;

import jbc.com.cn.yidianav.dto.ApiResponse;
import jbc.com.cn.yidianav.entity.Card;
import jbc.com.cn.yidianav.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/cards")
public class AdminCardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    public ApiResponse<List<Card>> getAllCards() {
        return ApiResponse.success(cardService.getAllCards());
    }

    @GetMapping("/{id}")
    public ApiResponse<Card> getCardById(@PathVariable Long id) {
        Optional<Card> card = cardService.getCardById(id);
        return card.map(ApiResponse::success)
                .orElse(ApiResponse.error("Card not found"));
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<Card>> getCardsByCategory(@PathVariable Long categoryId) {
        return ApiResponse.success(cardService.getCardsByCategory(categoryId));
    }

    @PostMapping
    public ApiResponse<Card> createCard(@RequestBody Card card) {
        Card created = cardService.createCard(card);
        return ApiResponse.success("Card created successfully", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<Card> updateCard(@PathVariable Long id, @RequestBody Card card) {
        Card updated = cardService.updateCard(id, card);
        if (updated != null) {
            return ApiResponse.success("Card updated successfully", updated);
        }
        return ApiResponse.error("Card not found");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCard(@PathVariable Long id) {
        if (cardService.deleteCard(id)) {
            return ApiResponse.success("Card deleted successfully");
        }
        return ApiResponse.error("Card not found");
    }

    @PostMapping("/parse")
    public ApiResponse<Map<String, String>> parseWebsite(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        if (url == null || url.isEmpty()) {
            return ApiResponse.error("URL is required");
        }
        Map<String, String> info = cardService.parseWebsiteInfo(url);
        if (info.containsKey("error")) {
            return ApiResponse.error(info.get("error"));
        }
        return ApiResponse.success(info);
    }
}
