package co.com.pragma.config;

import co.com.pragma.model.gateway.CapacidadPublisher;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.usecase.capacidad.CalcularCapacidadUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.pragma.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public CalcularCapacidadUseCase calcularCapacidadUseCase(
            SolicitudRepository solicitudRepository,
            CapacidadPublisher capacidadPublisher
    ) {
        return new CalcularCapacidadUseCase(solicitudRepository, capacidadPublisher);
    }
}

