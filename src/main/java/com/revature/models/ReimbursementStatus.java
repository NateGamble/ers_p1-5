package com.revature.models;


public enum ReimbursementStatus {
    // values declared within enums are constants and are comma separated
    PENDING("Pending"),
    APPROVED("Approved"),
    DENIED("Denied"),
    CLOSED("Closed");

    private String reimbursementStatus;

    // enum constructors are implicitly private
    ReimbursementStatus(String name) {
        this.reimbursementStatus = name;
    }

    /**
     * Retrieves a {@code ReimbursementStatus} associated with a given string
     * 
     * @param name case insensitive name of a Reimbursement status
     * @return a {@code ReimbursementStatus} enum associated with the given {@code name}
     *      or {@code null} if there is no enum associated with the given name
     */
    public static ReimbursementStatus getByName(String name) {

        for (ReimbursementStatus role : ReimbursementStatus.values()) {
            if (role.reimbursementStatus.equalsIgnoreCase(name)) {
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
    public static ReimbursementStatus getByNumber(Integer number){
        switch (number){
            case 1:
                return PENDING;
            case 2:
                return APPROVED;
            case 3:
                return DENIED;
            case 4:
                return CLOSED;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return reimbursementStatus;
    }

    public String getReimbursementStatus() {
        return reimbursementStatus;
    }

    public void setReimbursementStatus(String reimbursementStatus) {
        this.reimbursementStatus = reimbursementStatus;
    }



}
