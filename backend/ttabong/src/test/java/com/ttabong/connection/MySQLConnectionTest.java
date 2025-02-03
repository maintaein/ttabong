package com.ttabong.connection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 DB 사용
public class MySQLConnectionTest {
/*
    @Autowired
    private DataSource dataSource;

    @Test
    public void testMySQLConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection);
            System.out.println("MySQL 연결 성공");
        }
    }

 */
}
