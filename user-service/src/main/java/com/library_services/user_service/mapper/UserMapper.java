package com.library_services.user_service.mapper;


public class UserMapper {

    // Convert JPA entity -> POJO
    public static com.library_services.user_service.pojo.User toPojo(com.library_services.user_service.entity.User user) {
        if (user == null) {
            return null;
        }
        return new com.library_services.user_service.pojo.User(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getFullName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    // Convert POJO -> JPA entity
    public static com.library_services.user_service.entity.User toEntity(com.library_services.user_service.pojo.User pojo) {
        if (pojo == null) {
            return null;
        }
        com.library_services.user_service.entity.User user = new com.library_services.user_service.entity.User();
        user.setId(pojo.getId());
        user.setUsername(pojo.getUsername());
        user.setEmail(pojo.getEmail());
        user.setPasswordHash(pojo.getPasswordHash());
        user.setFullName(pojo.getFullName());
        user.setCreatedAt(pojo.getCreatedAt());
        user.setUpdatedAt(pojo.getUpdatedAt());
        return user;
    }

}
