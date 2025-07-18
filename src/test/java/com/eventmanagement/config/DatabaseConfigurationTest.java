package com.eventmanagement.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Database Configuration Tests
 * 
 * Tests for Oracle database connection configuration and MyBatis setup.
 * Validates that the Oracle database connection is properly configured and working.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class DatabaseConfigurationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    public void testDataSourceConnection() throws SQLException {
        // Test that DataSource is properly configured and can establish connection
        assertThat(dataSource).isNotNull();
        
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.isClosed()).isFalse();
            
            // Test basic SQL operation with Oracle-specific syntax
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery("SELECT 1 FROM DUAL");
            assertThat(resultSet.next()).isTrue();
            assertThat(resultSet.getInt(1)).isEqualTo(1);
        }
    }

    @Test
    public void testDataSourceProperties() throws SQLException {
        // Test DataSource properties
        try (Connection connection = dataSource.getConnection()) {
            var metaData = connection.getMetaData();
            
            assertThat(metaData.getURL()).contains("jdbc:oracle:thin:");
            assertThat(metaData.getDriverName()).contains("Oracle");
            assertThat(metaData.getUserName()).isNotNull();
        }
    }

    @Test
    public void testSqlSessionFactory() {
        // Test that SqlSessionFactory is properly configured
        assertThat(sqlSessionFactory).isNotNull();
        assertThat(sqlSessionFactory.getConfiguration()).isNotNull();
        
        // Test MyBatis configuration
        var configuration = sqlSessionFactory.getConfiguration();
        assertThat(configuration.isMapUnderscoreToCamelCase()).isTrue();
        assertThat(configuration.getDefaultStatementTimeout()).isEqualTo(30);
        assertThat(configuration.getDefaultFetchSize()).isEqualTo(100);
    }

    @Test
    public void testMyBatisMapperRegistration() {
        // Test that MyBatis mappers are properly registered
        var configuration = sqlSessionFactory.getConfiguration();
        
        // Check that EventMapper is registered
        assertThat(configuration.hasMapper(com.eventmanagement.mapper.EventMapper.class)).isTrue();
        
        // Check that mapper locations are configured
        assertThat(configuration.getMappedStatementNames()).isNotEmpty();
    }

    @Test
    public void testDatabaseSchemaInitialization() throws SQLException {
        // Test that database schema is properly initialized
        try (Connection connection = dataSource.getConnection()) {
            var statement = connection.createStatement();
            
            // Test that events table exists
            var resultSet = statement.executeQuery("SELECT COUNT(*) FROM events");
            assertThat(resultSet.next()).isTrue();
            assertThat(resultSet.getInt(1)).isGreaterThanOrEqualTo(0); // Should have 0 or more records
            
            // Test that table structure is correct by checking some required columns
            resultSet = statement.executeQuery("SELECT event_id, event_name, status FROM events WHERE ROWNUM <= 1");
            // Just verify the query runs successfully - table structure is correct
        }
    }

    @Test
    public void testTransactionSupport() {
        // Test that transaction support is properly configured
        try (var sqlSession = sqlSessionFactory.openSession()) {
            assertThat(sqlSession).isNotNull();
            assertThat(sqlSession.getConfiguration()).isNotNull();
            assertThat(sqlSession.getConnection()).isNotNull();
            
            // Test that the session is working properly
            // Just verify we can get a working session - that's sufficient for this test
            assertThat(sqlSession.getConfiguration().getTypeAliasRegistry()).isNotNull();
        }
    }
}