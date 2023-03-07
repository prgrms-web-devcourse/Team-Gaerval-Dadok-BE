package com.dadok.gaerval.integration_util;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.user.repository.UserRepository;
import com.dadok.gaerval.global.config.jpa.JpaConfig;
import com.dadok.gaerval.global.config.p6spy.P6spyConfiguration;
import com.github.gavlyukovskiy.boot.jdbc.decorator.DataSourceDecoratorAutoConfiguration;

@Transactional
@SpringBootTest(properties = "spring.jpa.show-sql=false")
@ImportAutoConfiguration(DataSourceDecoratorAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({P6spyConfiguration.class, JpaConfig.class})
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
public abstract class ServiceIntegration {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DataSource dataSource;

}
