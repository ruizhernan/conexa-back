package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.UpdateUserRoleRequest;
import conexa.starwarschallenge.dto.UserRoleDto;
import conexa.starwarschallenge.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserRoleDto>> getUsers() {
        return ResponseEntity.ok(adminService.getUsers());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserRoleDto> updateUserRole(@PathVariable Integer id, @RequestBody UpdateUserRoleRequest request) {
        return ResponseEntity.ok(adminService.updateUserRole(id, request));
    }
}
