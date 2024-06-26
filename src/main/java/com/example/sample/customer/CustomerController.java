package com.example.sample.customer;

import com.example.sample.common.exception.Common400Exception;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customer")
@Tag(name = CustomerConstant.name, description = CustomerConstant.description)
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
//    @Operation(summary = "고객 리스트 조회", description = "고객 리스트를 조회합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "OK",
//                    content = @Content(
//                            array = @ArraySchema (
//                                    schema = @Schema(implementation = CustomerDTO.class)
//                            )
//                    )
//            ),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
//                    content = @Content(
//                            schema = @Schema(implementation = ProblemDetail.class)
//                    )
//            ),
//    })
    public ResponseEntity<Page<CustomerDTO>> getCustomers(Pageable pageable) {
        return ResponseEntity.ok(customerService.getCustomers(pageable));
    }


    @GetMapping("/{id}")
//    @Operation(summary = "고객 조회", description = "고객 정보를 조회합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "OK",
//                    content = @Content(
//                            schema = @Schema(
//                                    implementation = CustomerDTO.class)
//                    )
//            ),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
//                    content = @Content(
//                            schema = @Schema(implementation = ProblemDetail.class)
//                    )
//            ),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
//                    content = @Content(
//                            schema = @Schema(implementation = ProblemDetail.class)
//                    )
//            ),
//    })
    public ResponseEntity<CustomerDTO> getCustomer(
            @Parameter(name = "id", description = "고객의 id", in = ParameterIn.PATH) @PathVariable Long id
    ) {
        Optional<CustomerEntity> customerOptional = customerService.getCustomer(id);
        if (customerOptional.isEmpty()) {
            throw new Common400Exception(CustomerConstant.notFoundMessage);
        }
        return ResponseEntity.ok(customerOptional.get().toDTO());
    }

    @PostMapping
//    @Operation(summary = "고객 저장", description = "고객을 저장합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "OK",
//                    content = @Content(
//                            schema = @Schema(
//                                    implementation = CustomerDTO.class)
//                    )
//            ),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
//                    content = @Content(
//                            schema = @Schema(implementation = ProblemDetail.class)
//                    )
//            ),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
//                    content = @Content(
//                            schema = @Schema(implementation = ProblemDetail.class)
//                    )
//            ),
//    })
    public ResponseEntity<CustomerDTO> saveCustomer(
            @RequestBody @Valid CustomerInsertRequest request,
            Errors errors
    ) {
        if (errors.hasErrors()) {
            throw new Common400Exception(errors.getFieldErrors().get(0).getDefaultMessage());
        }

        return ResponseEntity.ok(customerService.save(request).toDTO());
    }

//    @Operation(summary = "고객 전체 수정", description = "고객 정보를 전체 수정합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "OK",
//                    content = @Content(
//                            schema = @Schema(
//                                    implementation = CustomerDTO.class)
//                    )
//            ),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
//                    content = @Content(
//                            schema = @Schema(implementation = ProblemDetail.class)
//                    )
//            ),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
//                    content = @Content(
//                            schema = @Schema(implementation = ProblemDetail.class)
//                    )
//            ),
//    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> putCustomer(
            @Parameter(name = "id", description = "고객의 id", in = ParameterIn.PATH) @PathVariable Long id,
            @RequestBody @Valid CustomerUpdateRequest request,
            Errors errors
    ) {
        if (errors.hasErrors()) {
            throw new Common400Exception(errors.getFieldErrors().get(0).getDefaultMessage());
        }

        Optional<CustomerEntity> customerOptional = customerService.getCustomer(id);
        if (customerOptional.isEmpty()) {
            throw new Common400Exception(CustomerConstant.notFoundMessage);
        }
        CustomerEntity customer = customerOptional.get();

        return ResponseEntity.ok(customerService.update(request, customer).toDTO());
    }


//    @Operation(summary = "고객 삭제", description = "고객 정보가 삭제됩니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "OK"),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
//    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(
            @Parameter(name = "id", description = "고객의 id", in = ParameterIn.PATH) @PathVariable Long id
    ) {

        Optional<CustomerEntity> customerOptional = customerService.getCustomer(id);
        if (customerOptional.isEmpty()) {
            throw new Common400Exception(CustomerConstant.notFoundMessage);
        }

        customerService.deleteCustomer(customerOptional.get());
        return ResponseEntity.ok().build();
    }

}
