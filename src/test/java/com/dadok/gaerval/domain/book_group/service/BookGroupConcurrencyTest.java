package com.dadok.gaerval.domain.book_group.service;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.repository.BookRepository;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupJoinRequest;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupMember;
import com.dadok.gaerval.domain.book_group.repository.BookGroupRepository;
import com.dadok.gaerval.domain.book_group.repository.GroupMemberRepository;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItem;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfItemRepository;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.repository.UserRepository;
import com.dadok.gaerval.global.config.jpa.JpaConfig;
import com.dadok.gaerval.global.config.p6spy.P6spyConfiguration;
import com.dadok.gaerval.testutil.TestTimeHolder;
import com.github.gavlyukovskiy.boot.jdbc.decorator.DataSourceDecoratorAutoConfiguration;

import lombok.extern.slf4j.Slf4j;


@Tag("Concurrency Integration Test")
@Sql({"/sql/job_data.sql", "/sql/bookgroup/data-book_group_service.sql"})
@Sql(scripts = {"/sql/clean_up.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Slf4j
@SpringBootTest(properties = "spring.jpa.show-sql=false")
@ImportAutoConfiguration(DataSourceDecoratorAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({P6spyConfiguration.class, JpaConfig.class})
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
class BookGroupConcurrencyTest {

	@Autowired
	private BookshelfRepository bookshelfRepository;

	@Autowired
	private GroupMemberRepository groupMemberRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BookGroupRepository bookGroupRepository;

	@Autowired
	private DefaultBookGroupService defaultBookGroupService;

	@Autowired
	private BookshelfItemRepository bookshelfItemRepository;

	private List<User> users;
	private Book book;
	private BookGroup bookGroup;

	@Autowired
	private PlatformTransactionManager platformTransactionManager;

	@BeforeEach
	void setUp() throws SQLException {
		this.book = bookRepository.findById(1L).get();
		this.users = userRepository.findAllById(LongStream.rangeClosed(1000, 1009).boxed().toList());
	}

	@DisplayName("인원제한이 5명인 모임에 1명만 참여중이고, 남은 4개의 자리에 9명이 동시에 신청하면 5명만 가입된다.")
	@Test
	void join_success_test() throws InterruptedException {
		//given
		int peopleCount = 9;
		User owner = users.get(0);

		BookGroup bookGroup = BookGroup.create(owner.getId(), book, LocalDate.now(), LocalDate.now().plusDays(7),
			5, "인원제한이 5명인 그룹", "인원제한이 5명인 그룹", false, null, null, true,
			new BCryptPasswordEncoder(), TestTimeHolder.now());
		this.bookGroup = bookGroupRepository.save(bookGroup);
		GroupMember groupMember = GroupMember.create(owner);
		bookGroup.addMember(groupMember, TestTimeHolder.now());
		groupMemberRepository.save(groupMember);
		TransactionStatus transaction = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			BookshelfItem bookshelfItem = BookshelfItem.create(bookshelfRepository.findByUserId(owner.getId()).get(),
				book);
			bookshelfItemRepository.save(bookshelfItem);
			platformTransactionManager.commit(transaction);
		} catch (Exception e) {
			platformTransactionManager.rollback(transaction);
		}
		CountDownLatch latch = new CountDownLatch(peopleCount);
		ExecutorService executorService = Executors.newFixedThreadPool(peopleCount);
		AtomicInteger failCount = new AtomicInteger();
		//when
		for (int i = 1; i <= peopleCount; i++) {
			User user = users.get(i);
			BookGroupJoinRequest bookGroupJoinRequest = new BookGroupJoinRequest(null);
			executorService.execute(() -> {
				try {
					defaultBookGroupService.join(bookGroup.getId(), user.getId(), bookGroupJoinRequest);
				} catch (Exception e) {
					log.info("exception : {}, message : {}", e.getClass().getName(), e.getMessage());
					log.info("fail user id: {}, email: {} ", user.getId(), user.getEmail());
					failCount.getAndIncrement();
				} finally {
					latch.countDown();
				}
			});
		}
		//then
		latch.await();
		Optional<BookGroup> bookGroupOptional = bookGroupRepository.findByIdWithGroupMembers(bookGroup.getId());

		assertTrue(bookGroupOptional.isPresent());
		BookGroup findBookGroup = bookGroupOptional.get();
		List<Bookshelf> bookshelves = bookshelfRepository.findAllByUserIds(
			findBookGroup.getGroupMembers().stream().map(bg -> bg.getUser().getId()).toList());

		List<BookshelfItem> bookshelfItems = bookshelfItemRepository.findAllByBookShelfIdsAndBookId(
			bookshelves.stream().map(
				Bookshelf::getId
			).collect(Collectors.toList()), book.getId());

		int memberCount = findBookGroup.getGroupMembers().size();

		assertEquals(findBookGroup.getMaxMemberCount(), memberCount);
		assertEquals(5, failCount.get());
		assertEquals(5, bookshelfItems.size());
	}

}
