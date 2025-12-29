package com.example.project.service.impl;

import com.example.project.model.Reader;
import com.example.project.repository.ReaderRepository;
import com.example.project.service.ReaderService2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService2 {

    private final ReaderRepository readerRepository;

    @Override
    public void updateAvatar(Long readerId, String avatarUrl) {
        Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new RuntimeException("Reader không tồn tại"));
        reader.setAvatarUrl(avatarUrl);
        readerRepository.save(reader);
    }
}
