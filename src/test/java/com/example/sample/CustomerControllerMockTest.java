package com.example.sample;

import com.example.sample.customer.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("고객 리스트 조회")
    public void getList() throws Exception {

        CustomerEntity customer = CustomerExample.customer;

        given(customerService.getCustomers(
            Pageable.ofSize(10)
        )).willReturn(
            new PageImpl<>(List.of(customer.toDTO()))
        );

        this.mockMvc.perform(get("/customer")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value(customer.getName()))
                .andExpect(jsonPath("$.content[0].tel").value(customer.getTel()))
                .andDo(print());
    }

    @Test
    @DisplayName("고객 정보 조회 실패")
    public void getNoCustomer() throws Exception {
        given(customerService.getCustomer(1000L)).willReturn(Optional.empty());

        this.mockMvc.perform(
                        get("/customer/{id}", 1000L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(CustomerConstant.notFoundMessage))
                .andExpect(jsonPath("$.status").value(400))
                .andDo(print());
    }

    @Test
    @DisplayName("고객 정보 조회")
    public void getCustomer() throws Exception {

        CustomerEntity customer = CustomerExample.customer;
        given(customerService.getCustomer(customer.getId())).willReturn(Optional.of(customer));

        this.mockMvc.perform(
                        get("/customer/{id}", customer.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(customer.getName()))
                .andExpect(jsonPath("$.tel").value(customer.getTel()))
                .andDo(print());
    }

    @Test
    @DisplayName("고객 정보 수정 실패 - 이름 미입력 (Validation)")
    public void putValid() throws Exception {

        CustomerUpdateRequest request = new CustomerUpdateRequest("", "01012345678");
        this.mockMvc.perform(
                        put("/customer/{id}", 1000L)
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("이름을 입력해주세요."))
                .andExpect(jsonPath("$.status").value(400))
                .andDo(print());
    }

    @Test
    @DisplayName("고객 정보 수정 실패 - 고객 정보 찾을수 없음")
    public void putNoCustomer() throws Exception {

        CustomerUpdateRequest request = new CustomerUpdateRequest("박진희", "01012345678");

        given(customerService.getCustomer(1000L)).willReturn(Optional.empty());
        this.mockMvc.perform(
                        put("/customer/{id}", 1000L)
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(CustomerConstant.notFoundMessage))
                .andExpect(jsonPath("$.status").value(400))
                .andDo(print());
    }

    @Test
    @DisplayName("고객 정보 수정")
    public void putSuccess() throws Exception {
        CustomerUpdateRequest request = new CustomerUpdateRequest("박진희", "01012345678");
        CustomerEntity customer = CustomerExample.customer;
        given(customerService.getCustomer(customer.getId())).willReturn(Optional.of(customer));
        given(customerService.update(request, customer)).willReturn(customer);

        this.mockMvc.perform(
                        put("/customer/{id}", customer.getId())
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(customer.getName()))
                .andExpect(jsonPath("$.tel").value(customer.getTel()))
                .andDo(print());
    }


    @Test
    @DisplayName("고객 정보 저장 실패 - 이름 미입력 (Validation)")
    public void postValid() throws Exception {
        CustomerInsertRequest request = new CustomerInsertRequest("", "01040234504");
        this.mockMvc.perform(
                        post("/customer")
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("이름을 입력해주세요."))
                .andExpect(jsonPath("$.status").value(400))
                .andDo(print());
    }

    @Test
    @DisplayName("고객 정보 저장")
    public void postSuccess() throws Exception {
        CustomerInsertRequest request = new CustomerInsertRequest("박진희", "01022223333");

        CustomerEntity customer = new CustomerEntity(
            null, request.name(), request.tel()
        );
        given(customerService.save(request)).willReturn(customer);

        this.mockMvc.perform(
                        post("/customer")
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(customer.getName()))
                .andExpect(jsonPath("$.tel").value(customer.getTel()))
                .andDo(print());
    }


    @Test
    @DisplayName("고객 정보 삭제 실패 - 고객 정보 찾을수 없음")
    public void deleteNoCustomer() throws Exception {
        this.mockMvc.perform(
                        delete("/customer/{id}", 1000L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(CustomerConstant.notFoundMessage))
                .andExpect(jsonPath("$.status").value(400))
                .andDo(print());
    }


    @Test
    @DisplayName("고객 정보 삭제")
    public void deleteSuccess() throws Exception {
        CustomerEntity customer = CustomerExample.customer;
        given(customerService.getCustomer(customer.getId())).willReturn(Optional.of(customer));

        this.mockMvc.perform(
                        delete("/customer/{id}", customer.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
