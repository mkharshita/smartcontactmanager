package com.smart.smartcontactmanager.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smart.smartcontactmanager.entities.Contact;
import com.smart.smartcontactmanager.entities.User;

import java.util.List;


@Repository
public interface ContactRepository extends JpaRepository<Contact,Integer>{

    @Query("from Contact as c where c.user.id =:userId")
    public Page<Contact> findContactByUser(@Param("userId")int userId, Pageable pageable);

    public List<Contact> findByNameContainingAndUser(String name,User user);
}
