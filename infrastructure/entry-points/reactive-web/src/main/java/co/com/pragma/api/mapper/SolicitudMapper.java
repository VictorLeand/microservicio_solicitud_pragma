package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.SolicitudDto;
import co.com.pragma.api.dto.SolicitudResponseDto;
import co.com.pragma.model.solicitud.Solicitud;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudMapper {


    @Mapping(target = "idEstado", ignore = true)
//    @Mapping(target = "idTipoPrestamo.id", source = "idTipoPrestamo")
    Solicitud toSolicitud(SolicitudDto dto);

    SolicitudResponseDto toResponse(Solicitud model);
}
