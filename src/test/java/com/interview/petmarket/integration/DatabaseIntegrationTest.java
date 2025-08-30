package com.interview.petmarket.integration;

import com.interview.petmarket.config.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integración para verificar la conectividad con PostgreSQL
 */
class DatabaseIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldConnectToPostgreSQL() {
        // Given & When
        String databaseProductName = null;
        try {
            databaseProductName = dataSource.getConnection().getMetaData().getDatabaseProductName();
        } catch (Exception e) {
            throw new RuntimeException("Error connecting to database", e);
        }

        // Then
        assertThat(databaseProductName).isEqualTo("PostgreSQL");
    }

    @Test
    void shouldHaveAllRequiredTables() {
        // Given
        String[] expectedTables = {
            "productos", "clientes", "carritos", "carrito_items",
            "pedidos", "pedido_items", "inventario", "solicitudes_adopcion"
        };

        // When & Then
        for (String tableName : expectedTables) {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?",
                Integer.class,
                tableName
            );
            assertThat(count).withFailMessage("Table %s should exist", tableName).isEqualTo(1);
        }
    }

    @Test
    void shouldBeAbleToInsertAndQueryData() {
        // Given
        String insertSql = "INSERT INTO clientes (nombre, email) VALUES (?, ?)";
        String selectSql = "SELECT COUNT(*) FROM clientes WHERE nombre = ?";

        // When
        jdbcTemplate.update(insertSql, "Test Cliente", "test@example.com");
        Integer count = jdbcTemplate.queryForObject(selectSql, Integer.class, "Test Cliente");

        // Then
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldHaveCleanDatabaseBetweenTests() {
        // Given & When
        Integer clienteCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM clientes", Integer.class);
        Integer productCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM productos", Integer.class);

        // Then - La base de datos debe estar limpia al inicio de cada test
        assertThat(clienteCount).isEqualTo(0);
        assertThat(productCount).isEqualTo(0);
    }
}
