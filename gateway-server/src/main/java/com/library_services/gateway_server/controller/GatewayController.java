package com.library_services.gateway_server.controller;

import com.library_services.gateway_server.pojo.User;
import com.library_services.gateway_server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class GatewayController {

    private UserService userService;

    public GatewayController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/user",method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@RequestParam String username) {
          User user=  userService.getUser(username);
          return  new ResponseEntity<>(user, HttpStatus.OK);
    }
}
