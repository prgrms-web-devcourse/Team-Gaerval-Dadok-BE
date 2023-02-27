package com.dadok.gaerval.controller.document.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface DocumentLinkGenerator {

    static String generateLinkCode(DocUrl docUrl) {
        return String.format("link:common/%s.html[%s %s,role=\"popup\"]", docUrl.pageId, docUrl.text, "코드");
    }

    static String generateText(DocUrl docUrl) {
        return String.format("%s %s", docUrl.text, "코드명");
    }

    @RequiredArgsConstructor
    @Getter
    enum DocUrl {
        GENDER("gender", "성별"),
        JOB_GROUP("jobGroup", "직군"),
        JOB_NAME("jobName", "직업");
        private final String pageId;
        private final String text;
    }
}
