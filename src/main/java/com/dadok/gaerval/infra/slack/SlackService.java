package com.dadok.gaerval.infra.slack;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dadok.gaerval.global.error.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Info : 에러는 아니지만 주시해야할 것
 * Warning : 예외상황이긴 했지만 에러는 아닌 것
 * Error : 에러가 맞고 대응해야할 것
 * Fatal : 치명적인 것
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SlackService {

	private final Slack slack = Slack.getInstance();

	private final ObjectMapper objectMapper;

	@Value("${slack.oauth-token}")
	private String slackOauthToken;

	@Value("${slack.error-channel-id}")
	private String errorChannelId;

	public void sendMessage(String channelId, SlackMessage slackMessage) {

		try {
			ChatPostMessageResponse response = slack.methods()
				.chatPostMessage(builder -> builder.token(slackOauthToken)
					.channel(channelId)
					.blocks(slackMessage.blocks()));

			checkResponse(response);
		} catch (IOException | BusinessException | SlackApiException e) {
			e.printStackTrace();
			throw new SlackException(e.getMessage(), e);
		}

	}

	public void sendAsyncMessage(String channelId, SlackMessage slackMessage) {

		try {
			CompletableFuture<ChatPostMessageResponse> chatPostMessageResponseCompletableFuture = slack.methodsAsync()
				.chatPostMessage(builder -> builder.token(slackOauthToken)
					.channel(channelId)
					.blocks(slackMessage.blocks()));

			ChatPostMessageResponse response = chatPostMessageResponseCompletableFuture.get(10, TimeUnit.SECONDS);

			checkResponse(response);
		} catch (IOException | BusinessException e) {
			e.printStackTrace();
			throw new SlackException(e.getMessage(), e);
		} catch (ExecutionException | TimeoutException | InterruptedException e) {
			throw new SlackException(e.getMessage(), e);
		}

	}

	public void sendInfo(Exception e, String requestUri) {
		SlackMessage slackMessage = SlackMessage.errorMessage("info", e, requestUri);
		sendExceptionMessage(slackMessage);
	}

	public void sendError(Exception e, String requestUri) {
		SlackMessage slackMessage = SlackMessage.errorMessage("error", e, requestUri);
		sendExceptionMessage(slackMessage);
	}

	public void sendWarn(Exception e, String requestUri) {
		SlackMessage slackMessage = SlackMessage.errorMessage("warn", e, requestUri);
		sendExceptionMessage(slackMessage);
	}

	public void sendExceptionMessage(SlackMessage slackMessage) {
		sendAsyncMessage(this.errorChannelId, slackMessage);
	}

	private void checkResponse(ChatPostMessageResponse response) throws JsonProcessingException {

		if (!response.isOk()) {
			System.out.println(response.isOk());
			String parseResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
			log.warn("slack send error : \n" + parseResponse);
			throw new SlackException(parseResponse, new IllegalArgumentException(new RuntimeException()));
		}

	}

}
