package com.mi.zhipuserver.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author mi
 * @data 2025/4/18 14:32
 * @version 1.0
 */
@Data
public class InMemory implements Serializable {
    /**
     * 描述词
     */
    private String prompt;

    /**
     * 会话id
     */
    private String chatId;
}
