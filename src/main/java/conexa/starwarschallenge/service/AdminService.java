package conexa.starwarschallenge.service;

import conexa.starwarschallenge.dto.UpdateUserRoleRequest;
import conexa.starwarschallenge.dto.UserRoleDto;

import java.util.List;

public interface AdminService {
    List<UserRoleDto> getUsers();
    UserRoleDto updateUserRole(Integer userId, UpdateUserRoleRequest request);
}
