package com.library_services.gateway_server.service;

import com.library_services.gateway_server.entity.User;
import com.library_services.gateway_server.repository.UsersRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class UserService implements UserDetailsService {


    UsersRepo repository;

    public UserService(UsersRepo repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = repository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + username));
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));


        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);

    }

    public  com.library_services.gateway_server.pojo.User getUser(String username) {

        User usr = repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        com.library_services.gateway_server.pojo.User user = new  com.library_services.gateway_server.pojo.User();
        user.setId(usr.getId());
        user.setUsername(usr.getUsername());
        user.setPassword(usr.getPassword());
        user.setEmail(usr.getEmail());
        return user;
    }

}
