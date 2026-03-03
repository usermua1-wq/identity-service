package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.request.RoleRequest;
import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.dto.response.RoleDTO;
import com.devteria.identity_service.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleDTO> createRole(@RequestBody RoleRequest request){
        return ApiResponse.<RoleDTO>builder()
                .result(roleService.createRole(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleDTO>> getAll2(){
        return ApiResponse.<List<RoleDTO>>builder()
                .result(roleService.getAll2())
                .build();
    }

    @DeleteMapping("/{rolename}")
    ApiResponse<Void> deletePermission(@PathVariable String rolename){
        roleService.deleteRole(rolename);
        return ApiResponse.<Void>builder()
                .build();
    }
}
