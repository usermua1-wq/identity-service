package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.dto.request.UserUpdateRequest;
import com.devteria.identity_service.dto.response.UserDTO;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request){
        return ApiResponse.<User>builder()
                 .result(userService.createRequest(request))
                 .build();
    }

    @GetMapping
    ApiResponse<List<UserDTO>> getUsers(){
        return ApiResponse.<List<UserDTO>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserDTO> getUser(@PathVariable("userId") String userId){
        return ApiResponse.<UserDTO>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @PutMapping
    ApiResponse<UserDTO> userUpdate(@RequestBody @Valid UserUpdateRequest request){
        return ApiResponse.<UserDTO>builder()
                 .result(userService.userUpdate(request))
                 .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable("userId") String userId){
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }

    @GetMapping("/getMyInfo")
    ApiResponse<UserDTO> getMyInfo(){
        return ApiResponse.<UserDTO>builder()
                .result(userService.getMyInfo())
                .build();
    }
}
