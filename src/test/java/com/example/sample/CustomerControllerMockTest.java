package com.example.sample;

import com.example.sample.customer.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

        Customer customer = CustomerExample.customer;
        given(customerService.getCustomers()).willReturn(List.of(customer.toDTO()));

        this.mockMvc.perform(get("/customer").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value(customer.getName()))
                .andExpect(jsonPath("$.[0].tel").value(customer.getTel()))
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

        Customer customer = CustomerExample.customer;
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
    @DisplayName("고객 정보 전체 수정 실패 - 이름 미입력 (Validation)")
    public void putValid() throws Exception {

        CustomerRequest.PutUpdateRequest request = new CustomerRequest.PutUpdateRequest("", "01012345678");
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
    @DisplayName("고객 정보 전체 수정 실패 - 고객 정보 찾을수 없음")
    public void putNoCustomer() throws Exception {

        CustomerRequest.PutUpdateRequest request = new CustomerRequest.PutUpdateRequest("박진희", "01012345678");

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
    @DisplayName("고객 정보 전체 수정")
    public void putSuccess() throws Exception {
        CustomerRequest.PutUpdateRequest request = new CustomerRequest.PutUpdateRequest("박진희", "01012345678");
        Customer customer = CustomerExample.customer;
        given(customerService.getCustomer(customer.getId())).willReturn(Optional.of(customer));
        given(customerService.mergeCustomer(customer)).willReturn(customer);

        this.mockMvc.perform(
                        put("/customer/{id}", customer.getId())
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(customer.getName()))
                .andExpect(jsonPath("$.tel").value(request.getTel()))
                .andDo(print());
    }

    @Test
    @DisplayName("고객 정보 일부 수정 실패 - 이름 미입력 (Validation)")
    public void patchValid() throws Exception {
        CustomerRequest.PatchUpdateRequest request = new CustomerRequest.PatchUpdateRequest("");
        this.mockMvc.perform(
                        patch("/customer/{id}", 1000L)
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("이름을 입력해주세요."))
                .andExpect(jsonPath("$.status").value(400))
                .andDo(print());
    }

    @Test
    @DisplayName("고객 정보 일부 수정 실패 - 고객 정보 찾을수 없음")
    public void patchNoCustomer() throws Exception {
        CustomerRequest.PatchUpdateRequest request = new CustomerRequest.PatchUpdateRequest("유광열");
        given(customerService.getCustomer(1000L)).willReturn(Optional.empty());

        this.mockMvc.perform(
                        patch("/customer/{id}", 1000L)
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(CustomerConstant.notFoundMessage))
                .andExpect(jsonPath("$.status").value(400))
                .andDo(print());
    }


    @Test
    @DisplayName("고객 정보 일부 수정")
    public void patchSuccess() throws Exception {
        CustomerRequest.PatchUpdateRequest request = new CustomerRequest.PatchUpdateRequest("유광열");
        Customer customer = CustomerExample.customer;
        given(customerService.getCustomer(customer.getId())).willReturn(Optional.of(customer));
        given(customerService.mergeCustomer(customer)).willReturn(customer);

        this.mockMvc.perform(
                        patch("/customer/{id}", customer.getId())
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.tel").value(customer.getTel()))
                .andDo(print());
    }



    @Test
    @DisplayName("고객 정보 저장 실패 - 이름 미입력 (Validation)")
    public void postValid() throws Exception {
        CustomerRequest.InsertRequest request = new CustomerRequest.InsertRequest("", "01040234504");
        this.mockMvc.perform(
                        post("/customer")
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("이름을 입력해주세요."))
                .andExpect(jsonPath("$.status").value(400))
                .andDo(print());
    }

    // TODO
//    @Test
//    public void postSuccess() throws Exception {
//        CustomerRequest.CustomerInsertRequest request = new CustomerRequest.CustomerInsertRequest("유광열", "01022223333");
//
//        Customer customer = request.toEntity();
//        given(customerService.mergeCustomer(request)).willReturn(customer);
//
//        this.mockMvc.perform(
//                        post("/customer")
//                                .content(objectMapper.writeValueAsBytes(request))
//                                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value(customer.getName()))
//                .andExpect(jsonPath("$.tel").value(customer.getTel()))
//                .andDo(print());
//    }


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
        Customer customer = CustomerExample.customer;
        given(customerService.getCustomer(customer.getId())).willReturn(Optional.of(customer));

        this.mockMvc.perform(
                        delete("/customer/{id}", customer.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }


}
