package com.mi.zhipuserver.controller;

import com.mi.zhipuserver.mapper.ChatMessageRepository;
import com.mi.zhipuserver.memory.DatabaseChatMemory;
import com.mi.zhipuserver.model.dto.InMemory;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.annotations.Result;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;


@RestController
@RequestMapping("/zhipuai/chat-client")
public class ZhiPuAiChatClientController {

	private static final String DEFAULT_PROMPT = "你好，介绍下你自己！";

	private final ChatClient zhipuAiChatClient;

	private final ChatModel chatModel;

	@Resource
	private ChatMessageRepository chatMessageRepository;

	public ZhiPuAiChatClientController(@Qualifier("zhiPuAiChatModel") ChatModel chatModel) {

		this.chatModel = chatModel;

		// 构造时，可以设置 ChatClient 的参数
		// {@link org.springframework.ai.chat.client.ChatClient};
		this.zhipuAiChatClient = ChatClient.builder(chatModel)
				// 实现 Chat Memory 的 Advisor
				// 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
				.defaultAdvisors(
						new MessageChatMemoryAdvisor(new InMemoryChatMemory())
				)
				// 实现 Logger 的 Advisor
				.defaultAdvisors(
						new SimpleLoggerAdvisor()
				)
				// 设置 ChatClient 中 ChatModel 的 Options 参数
				.defaultOptions(
						ZhiPuAiChatOptions.builder()
								.topP(0.7)
								.build()
				)
				.build();
	}

	/**
	 * Spring AI 提供的基于内存的 Chat Memory 实现
	 */
	@PostMapping("/in-memory")
	public Flux<String> memory(
			@RequestBody InMemory inMemory,
			HttpServletResponse response
	) {
		String chatId = inMemory.getChatId();
		String prompt = inMemory.getPrompt();

		response.setCharacterEncoding("UTF-8");

		return zhipuAiChatClient.prompt(prompt).advisors(
				new MessageChatMemoryAdvisor(
						new InMemoryChatMemory())
		).advisors(
				a -> a
						.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
						.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
		).stream().content();
	}

	/**
	 * Spring AI 提供的基于内存的 Chat Memory 实现
	 */
	@PostMapping("/in-mysql")
	public Flux<String> mysql(
			@RequestBody InMemory inMemory,
			HttpServletResponse response
	) {
		String chatId = inMemory.getChatId();
		String prompt = inMemory.getPrompt();

		response.setCharacterEncoding("UTF-8");

		return zhipuAiChatClient.prompt(prompt).advisors(
				new MessageChatMemoryAdvisor(new DatabaseChatMemory(chatMessageRepository))
		).advisors(
				a -> a
						.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
						.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
		).stream().content();
	}

	// 也可以使用如下的方式注入 ChatClient
	// public OpenAIChatClientController(ChatClient.Builder chatClientBuilder) {
	//
	//  	this.openAiChatClient = chatClientBuilder.build();
	// }

	/**
	 * ChatClient 简单调用
	 */
	@GetMapping("/simple/chat")
	public String simpleChat() {

		return zhipuAiChatClient.prompt(DEFAULT_PROMPT).options(ChatOptions.builder().model("glm-4-flash").build()).call().content();
	}

	/**
	 * ChatClient 流式调用
	 */
	@GetMapping("/stream/chat")
	public Flux<String> streamChat(HttpServletResponse response) {

		response.setCharacterEncoding("UTF-8");
		return zhipuAiChatClient.prompt(DEFAULT_PROMPT).stream().content();
	}

}
