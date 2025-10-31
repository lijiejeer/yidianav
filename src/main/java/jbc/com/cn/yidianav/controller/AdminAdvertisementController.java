package jbc.com.cn.yidianav.controller;

import jbc.com.cn.yidianav.dto.ApiResponse;
import jbc.com.cn.yidianav.entity.Advertisement;
import jbc.com.cn.yidianav.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/ads")
public class AdminAdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    @GetMapping
    public ApiResponse<List<Advertisement>> getAllAds() {
        return ApiResponse.success(advertisementService.getAllAds());
    }

    @GetMapping("/{id}")
    public ApiResponse<Advertisement> getAdById(@PathVariable Long id) {
        Optional<Advertisement> ad = advertisementService.getAdById(id);
        return ad.map(ApiResponse::success)
                .orElse(ApiResponse.error("Advertisement not found"));
    }

    @PostMapping
    public ApiResponse<Advertisement> createAd(@RequestBody Advertisement ad) {
        Advertisement created = advertisementService.createAd(ad);
        return ApiResponse.success("Advertisement created successfully", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<Advertisement> updateAd(@PathVariable Long id, @RequestBody Advertisement ad) {
        Advertisement updated = advertisementService.updateAd(id, ad);
        if (updated != null) {
            return ApiResponse.success("Advertisement updated successfully", updated);
        }
        return ApiResponse.error("Advertisement not found");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteAd(@PathVariable Long id) {
        if (advertisementService.deleteAd(id)) {
            return ApiResponse.success("Advertisement deleted successfully");
        }
        return ApiResponse.error("Advertisement not found");
    }
}
