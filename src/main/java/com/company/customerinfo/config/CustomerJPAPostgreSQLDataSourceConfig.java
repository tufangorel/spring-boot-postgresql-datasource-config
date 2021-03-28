package com.company.customerinfo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "customerEntityManager",
        transactionManagerRef = "customerTransactionManager",
        basePackages = {"com.company.customerinfo.repository"})
public class CustomerJPAPostgreSQLDataSourceConfig {

    @Autowired
    Environment environment;


    @Bean(name = "customerEntityManager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(){
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] {"com.company.customerinfo.model"});
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalJpaProperties());
        em.setPersistenceUnitName("customers");

        return em;
    }

    Properties additionalJpaProperties(){
        Properties properties = new Properties();

        // Hibernate ddl auto (create, create-drop, validate, update)
        properties.setProperty("hibernate.hbm2ddl.auto", "create");

        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.setProperty("hibernate.format_sql", "false");
        properties.setProperty("hibernate.generate_statistics", "false");

        // the creation of the HT_ tables is omitted.
        properties.setProperty("hibernate.hql.bulk_id_strategy", "org.hibernate.hql.spi.id.inline.InlineIdsSubSelectValueListBulkIdStrategy");

        return properties;
    }

    @Bean
    public DataSource dataSource(){
        return DataSourceBuilder.create()
                .url(environment.getProperty("datasource.url"))
                .driverClassName(environment.getProperty("datasource.driver.class"))
                .username(environment.getProperty("datasource.username"))
                .password(environment.getProperty("datasource.password"))
                .build();
    }

    @Bean(name = "customerTransactionManager")
    public JpaTransactionManager transactionManager(EntityManagerFactory customerEntityManager){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(customerEntityManager);

        return transactionManager;
    }
}
