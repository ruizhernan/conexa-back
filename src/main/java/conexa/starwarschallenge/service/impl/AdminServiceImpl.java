package conexa.starwarschallenge.service.impl;

import conexa.starwarschallenge.dto.UpdateUserRoleRequest;
import conexa.starwarschallenge.dto.UserRoleDto;
import conexa.starwarschallenge.repository.UserRepository;
import conexa.starwarschallenge.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    @Override
    public List<UserRoleDto> getUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserRoleDto(user.getId(), user.getUsername(), user.getRole().name()))
                .collect(Collectors.toList());
    }

    @Override
    public UserRoleDto updateUserRole(Integer userId, UpdateUserRoleRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        user.setRole(request.getRole());
        userRepository.save(user);
        return new UserRoleDto(user.getId(), user.getUsername(), user.getRole().name());
    }
}
