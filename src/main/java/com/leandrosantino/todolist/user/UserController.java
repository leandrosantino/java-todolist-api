package com.leandrosantino.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leandrosantino.todolist.Responses.HttpResponse;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping
    public ResponseEntity<HttpResponse<UserModel>> create(@RequestBody UserModel userModel) {
        var response = new HttpResponse<UserModel>();
        var user = this.userRepository.findByUsername(userModel.getUsername());

        if (user != null) {
            response.setMessage("user already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        var passwordHashered = BCrypt.withDefaults()
                .hashToString(12, userModel.getPassword()
                        .toCharArray());

        userModel.setPassword(passwordHashered);

        var userCreated = this.userRepository.save(userModel);
        response.setData(userCreated);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
