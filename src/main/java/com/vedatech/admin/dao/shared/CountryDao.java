package com.vedatech.admin.dao.shared;

import com.vedatech.admin.model.shared.Country;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CountryDao extends CrudRepository<Country ,Integer> {

    @Query("select p from Country p where p.description like %?1%")
    public List<Country> findByDescription(String term);


}
