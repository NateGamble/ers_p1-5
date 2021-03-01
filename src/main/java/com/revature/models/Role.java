package com.revature.models;

public enum Role {
    ADMIN("Admin"),
    FINANCE_MANAGER("Finance Manager"),
    EMPLOYEE("Employee"),
    DELETED("Deleted");

    private String roleName;

    Role(String name) {
        this.roleName = name;
    }

    /**
     * Retrieves a {@code Role} associated with a given string
     * 
     * @param name case insensitive name of a role type
     * @return a {@code Role} enum associated with the given {@code name}
     *      or {@code null} if there is no enum associated with the given name
     */
    public static Role getByName(String name) {

        for (Role role : Role.values()) {
            if (role.roleName.equalsIgnoreCase(name)) {
                return role;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return roleName;
    }

}
