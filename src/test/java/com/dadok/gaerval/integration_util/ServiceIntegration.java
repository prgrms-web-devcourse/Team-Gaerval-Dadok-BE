package com.dadok.gaerval.integration_util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

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


@SpringBootTest(properties = "spring.jpa.show-sql=false")
@ImportAutoConfiguration(DataSourceDecoratorAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({P6spyConfiguration.class, JpaConfig.class})
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
public abstract class ServiceIntegration {

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

	protected Authority getAuthority(Role role) {
		return this.authorityRepository.getReferenceById(role);
	}

}
