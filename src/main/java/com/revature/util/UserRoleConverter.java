package com.revature.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.revature.models.Role;

/**
 * Converts a {@code Role} to an int and back based on values used
 * in the database
 */
@Converter
public class UserRoleConverter implements AttributeConverter<Role, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Role attribute) {
        if (attribute == null) {
            return null;
        }
        switch (attribute) {
            case ADMIN:
                return 1;
            case FINANCE_MANAGER:
                return 2;
            case EMPLOYEE:
                return 3;
            case DELETED:
                return 4;
            default:
                throw new IllegalArgumentException(attribute + " not supported.");   
        }
    }

    @Override
    public Role convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        switch (dbData) {
            case 1:
                return Role.ADMIN;
            case 2:
                return Role.FINANCE_MANAGER;
            case 3:
                return Role.EMPLOYEE;
            case 4:
                return Role.DELETED;
            default:
                throw new IllegalArgumentException(dbData + " not supported");
        }
    }
    
}