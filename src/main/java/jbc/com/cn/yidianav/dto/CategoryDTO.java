package jbc.com.cn.yidianav.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String icon;
    private Long parentId;
    private Integer sortOrder;
    private List<CategoryDTO> children;
}
