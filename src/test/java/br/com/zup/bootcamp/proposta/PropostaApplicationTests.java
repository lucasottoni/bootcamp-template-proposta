package br.com.zup.bootcamp.proposta;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import io.opentracing.contrib.spring.tracer.configuration.TracerAutoConfiguration;

@SpringBootTest
@EnableAutoConfiguration(exclude = TracerAutoConfiguration.class)
class PropostaApplicationTests {

	@Test
	void contextLoads() {
	}

}
