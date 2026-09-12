package com.ambrosia.cluster_controller;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

import com.ambrosia.cluster_controller.model.DTO.admin.request.ClusterAdminRequest;
import com.ambrosia.cluster_controller.util.HttpSchema;
import com.ambrosia.cluster_controller.util.SupportedTaskSchedulers;

@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {
	@Container
	protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
		.withUsername("testuser")
		.withDatabaseName("testdb")
		.withPassword("testpassword")
		.withExposedPorts(5432);

	@Container
	protected static GenericContainer<?> testCluster = new GenericContainer<>("ghcr.io/ambrosia0/test-cluster:v1")
		.withExposedPorts(6820, 22)
		.withPrivilegedMode(true)
		.waitingFor(
			Wait.forLogMessage(".*Everything is started.*\\n", 1)
		);

	static {
		postgres.start();
		testCluster.start();
		try {
			testCluster.execInContainer("groupadd", "webguiusers");
			testCluster.execInContainer("usermod", "-aG", "webguiusers", "xenon");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@DynamicPropertySource
	static void configureContainer(DynamicPropertyRegistry registry){
		var mappedPort = postgres.getMappedPort(5432);
		registry.add("PASSWORD_ENCRYPTION_KEY", () -> "AOJINF12904i3toplefvdV2423r");
		registry.add("PASSWORD_ENCRYPTION_SALT", () -> "a1b2c3d4e5f60789");
		registry.add("DB_URL", () -> "jdbc:postgresql://localhost:"+mappedPort+"/testdb");
		registry.add("DB_USER", ()-> "testuser");
		registry.add("DB_PASSWORD", () -> "testpassword");
		registry.add("ADMIN_GROUP", () -> "webguiusers");
	}

	public class TestClusterConnectionFactory {
		public static ClusterAdminRequest createRequest(){
			return new ClusterAdminRequest(
				"localhost", 
				"xenon", 
				"javagat", 
				"testCluster", 
				testCluster.getMappedPort(6820), 
				HttpSchema.HTTP,
				testCluster.getMappedPort(22),
				SupportedTaskSchedulers.SLURMv0042
			);
		}
	}

}