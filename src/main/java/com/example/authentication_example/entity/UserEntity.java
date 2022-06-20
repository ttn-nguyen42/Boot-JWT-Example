package com.example.authentication_example.entity;

import com.example.authentication_example.model.RoleEnum;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotNull(message = "Email is required")
    @Basic(optional = false)
    @Column(name = "email")
    private String email;

    @Length(min = 8, message = "Password must be at least 8 characters")
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleEnum role = RoleEnum.USER;

    public UserEntity() {
    }

    public UserEntity(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
