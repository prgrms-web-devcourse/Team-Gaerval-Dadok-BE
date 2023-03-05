package com.dadok.gaerval.domain.book_group.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.repository.BookGroupRepository;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

@ExtendWith(MockitoExtension.class)
class DefaultBookGroupServiceSliceTest {

	@InjectMocks
	private DefaultBookGroupService defaultBookGroupService;

	@Mock
	private BookGroupRepository bookGroupRepository;

	@Mock
	private BookService bookService;

	@Mock
	private UserService userService;

	@DisplayName("findAllBookGroups - 모임 리스트를 조회한다.")
	@Test
	void findAllBookGroups() {
		//given
		BookGroupSearchRequest request = new BookGroupSearchRequest(10, null, SortDirection.DESC);

		List<BookGroupResponse> responses = List.of(
			new BookGroupResponse(
				999L, "모임999", "모임 999에용", 2L, 100L, 4452L,
				"http://bookImageUrl1.com", 1341234L, "http://ownerProfile1.com",
				"나는모임장이다1"
			),

			new BookGroupResponse(
				997L, "모임997", "모임 997에용", 5L, 2L, 2083L,
				"http://bookImageUrl1.com", 941234L, "http://ownerProfile1.com",
				"나는모임장이다1"
			),

			new BookGroupResponse(
				995L, "모임995", "모임 995에용", 5L, 0L, 442L,
				"http://bookImageUrl1.com", 1334L, "http://ownerProfile1.com",
				"나는모임장이다1"
			)
			,
			new BookGroupResponse(
				994L, "모임994", "모임 994에용", 3L, 30L, 44L,
				"http://bookImageUrl1.com", 1341234L, "http://ownerProfile1.com",
				"나는모임장이다1"
			)
			,
			new BookGroupResponse(
				993L, "모임993", "모임 993에용", 4L, 4000L, 913452L,
				"http://bookImageUrl1.com", 123234L, "http://ownerProfile1.com",
				"나는모임장이다1"
			)
		);

		BookGroupResponses bookGroupResponses = new BookGroupResponses(
			QueryDslUtil.toSlice(responses, PageRequest.of(0, 10)));

		given(bookGroupRepository.findAllBy(request))
			.willReturn(bookGroupResponses);
		//when
		BookGroupResponses bookGroups = defaultBookGroupService.findAllBookGroups(request);
		//then
		assertThat(bookGroups)
			.hasFieldOrPropertyWithValue("isFirst", true)
			.hasFieldOrPropertyWithValue("isLast", true)
			.hasFieldOrPropertyWithValue("hasNext", false)
			.hasFieldOrPropertyWithValue("count", 5)
			.hasFieldOrPropertyWithValue("isEmpty", false);

		assertThat(bookGroups.bookGroups()).isSortedAccordingTo(
			(o1, o2) -> o2.getBookGroupId().compareTo(o1.getBookGroupId()));

	}

	@DisplayName("createBookGroup - 모임 생성에 성공한다.")
	@Test
	void createBookGroup_success() {
		// Given
		var user = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(user, "id", 1L);
		var book = BookObjectProvider.createRequiredFieldBook();
		var bookCreateRequest = new BookCreateRequest(book.getTitle(), book.getAuthor(), book.getIsbn(),
			book.getContents(), book.getUrl(), book.getImageUrl(), book.getPublisher(), book.getApiProvider());
		BookGroupCreateRequest request = new BookGroupCreateRequest(bookCreateRequest,
			"소모임 화이팅", LocalDate.now(), LocalDate.now(), 5, "우리끼리 옹기종기", true
		);
		BookGroup bookGroup = BookGroup.create(user.getId(), book, request.startDate(), request.endDate(),
			request.maxMemberCount(), request.title(), request.introduce(), request.isPublic());
		ReflectionTestUtils.setField(bookGroup, "id", 1L);

		given(userService.getById(user.getId()))
			.willReturn(user);
		given(bookService.createBook(bookCreateRequest))
			.willReturn(book);
		given(bookGroupRepository.save(any()))
			.willReturn(bookGroup);

		// When
		var bookGroupId = defaultBookGroupService.createBookGroup(user.getId(), request);

		// Then
		verify(bookService).createBook(bookCreateRequest);
		verify(bookGroupRepository).save(any());
		assertThat(bookGroupId).isEqualTo(1L);
	}

}