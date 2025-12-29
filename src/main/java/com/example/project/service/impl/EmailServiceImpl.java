package com.example.project.service.impl;

import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import com.example.project.service.EmailService;
import com.example.project.util.SendEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final SendEmail sendEmail;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendLibrarianAccountApproved(Librarian librarian, String rawPassword) {

        String subject = "[THỦ THƯ] Tài khoản thư viện của bạn đã được phê duyệt";

        String body = """
                Xin chào %s,

                Tài khoản THỦ THƯ của bạn đã được phê duyệt thành công!

                Tên đăng nhập: %s
                Mật khẩu: %s

                Vui lòng đăng nhập và đổi mật khẩu ngay sau lần sử dụng đầu tiên.

                Thân mến,
                Phòng Thư viện
                """.formatted(
                librarian.getFullName(),
                librarian.getUsername(),
                rawPassword
        );

        sendEmail.sendMail("huongcao.seee@gmail.com", subject, body);
    }

    @Override
    public void sendReaderAccountApproved(Reader reader, String rawPassword) {

        String subject = "[ĐỘC GIẢ] Tài khoản thư viện của bạn đã được kích hoạt";

        String body = """
                Xin chào %s,

                Tài khoản ĐỘC GIẢ của bạn đã được kích hoạt thành công!

                Tên đăng nhập: %s
                Mật khẩu: %s

                Vui lòng đăng nhập và đổi mật khẩu ngay sau khi sử dụng lần đầu.\n\n

                Thân mến,\nPhòng Thư viện
                """.formatted(
                reader.getFullName(),
                reader.getUsername(),
                rawPassword
        );

        sendEmail.sendMail("huongcao.seee@gmail.com", subject, body);
    }
}
