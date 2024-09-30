package com.smart.smartcontactmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.User;

@Service
public class UserDetailServiceImpl implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.getUserByUserName(username);
        if(user == null){
            throw new UsernameNotFoundException("Could not found User");
        }

        CustomUserDetail customUserDetail = new CustomUserDetail(user);
        
        return customUserDetail;
    }


}
