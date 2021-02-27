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

    public static ReimbursementStatus getByName(String name) {

        for (ReimbursementStatus role : ReimbursementStatus.values()) {
            if (role.reimbursementStatus.equals(name)) {
                return role;
            }
        }

        return PENDING;

        // functional implementation of the above code
//        return Arrays.stream(Role.values())
//                .filter(role -> role.roleName.equals(name))
//                .findFirst()
//                .orElse(LOCKED);

    }

    public static ReimbursementStatus getByNumber(Integer number){
        switch (number){
            case 1:
                return PENDING;
            case 2:
                return APPROVED;
            case 3:
                return DENIED;
        }
        return CLOSED;
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
