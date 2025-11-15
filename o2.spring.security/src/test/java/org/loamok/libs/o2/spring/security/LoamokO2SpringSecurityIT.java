package org.loamok.libs.o2.spring.security;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author Huby Franck
 */
@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
public class LoamokO2SpringSecurityIT {

    public LoamokO2SpringSecurityIT() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testLoamokO2SpringSecurity() {
        LoamokO2SpringSecurity testedClass = new LoamokO2SpringSecurity();
        
        Assertions.assertThat(testedClass).isExactlyInstanceOf(LoamokO2SpringSecurity.class);
    }

}
