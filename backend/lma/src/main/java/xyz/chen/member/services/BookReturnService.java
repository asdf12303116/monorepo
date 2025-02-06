package xyz.chen.member.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.chen.member.entity.BookReturn;
import xyz.chen.member.repository.BookReturnRepository;

@Service
public class BookReturnService extends ServiceImpl<BookReturnRepository, BookReturn> {
}
