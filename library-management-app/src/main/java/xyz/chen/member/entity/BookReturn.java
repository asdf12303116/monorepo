package xyz.chen.member.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xyz.chen.commons.base.BaseEntity;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@TableName("t_book_return")
@Data
@NoArgsConstructor
public class BookReturn extends BaseEntity {
    private Long userId;
    private Long bookId;
    private Long borrowId;
    private LocalDateTime returnDate;
}
