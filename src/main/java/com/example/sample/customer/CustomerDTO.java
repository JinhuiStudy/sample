package com.example.sample.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public record CustomerDTO (
        @Schema(description = "고객 ID")
        Long id,

        @Schema(description = "고객 이름", example = "박진희")
        String name,

        @Schema(description = "고객 전화번호", example = "01040234504")
        String tel
) {

}
