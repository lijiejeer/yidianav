package jbc.com.cn.yidianav.service;

import jbc.com.cn.yidianav.entity.FriendLink;
import jbc.com.cn.yidianav.repository.FriendLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendLinkService {

    @Autowired
    private FriendLinkRepository friendLinkRepository;

    public List<FriendLink> getAllFriendLinks() {
        return friendLinkRepository.findAllByOrderBySortOrder();
    }

    public Optional<FriendLink> getFriendLinkById(Long id) {
        return friendLinkRepository.findById(id);
    }

    public FriendLink createFriendLink(FriendLink friendLink) {
        return friendLinkRepository.save(friendLink);
    }

    public FriendLink updateFriendLink(Long id, FriendLink linkDetails) {
        Optional<FriendLink> linkOpt = friendLinkRepository.findById(id);
        if (linkOpt.isPresent()) {
            FriendLink link = linkOpt.get();
            link.setName(linkDetails.getName());
            link.setUrl(linkDetails.getUrl());
            link.setLogo(linkDetails.getLogo());
            link.setDescription(linkDetails.getDescription());
            link.setSortOrder(linkDetails.getSortOrder());
            return friendLinkRepository.save(link);
        }
        return null;
    }

    public boolean deleteFriendLink(Long id) {
        if (friendLinkRepository.existsById(id)) {
            friendLinkRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
