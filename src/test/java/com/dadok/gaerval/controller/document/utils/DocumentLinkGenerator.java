package com.dadok.gaerval.controller.document.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface DocumentLinkGenerator {

	static String generateLinkCode(DocUrl docUrl) {
		return String.format("link:common/%s.html[%s %s,role=\"popup\"]", docUrl.pageId, docUrl.text, "코드");
	}

	static String generateText(DocUrl docUrl) {
		return String.format("%s %s", docUrl.text, "코드명 및 설명");
	}

	@RequiredArgsConstructor
	@Getter
	enum DocUrl {
		AUTH_PROVIDER("authProvider", "oauth2 제공자."),
		GENDER("gender", "성별"),
		JOB_GROUP("jobGroup", "직군"),
		JOB_NAME("jobName", "직업"),
		BOOKSHELF_ITEM_TYPE("bookshelfItemType", "책장 아이템 타입"),
		SORT_DIRECTION("sortDirection", "정렬 방식"),
		GROUP_SEARCH_OPTION("groupSearchOption", "모임 검색 방식"),
		BEST_SELLER_SEARCH_RANGE("bestSellerSearchRange", "베스트셀러 검색 범위");
		private final String pageId;
		private final String text;
	}
}
