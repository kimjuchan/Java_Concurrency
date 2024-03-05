package com.example.demo.xss;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@AutoConfigureMockMvc
@Transactional
//열러있는 포트 없어 생성 후 빈 주입 시킴.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class XssTestControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void should_success_when_characterEscapesByObject() {
        String content = "<script>alert(0);</script>";
        String expected = "&lt;script&gt;alert(0);&lt;/script&gt;";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        XssRequestDto xssRequestDto = new XssRequestDto();
        xssRequestDto.setContent(content);

        HttpEntity<XssRequestDto> requestParam = new HttpEntity<>(xssRequestDto, headers);

        ResponseEntity<XssRequestDto> response = restTemplate.exchange(
                "/xss/list",
                HttpMethod.POST,
                requestParam,
                XssRequestDto.class
                );
        //치환은 정상적으로 &lt;script&gt;alert(0);&lt;/script&gt; 처리되는데
        //Controller에서 리턴값 넘겨줘서 해당 test 코드에서 받으면 "&amp;lt;script&amp;gt;alert(0);&amp;lt;/script&amp;gt;" 요래 되는데.. 문제가 뭘까요
        assertThat(response.getBody().getContent()).isEqualTo(expected);
        System.out.println("###");
        System.out.println("###");
        System.out.println("###");
    }

}