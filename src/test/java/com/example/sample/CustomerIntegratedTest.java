package com.example.sample;

import com.example.sample.customer.CustomerInsertRequest;
import com.example.sample.customer.CustomerUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(documentationConfiguration(restDocumentation).snippets()
//                        .withEncoding("UTF-8").withTemplateFormat(TemplateFormats.markdown())
                )
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("고객 리스트 조회")
    public void getList() throws Exception {
        mockMvc .perform( get("/customer").queryParam("size", "10").queryParam("page", "0").queryParam("sort", "id,desc")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document("customer-list",
                                queryParameters(
                                        parameterWithName("size").description("페이지 크기"),
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("sort").description("정렬 기준")
                                ),
                                responseFields (
                                        fieldWithPath("content").type(JsonFieldType.ARRAY).description("고객 정보"),
                                        fieldWithPath("content[].id").type(JsonFieldType.NUMBER).description("고유번호"),
                                        fieldWithPath("content[].name").type(JsonFieldType.STRING).description("박진희"),
                                        fieldWithPath("content[].tel").type(JsonFieldType.STRING).description("전화번호")
                                ).and(responsePageFields())
                        )
                );
    }

    @Test
    @DisplayName("고객 정보 조회")
    public void getDetail() throws Exception {
        mockMvc .perform( get("/customer/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document("customer-detail",
                                pathParameters(
                                        parameterWithName("id").description("고유번호")
                                ),
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
        CustomerInsertRequest request = new CustomerInsertRequest("박진희", "01022223333");
        mockMvc .perform( post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document("customer-save",
                                requestFields(
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("박진희"),
                                        fieldWithPath("tel").type(JsonFieldType.STRING).description("전화번호")
                                ),
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
        CustomerInsertRequest request = new CustomerInsertRequest("", "01022223333");
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
    @DisplayName("고객 정보 수정")
    public void putSuccess() throws Exception {
        CustomerUpdateRequest request = new CustomerUpdateRequest("박진희", "01022223333");
        mockMvc .perform( put("/customer/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document("customer-update-put",
                                pathParameters(
                                        parameterWithName("id").description("고유번호")
                                ),
                                requestFields(
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("박진희"),
                                        fieldWithPath("tel").type(JsonFieldType.STRING).description("전화번호")
                                ),
                                responseFields (
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("고유번호"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("박진희"),
                                        fieldWithPath("tel").type(JsonFieldType.STRING).description("전화번호")
                                )
                        )
                );
    }

    @Test
    @DisplayName("고객 정보 수정 실패")
    public void putFail() throws Exception {
        CustomerUpdateRequest request = new CustomerUpdateRequest("", "01022223333");
        mockMvc .perform( put("/customer/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(
                        document("customer-update-put-fail",
                                pathParameters(
                                        parameterWithName("id").description("고유번호")
                                ),
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
                .andDo(
                    document("customer-delete",
                        pathParameters(
                                parameterWithName("id").description("고유번호")
                        )
                ));
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

    private List<FieldDescriptor> responsePageFields() {
        return List.of(
                fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
                fieldWithPath("pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 여부"),
                fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                fieldWithPath("number").type(JsonFieldType.NUMBER).description("페이지 번호"),
                fieldWithPath("sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("요소 수"),
                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫번째 여부"),
                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("비어있는지 여부")
        );
    }


}
