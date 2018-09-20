package com.vedatech.admin.dao.bankDao;

import com.vedatech.admin.model.bank.Bank;
import org.springframework.data.repository.CrudRepository;

public interface BankDao extends CrudRepository<Bank, Long> {

        Bank findBankByAccountNumber(Long accountNumber);
        Bank findBankById(Long id);

}
