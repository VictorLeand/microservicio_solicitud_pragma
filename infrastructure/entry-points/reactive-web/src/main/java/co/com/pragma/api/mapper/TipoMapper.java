package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.TipoPrestamoDto;
import co.com.pragma.model.tipoprestamo.TipoPrestamo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TipoMapper {

    TipoPrestamo toTipo(TipoPrestamoDto dto);
}
