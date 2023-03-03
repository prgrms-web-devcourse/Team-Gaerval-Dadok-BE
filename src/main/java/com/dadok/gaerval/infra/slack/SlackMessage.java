package com.dadok.gaerval.infra.slack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.global.util.CommonValidator;
import com.slack.api.model.block.DividerBlock;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.ImageBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.block.composition.TextObject;
import com.slack.api.model.block.element.ImageElement;

import lombok.Getter;

public class SlackMessage {

	private List<LayoutBlock> blocks = new ArrayList<>();

	protected SlackMessage() {
		this.blocks = new ArrayList<>();
	}

	public static MessageBuilder builder() {
		return new MessageBuilder();
	}

	public List<LayoutBlock> blocks() {
		return blocks;
	}

	protected static class MessageBuilder {

		private SlackMessage slackMessage;

		public MessageBuilder() {
			this.slackMessage = new SlackMessage();
		}

		public MessageBuilder titleBlock(String headerTitle) {
			CommonValidator.validateBlank(headerTitle, "headerTitle");

			this.slackMessage.blocks.add(headerBlock(headerTitle));
			return this;
		}

		public MessageBuilder sectionBlock(List<TextObject> fields) {
			LayoutBlock section = section(fields);
			this.slackMessage.blocks.add(section);
			return this;
		}

		public MessageBuilder sectionBlock(TextObject field) {
			LayoutBlock section = section(field);
			this.slackMessage.blocks.add(section);
			return this;
		}

		public MessageBuilder dividerBlock() {
			this.slackMessage.blocks.add(divider());
			return this;
		}

		public MessageBuilder imageBlock(String sectionText, String imageUrl, String imageText) {
			LayoutBlock section = section(SlackMessage.markdownField(sectionText));
			LayoutBlock layoutBlock = sectionWithImage(SlackMessage.markdownField(sectionText), imageUrl, imageText);
			this.slackMessage.blocks.add(layoutBlock);
			return this;
		}

		public MessageBuilder image(String imageUrl) {
			ImageBlock image = SlackMessage.image(imageUrl);
			this.slackMessage.blocks.add(image);
			return this;
		}

		public MessageBuilder image(String imageUrl, String imageText) {
			ImageBlock image = SlackMessage.image(imageUrl, imageText);
			this.slackMessage.blocks.add(image);
			return this;
		}

		public SlackMessage build() {
			if (slackMessage.blocks() == null || slackMessage.blocks().isEmpty()) {
				throw new IllegalArgumentException("메시지가 비어있습니다.");
			}

			return slackMessage;
		}

	}

	public static LayoutBlock headerBlock(String text) {
		return HeaderBlock.builder()
			.text(new PlainTextObject(text, true))
			.build();
	}

	public static LayoutBlock section(List<TextObject> fields) {
		CommonValidator.validateNotnull(fields, "field in section");

		return SectionBlock.builder()
			.fields(fields)
			.build();
	}

	public static LayoutBlock section(TextObject field) {
		CommonValidator.validateNotnull(field, "field in section");

		return SectionBlock.builder()
			.text(field)
			.build();
	}

	public static LayoutBlock sectionWithImage(TextObject field, String imageUrl, String imageText) {
		CommonValidator.validateNotnull(field, "field in section");

		ImageBlock image = image(imageUrl, imageText);

		ImageElement imageElement = ImageElement.builder()
			.imageUrl(imageUrl)
			.altText(imageText)
			.build();

		return SectionBlock.builder()
			.text(field)
			.accessory(imageElement)
			.build();
	}

	public static LayoutBlock divider() {
		return new DividerBlock();
	}

	public static TextObject markdownField(String fieldTitle, String text) {
		return MarkdownTextObject.builder()
			.text("*`" + fieldTitle + "`:*\n\n```" + text + "```")
			.verbatim(true)
			.build();
	}

	public static TextObject plainField(String fieldTitle, String text) {
		return PlainTextObject.builder()
			.text("*`" + fieldTitle + "` : *\n\n```" + text + "```")
			.emoji(true).build();
	}

	public static TextObject markdownField(String text) {
		return MarkdownTextObject.builder()
			.text(text)
			.verbatim(true)
			.build();
	}

	public static TextObject plainField(String text) {
		return PlainTextObject.builder()
			.text(text)
			.emoji(true).build();
	}

	public static ImageBlock image(String imageUrl) {
		return ImageBlock.builder()
			.imageUrl(imageUrl)
			.altText("image")
			.build();
	}

	public static ImageBlock image(String imageUrl, String imageText) {
		return ImageBlock.builder()
			.imageUrl(imageUrl)
			.altText(imageText)
			.build();
	}

	public static SlackMessage errorMessage(String logLevel, Exception e, String requestUri) {
		String cause = e.getClass().toString();

		if (e.getCause() != null) {
			cause = e.getCause().toString();
		}
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초 - ss"));

		List<TextObject> textObjects = List.of(
			SlackMessage.markdownField("예외 명", e.getClass().getName()),
			SlackMessage.markdownField("예외 발생시간 ", time),
			SlackMessage.markdownField("request_path", requestUri),
			SlackMessage.markdownField("로그 레벨", logLevel),
			SlackMessage.markdownField("예외 메시지", e.getMessage()),
			SlackMessage.markdownField("cause", cause)
		);

		return SlackMessage.builder()
			.dividerBlock()
			.titleBlock("⚠️ - " + LogTitle.of(logLevel).getText())
			.titleBlock("⚠️ '" + e.getClass()
				.getSimpleName() + "' 예외 발생 " + "❗")
			.dividerBlock()
			.sectionBlock(textObjects)
			.build();
	}

	@Getter
	public enum LogTitle {
		ERROR("\uD83D\uDD34 error Level \uD83D\uDD34"), // 빨강
		WARN("\uD83D\uDFE0 warn Level \uD83D\uDFE0"), // 주황
		INFO("\uD83D\uDFE2 info Level \uD83D\uDFE2"), // 초록
		DEBUG("\uD83D\uDFE3 debug Level \uD83D\uDFE3"), // 보라
		TRACE("\uD83D\uDD35 trace Level \uD83D\uDD35"); // 연초록

		private final String text;

		LogTitle(String text) {
			this.text = text;
		}

		public static LogTitle of(String logStr) {
			return Arrays.stream(values())
				.filter(logTitle -> Objects.equals(logTitle.name(), logStr.toUpperCase()))
				.findFirst()
				.orElseThrow(() -> new InvalidArgumentException(logStr, "logStr in LogTitle"));
		}
	}
}
