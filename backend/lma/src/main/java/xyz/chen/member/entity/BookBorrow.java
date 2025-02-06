package xyz.chen.member.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xyz.chen.commons.base.BaseEntity;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@TableName("t_book_borrow")
@Data
@NoArgsConstructor
public class BookBorrow extends BaseEntity {
    private Long userId;
    private Long bookId;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private String status;
}
