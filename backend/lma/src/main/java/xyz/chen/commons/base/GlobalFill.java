package xyz.chen.commons.base;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.chen.commons.config.ConfigData;
import xyz.chen.member.utils.UserUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Slf4j
public class GlobalFill implements MetaObjectHandler {

    private UserInfo userInfo;

    @Autowired
    private ConfigData configData;


    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createUserName", String.class, getUserInfo().userName());
        this.strictInsertFill(metaObject, "createUserId", Long.class, getUserInfo().userId());
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateUserName", String.class, getUserInfo().userName());
        this.strictUpdateFill(metaObject, "updateUserId", Long.class, getUserInfo().userId());
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }

    private UserInfo getUserInfo() {
        if (Objects.nonNull(this.userInfo)) {
            return this.userInfo;
        } else {
            UserInfo userInfo = UserUtils.getUserInfo(configData.getDefaultSystemName(), configData.getDefaultSystemId());
            this.userInfo = userInfo;
            return userInfo;
        }

    }


}
