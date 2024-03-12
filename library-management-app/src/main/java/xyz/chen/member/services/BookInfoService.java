package xyz.chen.member.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.chen.member.entity.BookInfo;
import xyz.chen.member.repository.BookInfoRepository;

@Service
public class BookInfoService extends ServiceImpl<BookInfoRepository, BookInfo> {
}
