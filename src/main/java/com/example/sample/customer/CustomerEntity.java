package com.example.sample.customer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Table(name = "customer")
@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("이름")
    @Column(nullable = false, length = 100)
    private String name;

    @Comment("전화번호")
    @Column(nullable = false, length = 20)
    private String tel;

    public CustomerDTO toDTO() {
        return new CustomerDTO(id, name, tel);
    }
}
