package com.mi.zhipuserver.mapper;

import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.mi.zhipuserver.model.dto.ChatMessage;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageRepository extends CrudRepository<ChatMessageMapper, ChatMessage> {
}