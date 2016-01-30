package com.example.config;

import java.sql.Driver;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

/**
 * DataSourceの設定を行います.
 * @author ryo_shindo
 *
 */
@Configuration
@PropertySource("classpath:config/jdbc.properties")
public class DataSourceConfigulation {
	
//	@Value("classpath:schema-all.sql")
//	private Resource schemaScript;
	
	@Value("${jdbc.driverClassName}")
	private Class<? extends Driver> jdbcDriver;
	
	@Value("${jdbc.url}")
	private String url;
	
	@Value("${jdbc.username}")
	private String userName;
	
	@Value("${jdbc.password}")
	private String password;
	
	/**
	 * JdbcTemplateのインスタンスを取得します.
	 * @param dataSource
	 * @return jdbcTemplate
	 */
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	
	/**
	 * DataSourceのインスタンスを取得します.
	 * @return dataSource
	 */
	@Bean
	public DataSource dataSource() {
		final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(jdbcDriver);
		dataSource.setUrl(url);
		dataSource.setUsername(userName);
		dataSource.setPassword(password);
		return dataSource;
	}

}
