package com.nawasenahost.authservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "auth_accounts")
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int authId;
}
