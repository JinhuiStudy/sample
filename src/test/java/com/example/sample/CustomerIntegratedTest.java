package com.example.sample;

import com.example.sample.customer.CustomerRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class CustomerIntegratedTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp (
            WebApplicationContext webApplicationContext,
            RestDocumentationContextProvider restDocumentation
    ) {
        // markdown 문서 생성
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(documentationConfiguration(restDocumentation).snippets().withEncoding("UTF-8").withTemplateFormat(TemplateFormats.markdown()))
                .alwaysDo(print())
                .build();
    }


    @Test
    @DisplayName("고객 리스트 조회")
    public void getList() throws Exception {
        mockMvc .perform( get("/customer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document("customer-list",
                                responseFields (
                                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("고유번호"),
                                        fieldWithPath("[].name").type(JsonFieldType.STRING).description("박진희"),
                                        fieldWithPath("[].tel").type(JsonFieldType.STRING).description("전화번호")
                                )
                        )
                );
    }

    @Test
    @DisplayName("고객 정보 조회")
    public void getDetail() throws Exception {
        mockMvc .perform( get("/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document("customer-detail",
                                responseFields (
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("고유번호"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("박진희"),
                                        fieldWithPath("tel").type(JsonFieldType.STRING).description("전화번호")
                                )
                        )
                );
    }

    @Test
    @DisplayName("고객 정보 조회 실패")
    public void getDetailFail() throws Exception {
        mockMvc .perform( get("/customer/100000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(
                        document("customer-detail-fail",
                                responseFields (
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("실패 코드"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("실패 요인"),
                                        fieldWithPath("detail").type(JsonFieldType.STRING).description("실패 메세지"),
                                        fieldWithPath("instance").type(JsonFieldType.STRING).description("주소"),
                                        fieldWithPath("type").type(JsonFieldType.STRING).description("")
                                )
                        )
                );
    }


    @Test
    @DisplayName("고객 정보 저장")
    public void postSuccess() throws Exception {
        CustomerRequest.InsertRequest request = new CustomerRequest.InsertRequest("유광열", "01022223333");
        mockMvc .perform( post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document("customer-save",
                                responseFields (
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("고유번호"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("박진희"),
                                        fieldWithPath("tel").type(JsonFieldType.STRING).description("전화번호")
                                )
                        )
                );
    }

    @Test
    @DisplayName("고객 정보 저장 실패")
    public void postFail() throws Exception {
        CustomerRequest.InsertRequest request = new CustomerRequest.InsertRequest("", "01022223333");
        mockMvc .perform( post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(
                        document("customer-save-fail",
                                responseFields (
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("실패 코드"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("실패 요인"),
                                        fieldWithPath("detail").type(JsonFieldType.STRING).description("실패 메세지"),
                                        fieldWithPath("instance").type(JsonFieldType.STRING).description("주소"),
                                        fieldWithPath("type").type(JsonFieldType.STRING).description("")
                                )
                        )
                );
    }


    @Test
    @DisplayName("고객 정보 전체 수정")
    public void putSuccess() throws Exception {
        CustomerRequest.PutUpdateRequest request = new CustomerRequest.PutUpdateRequest("유광열", "01022223333");
        mockMvc .perform( put("/customer/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document("customer-update-put",
                                responseFields (
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("고유번호"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("박진희"),
                                        fieldWithPath("tel").type(JsonFieldType.STRING).description("전화번호")
                                )
                        )
                );
    }

    @Test
    @DisplayName("고객 정보 전체 수정 실패")
    public void putFail() throws Exception {
        CustomerRequest.PutUpdateRequest request = new CustomerRequest.PutUpdateRequest("", "01022223333");
        mockMvc .perform( put("/customer/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(
                        document("customer-update-put-fail",
                                responseFields (
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("실패 코드"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("실패 요인"),
                                        fieldWithPath("detail").type(JsonFieldType.STRING).description("실패 메세지"),
                                        fieldWithPath("instance").type(JsonFieldType.STRING).description("주소"),
                                        fieldWithPath("type").type(JsonFieldType.STRING).description("")
                                )
                        )
                );
    }


    @Test
    @DisplayName("고객 정보 일부 수정")
    public void patchSuccess() throws Exception {
        CustomerRequest.PatchUpdateRequest request = new CustomerRequest.PatchUpdateRequest("유광열");
        mockMvc .perform( patch("/customer/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document("customer-update-patch",
                                responseFields (
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("고유번호"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("박진희"),
                                        fieldWithPath("tel").type(JsonFieldType.STRING).description("전화번호")
                                )
                        )
                );
    }

    @Test
    @DisplayName("고객 정보 일부 수정 실패")
    public void patchFail() throws Exception {
        CustomerRequest.PatchUpdateRequest request = new CustomerRequest.PatchUpdateRequest("");
        mockMvc .perform( patch("/customer/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(
                        document("customer-update-patch-fail",
                                responseFields (
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("실패 코드"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("실패 요인"),
                                        fieldWithPath("detail").type(JsonFieldType.STRING).description("실패 메세지"),
                                        fieldWithPath("instance").type(JsonFieldType.STRING).description("주소"),
                                        fieldWithPath("type").type(JsonFieldType.STRING).description("")
                                )
                        )
                );
    }


    @Test
    @DisplayName("고객 정보 삭제")
    public void deleteSuccess() throws Exception {
        mockMvc .perform( delete("/customer/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("customer-delete"));
    }

    @Test
    @DisplayName("고객 정보 삭제 실패")
    public void deleteFail() throws Exception {
        mockMvc .perform( delete("/customer/{id}", 1000)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(
                        document("customer-delete-fail",
                                responseFields (
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("실패 코드"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("실패 요인"),
                                        fieldWithPath("detail").type(JsonFieldType.STRING).description("실패 메세지"),
                                        fieldWithPath("instance").type(JsonFieldType.STRING).description("주소"),
                                        fieldWithPath("type").type(JsonFieldType.STRING).description("")
                                )
                        )
                );
    }

}
