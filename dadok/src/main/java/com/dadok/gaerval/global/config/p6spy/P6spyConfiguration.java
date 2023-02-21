package com.dadok.gaerval.global.config.p6spy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.context.annotation.Configuration;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

@Configuration
public class P6spyConfiguration implements MessageFormattingStrategy {

	@PostConstruct
	public void setLogMessageFormat() {
		P6SpyOptions.getActiveInstance().setLogMessageFormat(this.getClass().getName());
	}

	@Override
	public String formatMessage(int connectionId, String now, long elapsed, String category,
		String prepared, String sql, String url) {
		sql = formatSql(category, sql);

		SimpleDateFormat formatter = new SimpleDateFormat("yy.MM.dd HH:mm:ss");

		return formatter.format(new Date()) + " | " + "OperationTime : " + elapsed + "ms" + sql;
	}

	private String formatSql(String category, String sql) {
		if (sql == null || sql.trim().equals("")) {
			return sql;
		}

		if (Category.STATEMENT.getName().equals(category)) {
			String tmpSql = sql.trim().toLowerCase(Locale.ROOT);
			if (tmpSql.startsWith("create") ||
				tmpSql.startsWith("alter") ||
				tmpSql.startsWith("comment")) {

				sql = FormatStyle.DDL.getFormatter().format(sql);
			} else {
				sql = FormatStyle.BASIC.getFormatter().format(sql);
			}
			sql = "|\n Hibernate FormatSql(P6Spy sql): " + sql + "\n";
		}

		return sql;
	}

}
