package com.mini.ecommerce.user.adapter.out.persistance.mapper;

import com.mini.ecommerce.user.application.dto.user.CreateUserRequest;
import com.mini.ecommerce.user.application.dto.user.UserResponse;
import com.mini.ecommerce.user.domain.model.User;
import com.mini.ecommerce.user.adapter.out.persistance.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDomain(CreateUserRequest request);
    UserResponse toResponse(User user);
    UserEntity toEntity(User user);
    User toDomain(UserEntity entity);
}
