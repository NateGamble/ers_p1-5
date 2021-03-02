package com.revature.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.revature.models.ReimbursementStatus;

/**
 * Converts a {@code ReimbursementStatus} to an int and back based on values used
 * in the database
 */
@Converter
public class StatusCodeConverter implements AttributeConverter<ReimbursementStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ReimbursementStatus attribute) {
        if (attribute == null) {
            return null;
        }
        switch (attribute) {
            case PENDING:
                return 1;
            case APPROVED:
                return 2;
            case DENIED:
                return 3;
            case CLOSED:
                return 4;
            default:
                throw new IllegalArgumentException(attribute + " not supported.");   
        }
    }

    @Override
    public ReimbursementStatus convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        switch (dbData) {
            case 1:
                return ReimbursementStatus.PENDING;
            case 2:
                return ReimbursementStatus.APPROVED;
            case 3:
                return ReimbursementStatus.DENIED;
            case 4:
                return ReimbursementStatus.CLOSED;
            default:
                throw new IllegalArgumentException(dbData + " not supported");
        }
    }
    
}