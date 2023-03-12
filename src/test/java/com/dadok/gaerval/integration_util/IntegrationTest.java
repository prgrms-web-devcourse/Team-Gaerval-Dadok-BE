package com.dadok.gaerval.integration_util;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.auth.repository.RefreshTokenRepository;
import com.dadok.gaerval.domain.auth.service.RefreshTokenService;
import com.dadok.gaerval.domain.book.repository.BookCommentRepository;
import com.dadok.gaerval.domain.book.repository.BookRepository;
import com.dadok.gaerval.domain.book_group.repository.BookGroupCommentRepository;
import com.dadok.gaerval.domain.book_group.repository.BookGroupRepository;
import com.dadok.gaerval.domain.book_group.repository.GroupMemberRepository;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfRepository;
import com.dadok.gaerval.domain.bookshelf.service.BookshelfService;
import com.dadok.gaerval.domain.job.repository.JobRepository;
import com.dadok.gaerval.domain.job.service.JobService;
import com.dadok.gaerval.domain.user.entity.Authority;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.repository.AuthorityRepository;
import com.dadok.gaerval.domain.user.repository.UserRepository;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.config.jpa.JpaConfig;
import com.dadok.gaerval.global.config.p6spy.P6spyConfiguration;
import com.github.gavlyukovskiy.boot.jdbc.decorator.DataSourceDecoratorAutoConfiguration;
import com.google.common.base.CaseFormat;

@SpringBootTest(properties = "spring.jpa.show-sql=false")
@ImportAutoConfiguration(DataSourceDecoratorAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({P6spyConfiguration.class, JpaConfig.class})
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
public abstract class IntegrationTest {

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected UserService userService;

	@Autowired
	protected JobService jobService;

	@Autowired
	protected JobRepository jobRepository;

	@Autowired
	protected AuthorityRepository authorityRepository;

	@Autowired
	protected BookshelfRepository bookshelfRepository;

	@Autowired
	protected BookshelfService bookshelfService;

	@Autowired
	protected BookRepository bookRepository;

	@Autowired
	protected BookCommentRepository bookCommentRepository;

	@Autowired
	protected BookGroupRepository bookGroupRepository;

	@Autowired
	protected GroupMemberRepository groupMemberRepository;

	protected Authority getAuthority(Role role) {
		return this.authorityRepository.getReferenceById(role);
	}

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected BookGroupCommentRepository groupCommentRepository;

	@Autowired
	protected RefreshTokenService refreshTokenService;

	@Autowired
	protected RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	protected void redisCleanUp() {
		RedisConnection connection = redisConnectionFactory.getConnection();
		connection.execute("flushall");
		connection.close();
	}

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	protected void databaseCleanUp() {
		List<String> tableNames = entityManager.getMetamodel().getEntities().stream()
			.filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
			.map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName()))
			.collect(Collectors.toList());

		entityManager.flush();
		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0;").executeUpdate();

		for (String tableName : tableNames) {
			entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
			if (tableName.equals("invite_code")) {
				continue;
			}
			entityManager.createNativeQuery("ALTER TABLE " + tableName + " AUTO_INCREMENT = 1").executeUpdate();
		}

		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1;").executeUpdate();
	}
}
