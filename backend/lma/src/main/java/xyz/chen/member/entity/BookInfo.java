package xyz.chen.member.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xyz.chen.commons.base.BaseEntity;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@TableName("t_book_info")
@Data
@NoArgsConstructor
public class BookInfo extends BaseEntity {
    private String bookName;
    private String author;
    private LocalDate publicationDate;
    private String isbn;
    private String description;
}
