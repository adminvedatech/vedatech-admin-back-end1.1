package com.vedatech.admin.service.account;

import com.vedatech.admin.model.bank.AccountingType;

import java.util.List;


public interface AccountingTypeServcie  {

    public List<AccountingType> findAllAccountName();

    List<AccountingType> findByNombre(String term);



}
