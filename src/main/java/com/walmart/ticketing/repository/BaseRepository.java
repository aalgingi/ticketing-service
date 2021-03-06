package com.walmart.ticketing.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

public @NoRepositoryBean interface BaseRepository<T, ID extends Serializable> extends Repository<T, ID> {
	 
    void delete(T deleted);
 
    List<T> findAll();
     
    Optional<T> findOne(ID id);
 
    T save(T persisted);
}