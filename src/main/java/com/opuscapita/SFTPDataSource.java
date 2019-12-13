//package com.opuscapita;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//@Component
//public class SFTPDataSource {
//
//    @Value("${db-init.user:root}")
//    private String user;
//
//    @Value("${db-init.password:test}")
//    private String password;
//
//    @Value("${db-init.database:gateway}")
//    private String database;
//
//    @Bean
//    @Primary
//    public DataSource dataSource() {
//        return DataSourceBuilder
//                .create()
//                .username(user)
//                .password(password)
//                .url("jdbc:mysql://mysql:3306/" + database)
//                .driverClassName("com.mysql.cj.jdbc.Driver")
//                .build();
//    }
//}
