package com.revature.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.revature.models.ReimbursementType;

/**
 * Converts a {@code ReimbursementType} to an int and back based on values used
 * in the database
 */
@Converter
public class ReimbursementTypeConverter implements AttributeConverter<ReimbursementType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ReimbursementType attribute) {
        if (attribute == null) {
            return null;
        }
        switch (attribute) {
            case LODGING:
                return 1;
            case TRAVEL:
                return 2;
            case FOOD:
                return 3;
            case OTHER:
                return 4;
            default:
                throw new IllegalArgumentException(attribute + " not supported.");   
        }
    }

    @Override
    public ReimbursementType convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        switch (dbData) {
            case 1:
                return ReimbursementType.LODGING;
            case 2:
                return ReimbursementType.TRAVEL;
            case 3:
                return ReimbursementType.FOOD;
            case 4:
                return ReimbursementType.OTHER;
            default:
                throw new IllegalArgumentException(dbData + " not supported");
        }
    }
    
}