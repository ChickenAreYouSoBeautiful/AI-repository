package com.mi.zhipuserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mi.zhipuserver.model.dto.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}


