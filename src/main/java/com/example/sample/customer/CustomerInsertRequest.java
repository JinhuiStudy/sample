package com.example.sample.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerInsertRequest(
        @NotBlank(message = "이름을 입력해주세요.")
        @Size(max = 5, message = "이름은 5자리 이내로 입력해주세요.")
        String name,

        @NotBlank(message = "휴대폰 번호를 입력해주세요.")
        @Pattern(regexp = "[0-9]+", message = "휴대폰번호는 숫자로만 입력해주세요.")
        @Size(max = 11, message = "휴대폰 번호는 12자리 이내로 입력해주세요.")
        String tel
) {}