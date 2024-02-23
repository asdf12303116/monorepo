package xyz.chen.commons.base;

import lombok.Data;

@Data
public class PageInfo {
    private Integer pageNum;
    private Integer pageSize;
    private Integer total;

}
