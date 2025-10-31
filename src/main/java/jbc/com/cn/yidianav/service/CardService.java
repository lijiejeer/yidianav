package jbc.com.cn.yidianav.service;

import jbc.com.cn.yidianav.entity.Card;
import jbc.com.cn.yidianav.repository.CardRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    public List<Card> getCardsByCategory(Long categoryId) {
        return cardRepository.findByCategoryIdOrderBySortOrder(categoryId);
    }

    public Optional<Card> getCardById(Long id) {
        return cardRepository.findById(id);
    }

    public Card createCard(Card card) {
        return cardRepository.save(card);
    }

    public Card updateCard(Long id, Card cardDetails) {
        Optional<Card> cardOpt = cardRepository.findById(id);
        if (cardOpt.isPresent()) {
            Card card = cardOpt.get();
            card.setName(cardDetails.getName());
            card.setUrl(cardDetails.getUrl());
            card.setLogo(cardDetails.getLogo());
            card.setDescription(cardDetails.getDescription());
            card.setCategoryId(cardDetails.getCategoryId());
            card.setSortOrder(cardDetails.getSortOrder());
            return cardRepository.save(card);
        }
        return null;
    }

    public boolean deleteCard(Long id) {
        if (cardRepository.existsById(id)) {
            cardRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Card> searchCards(String keyword) {
        return cardRepository.findByNameContainingOrDescriptionContaining(keyword, keyword);
    }

    public Map<String, String> parseWebsiteInfo(String url) {
        Map<String, String> info = new HashMap<>();
        try {
            Document doc = Jsoup.connect(url).timeout(5000).get();
            
            String title = doc.title();
            info.put("name", title);

            Element description = doc.select("meta[name=description]").first();
            if (description != null) {
                info.put("description", description.attr("content"));
            }

            Element icon = doc.select("link[rel~=icon]").first();
            if (icon != null) {
                String iconUrl = icon.attr("abs:href");
                info.put("logo", iconUrl);
            } else {
                String baseUrl = url.replaceAll("(https?://[^/]+).*", "$1");
                info.put("logo", baseUrl + "/favicon.ico");
            }

        } catch (Exception e) {
            info.put("error", "Failed to parse website: " + e.getMessage());
        }
        return info;
    }
}
