package com.backend.shoppingcart.service.user;

import com.backend.shoppingcart.dto.UserDto;
import com.backend.shoppingcart.model.User;
import com.backend.shoppingcart.request.CreateUserRequest;
import com.backend.shoppingcart.request.UserUpdateRequest;

public interface IUserService {
    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UserUpdateRequest request, Long userId);
    User getAuthenticatedUser();

    void deleteUser(Long userId);

    UserDto convertUserToDto(User user);
}
