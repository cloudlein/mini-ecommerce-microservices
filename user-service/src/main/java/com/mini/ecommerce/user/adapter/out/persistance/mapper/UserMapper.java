package com.mini.ecommerce.user.adapter.out.persistance.mapper;

import com.mini.ecommerce.user.adapter.out.persistance.entity.UserEntity;
import com.mini.ecommerce.user.application.dto.CreateUserRequest;
import com.mini.ecommerce.user.application.dto.UserResponse;
import com.mini.ecommerce.user.domain.model.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDomain(UserEntity userEntity);

    User toDomain(CreateUserRequest createUserRequest);

    UserEntity toEntity(User toDomain);

    UserResponse toResponse(User toDomain);

}
