package xyz.chen.member.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.chen.member.entity.BookBorrow;
import xyz.chen.member.repository.BookBorrowRepository;

@Service
public class BookBorrowService extends ServiceImpl<BookBorrowRepository, BookBorrow> {
}
