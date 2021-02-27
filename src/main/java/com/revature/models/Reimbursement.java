package com.revature.models;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.revature.util.ReimbursementTypeConverter;
import com.revature.util.StatusCodeConverter;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * The base unit of the ERS system. ready to include images
 */
@Entity
@DynamicInsert
@Table(name = "reimbursements", schema = "p1_5")
public class Reimbursement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "serial NOT NULL")
    private Integer id;

    @Column(name = "amount", columnDefinition = "numeric(6,2) NOT NULL")
    private Double amount;

    @Generated(value = GenerationTime.ALWAYS)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    @Column(name = "submitted",columnDefinition = "timestamp NOT NULL")
    private Timestamp submitted;

    @ColumnDefault(value = "NULL")
    @Column(name = "resolved", columnDefinition = "timestamp NULL")
    private Timestamp resolved;

    @ColumnDefault(value = "NULL")
    @Column(name = "description", columnDefinition = "varchar(1000) NULL")
    private String description;

    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "receipt", columnDefinition = "bytea")
    private byte[] receipt;

    @ManyToOne(targetEntity = User.class, optional = false)
    @JoinColumn(name = "author_id", columnDefinition = "int4 NOT NULL")
    // @Cascade(CascadeType.)
    private User author;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "resolver_id", columnDefinition = "int4 NULL")
    private User resolver;

    @Column(name = "reimbursement_status_id", columnDefinition = "int4 NOT NULL")
    @Convert(converter = StatusCodeConverter.class)
    private ReimbursementStatus reimbursementStatus;

    @Column(name = "reimbursement_type_id", columnDefinition = "int4 NOT NULL")
    @Convert(converter = ReimbursementTypeConverter.class)
    private ReimbursementType reimbursementType;

    public Reimbursement() {
        super();
    }

    public Reimbursement(Double amount, String description, User author,
                         ReimbursementStatus reimbursementStatus, ReimbursementType reimbursementType) {
        this.amount = amount;
        this.description = description;
        this.author = author;
        this.reimbursementStatus = reimbursementStatus;
        this.reimbursementType = reimbursementType;
    }

    public Reimbursement(Integer id, Double amount, String description, User author,
                         ReimbursementStatus reimbursementStatus, ReimbursementType reimbursementType) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.author = author;
        this.reimbursementStatus = reimbursementStatus;
        this.reimbursementType = reimbursementType;
    }

    public Reimbursement(Integer id, Double amount, Timestamp submitted,
                         Timestamp resolved, String description, User author, User resolver,
                         ReimbursementStatus reimbursementStatus, ReimbursementType reimbursementType) {
        this.id = id;
        this.amount = amount;
        this.submitted = submitted;
        this.resolved = resolved;
        this.description = description;
        this.author = author;
        this.resolver = resolver;
        this.reimbursementStatus = reimbursementStatus;
        this.reimbursementType = reimbursementType;
    }

    public byte[] getReceipt() {
        return receipt;
    }

    public void setReceipt(byte[] receipt) {
        this.receipt = receipt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Timestamp getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Timestamp submitted) {
        this.submitted = submitted;
    }

    public Timestamp getResolved() {
        return resolved;
    }

    public void setResolved(Timestamp resolved) {
        this.resolved = resolved;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    

    public ReimbursementStatus getReimbursementStatus() {
        return reimbursementStatus;
    }

    public void setReimbursementStatus(ReimbursementStatus reimbursementStatus) {
        this.reimbursementStatus = reimbursementStatus;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getResolver() {
        return resolver;
    }

    public void setResolver(User resolver) {
        this.resolver = resolver;
    }

    public ReimbursementType getReimbursementType() {
        return reimbursementType;
    }

    public void setReimbursementType(ReimbursementType reimbursementType) {
        this.reimbursementType = reimbursementType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reimbursement)) return false;
        Reimbursement that = (Reimbursement) o;
        return author == that.author &&
                resolver == that.resolver &&
                Objects.equals(id, that.id) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(submitted, that.submitted) &&
                Objects.equals(resolved, that.resolved) &&
                Objects.equals(description, that.description) &&
                reimbursementStatus == that.reimbursementStatus &&
                reimbursementType == that.reimbursementType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, submitted, resolved, description, author, resolver, reimbursementStatus, reimbursementType);
    }

    @Override
    public String toString() {
        return "Reimbursement{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", submitted=" + submitted +
                ", resolved=" + resolved +
                ", description='" + description + '\'' +
                ", authorId=" + author +
                ", resolverId=" + resolver +
                ", reimbursementStatus=" + reimbursementStatus +
                ", reimbursementType=" + reimbursementType +
                '}';
    }
}
