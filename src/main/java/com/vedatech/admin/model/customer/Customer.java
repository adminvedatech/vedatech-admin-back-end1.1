package com.vedatech.admin.model.customer;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vedatech.admin.model.bank.AccountingType;
import com.vedatech.admin.model.shared.Address;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "customers")
public class Customer extends Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // @NotEmpty
    @Column(name = "name_customer")
    private String nameCustomer;

    @Column(name = "customer_rfc")
    private String customerRfc;

    @Column(name = "taxes")
    private boolean taxes;

    @OneToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="accounting_type_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    AccountingType accountingType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameCustomer() {
        return nameCustomer;
    }

    public void setNameCustomer(String nameCustomer) {
        this.nameCustomer = nameCustomer;
    }

    public String getCustomerRfc() {
        return customerRfc;
    }

    public void setCustomerRfc(String customerRfc) {
        this.customerRfc = customerRfc;
    }

    public boolean isTaxes() {
        return taxes;
    }

    public void setTaxes(boolean taxes) {
        this.taxes = taxes;
    }

    public AccountingType getAccountingType() {
        return accountingType;
    }

    public void setAccountingType(AccountingType accountingType) {
        this.accountingType = accountingType;
    }
}
