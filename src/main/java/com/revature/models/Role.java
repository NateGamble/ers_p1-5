package com.revature.models;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user_roles", schema = "p1_5")
public enum Role {
    ADMIN("Admin"),
    FINANCE_MANAGER("Finance Manager"),
    EMPLOYEE("Employee"),
    DELETED("Deleted");

    private String roleName;

    Role(String name) {
        this.roleName = name;
    }

    public static Role getByName(String name) {

        for (Role role : Role.values()) {
            if (role.roleName.equals(name)) {
                return role;
            }
        }
        return EMPLOYEE;
    }

    @Override
    public String toString() {
        return roleName;
    }

}
