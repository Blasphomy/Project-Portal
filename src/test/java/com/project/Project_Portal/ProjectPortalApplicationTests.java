package com.project.Project_Portal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ImportAutoConfiguration(exclude = {
        R2dbcAutoConfiguration.class,
        FlywayAutoConfiguration.class
})
class ProjectPortalApplicationTests {

	@Test
	void contextLoads() {
	}

}
