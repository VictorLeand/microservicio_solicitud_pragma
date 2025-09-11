package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.EstadoPrestamoDto;
import co.com.pragma.model.estadoprestamo.EstadoPrestamo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EstadoMapper {

    EstadoPrestamo toEstado(EstadoPrestamoDto dto);
}
