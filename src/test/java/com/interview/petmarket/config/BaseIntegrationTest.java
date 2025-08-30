package com.interview.petmarket.config;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

/**
 * Clase base para tests de integración con PostgreSQL
 * 
 * Configuración:
 * - Usa PostgreSQL como base de datos de test
 * - Limpia la base de datos antes de cada test
 * - Configura transacciones para rollback automático
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = "/sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class BaseIntegrationTest {
    
    @BeforeEach
    void setUp() {
        // Setup común para todos los tests
        // La base de datos se limpia automáticamente por @Sql
    }
}
