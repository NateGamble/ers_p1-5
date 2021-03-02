package com.revature.models;

public enum ReimbursementType {
    // values declared within enums are constants and are comma separated
    LODGING("Lodging"),
    TRAVEL("Travel"),
    FOOD("Food"),
    OTHER("Other");

    private String reimbursementType;

    // enum constructors are implicitly private
    ReimbursementType(String name) {
        this.reimbursementType = name;
    }

    /**
     * Retrieves a {@code ReimbursementType} associated with a given string
     * 
     * @param name case insensitive name of a Reimbursement type
     * @return a {@code ReimbursementType} enum associated with the given {@code name}
     *      or {@code null} if there is no enum associated with the given name
     */
    public static ReimbursementType getByName(String name) {

        for (ReimbursementType role : ReimbursementType.values()) {
            if (role.reimbursementType.equalsIgnoreCase(name)) {
                return role;
            }
        }

        return null;

    }

    /**
     * Retrieves a {@code ReimbursementStatus} associated with an int given.
     * Expects numbers to be 1-based, not 0-based, with maximum of 4.
     * @param number
     * @return a {@code ReimbmursementStatus} based on the {@code number} given,
     *      or {@code null} if there is no status associated with the number
     */
    public static ReimbursementType getByNumber(Integer number){
        switch (number){
            case 1:
                return LODGING;
            case 2:
                return TRAVEL;
            case 3:
                return FOOD;
            case 4:
                return OTHER;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return reimbursementType;
    }

    public String getReimbursementType() {
        return reimbursementType;
    }

    public void setReimbursementType(String reimbursementType) {
        this.reimbursementType = reimbursementType;
    }
    

}
