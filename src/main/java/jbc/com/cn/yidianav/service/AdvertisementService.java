package jbc.com.cn.yidianav.service;

import jbc.com.cn.yidianav.entity.Advertisement;
import jbc.com.cn.yidianav.repository.AdvertisementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdvertisementService {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    public List<Advertisement> getAllAds() {
        return advertisementRepository.findAll();
    }

    public List<Advertisement> getEnabledAds() {
        return advertisementRepository.findByEnabledTrueOrderBySortOrder();
    }

    public List<Advertisement> getAdsByPosition(String position) {
        return advertisementRepository.findByPositionAndEnabledTrueOrderBySortOrder(position);
    }

    public Optional<Advertisement> getAdById(Long id) {
        return advertisementRepository.findById(id);
    }

    public Advertisement createAd(Advertisement ad) {
        return advertisementRepository.save(ad);
    }

    public Advertisement updateAd(Long id, Advertisement adDetails) {
        Optional<Advertisement> adOpt = advertisementRepository.findById(id);
        if (adOpt.isPresent()) {
            Advertisement ad = adOpt.get();
            ad.setTitle(adDetails.getTitle());
            ad.setImageUrl(adDetails.getImageUrl());
            ad.setLinkUrl(adDetails.getLinkUrl());
            ad.setPosition(adDetails.getPosition());
            ad.setEnabled(adDetails.getEnabled());
            ad.setSortOrder(adDetails.getSortOrder());
            return advertisementRepository.save(ad);
        }
        return null;
    }

    public boolean deleteAd(Long id) {
        if (advertisementRepository.existsById(id)) {
            advertisementRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
