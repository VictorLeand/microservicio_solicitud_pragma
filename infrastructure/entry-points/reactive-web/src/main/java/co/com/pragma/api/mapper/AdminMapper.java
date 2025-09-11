package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.AdminDto;
import co.com.pragma.model.admin.Admin;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    Admin toAdmin(AdminDto dto);


    AdminDto toDto(Admin admin);
}
