package xyz.chen.commons.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public abstract class BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;


    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private String createUserName;

    @TableField(fill = FieldFill.INSERT)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createTime;


    @TableField(fill = FieldFill.UPDATE)
    private Long updateUserId;

    @TableField(fill = FieldFill.UPDATE)
    private String updateUserName;

    @TableField(fill = FieldFill.UPDATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updateTime;

    private Boolean deleted = false;
}
