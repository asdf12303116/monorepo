package xyz.chen.commons.base;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.chen.commons.config.ConfigData;

import java.time.LocalDateTime;

@Component
@Slf4j
public class GlobalFill implements MetaObjectHandler {

    @Autowired
    private ConfigData configData;



    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject,"createUserName",String.class,getUserInfo().userName());
        this.strictInsertFill(metaObject,"createUserId",Long.class,getUserInfo().userId());
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject,"updateUserName",String.class,getUserInfo().userName());
        this.strictUpdateFill(metaObject, "updateUserId",Long.class,getUserInfo().userId());
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }

    private userInfo getUserInfo(){
        userInfo userInfo;
        userInfo = new userInfo(configData.getDefaultSystemId(), configData.getDefaultSystemName());
        return  userInfo;
    }

    private record userInfo (Long userId, String userName) { }
}
