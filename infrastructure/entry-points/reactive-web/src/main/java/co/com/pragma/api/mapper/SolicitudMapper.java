package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.SolicitudDto;
import co.com.pragma.model.user.Solicitud;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SolicitudMapper {

    Solicitud toSolicitud(SolicitudDto dto);
}
