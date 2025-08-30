package com.interview.petmarket;

import com.interview.petmarket.config.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@DisplayName("PetMarket Application Tests")
@Tag("integration")
@Tag("smoke")
class PetmarketApplicationTests extends BaseIntegrationTest {

	@Test
	@DisplayName("Should load Spring application context successfully")
	void contextLoads() {
		// Test que verifica que el contexto de Spring se carga correctamente
		// con PostgreSQL como base de datos de test
		// Si este test pasa, significa que:
		// - PostgreSQL está conectado
		// - Flyway ejecutó las migraciones
		// - Todas las configuraciones de Spring están correctas
	}

}
