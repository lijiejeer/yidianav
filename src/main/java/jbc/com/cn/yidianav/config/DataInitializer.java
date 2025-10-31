package jbc.com.cn.yidianav.config;

import jbc.com.cn.yidianav.entity.Category;
import jbc.com.cn.yidianav.entity.Card;
import jbc.com.cn.yidianav.entity.User;
import jbc.com.cn.yidianav.service.CategoryService;
import jbc.com.cn.yidianav.service.CardService;
import jbc.com.cn.yidianav.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CardService cardService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!userService.existsByUsername(adminUsername)) {
            userService.createUser(adminUsername, adminPassword, "admin@example.com");
            System.out.println("Default admin user created: " + adminUsername);
        }

        if (categoryService.getAllCategories().isEmpty()) {
            Category category1 = new Category();
            category1.setName("常用工具");
            category1.setIcon("fas fa-star");
            category1.setSortOrder(1);
            category1 = categoryService.createCategory(category1);

            Card card1 = new Card();
            card1.setName("百度");
            card1.setUrl("https://www.baidu.com");
            card1.setDescription("全球最大的中文搜索引擎");
            card1.setCategoryId(category1.getId());
            card1.setSortOrder(1);
            cardService.createCard(card1);

            Card card2 = new Card();
            card2.setName("Google");
            card2.setUrl("https://www.google.com");
            card2.setDescription("全球最大的搜索引擎");
            card2.setCategoryId(category1.getId());
            card2.setSortOrder(2);
            cardService.createCard(card2);

            Category category2 = new Category();
            category2.setName("开发工具");
            category2.setIcon("fas fa-code");
            category2.setSortOrder(2);
            category2 = categoryService.createCategory(category2);

            Card card3 = new Card();
            card3.setName("GitHub");
            card3.setUrl("https://github.com");
            card3.setDescription("全球最大的代码托管平台");
            card3.setCategoryId(category2.getId());
            card3.setSortOrder(1);
            cardService.createCard(card3);

            System.out.println("Sample data initialized");
        }
    }
}
