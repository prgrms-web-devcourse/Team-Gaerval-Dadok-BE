package com.dadok.gaerval.domain.book.dto.response;

import java.util.Arrays;
import java.util.Objects;

public record OriginalBookData(
	String contents,
	String datetime,
	String isbn,
	Long price,
	String publisher,
	Long salePrice,
	String status,
	String thumbnail,
	String url,
	String[] authors,
	String[] translators
	) {

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OriginalBookData that = (OriginalBookData) o;
		return Objects.equals(contents, that.contents) &&
			Objects.equals(datetime, that.datetime) &&
			Objects.equals(isbn, that.isbn) &&
			Objects.equals(price, that.price) &&
			Objects.equals(publisher, that.publisher) &&
			Objects.equals(salePrice, that.salePrice) &&
			Objects.equals(status, that.status) &&
			Objects.equals(thumbnail, that.thumbnail) &&
			Objects.equals(url, that.url) &&
			Arrays.equals(authors, that.authors) &&
			Arrays.equals(translators, that.translators);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(contents, datetime, isbn, price, publisher, salePrice, status, thumbnail, url);
		result = 31 * result + Arrays.hashCode(authors);
		result = 31 * result + Arrays.hashCode(translators);
		return result;
	}

	@Override
	public String toString() {
		return "OriginalBookData{" +
			"contents='" + contents + '\'' +
			", dateTime='" + datetime + '\'' +
			", isbn='" + isbn + '\'' +
			", price=" + price +
			", publisher='" + publisher + '\'' +
			", salePrice=" + salePrice +
			", status='" + status + '\'' +
			", thumbnail='" + thumbnail + '\'' +
			", url='" + url + '\'' +
			", authors=" + Arrays.toString(authors) +
			", translators=" + Arrays.toString(translators) +
			'}';
	}
}
