package xyz.chen.commons.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageInfo {
    private Long pageNum;
    private Long pageSize;
    private Long total;

    public PageInfo(Page<?> page) {
        this.pageNum = page.getPages();
        this.pageSize = page.getSize();
        this.total = page.getTotal();
    }
}


