package com.dadok.gaerval.domain.user.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "authorities")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authority {

    @Id
    @Enumerated(EnumType.STRING)
    private Role name;

    protected Authority(Role role) {
        this.name = role;
    }

    public static Authority of(Role role) {
        if (role == null)
            throw new IllegalArgumentException("role is must be not null");

        return new Authority(role);
    }

}

