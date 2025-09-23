package co.com.pragma.usecase.admin;

import co.com.pragma.model.admin.gateways.AdminRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AdminUseCaseTest {

    @Test
    void sePuedeConstruirConRepositorio() {
        AdminRepository repo = mock(AdminRepository.class);

        AdminUseCase useCase = new AdminUseCase(repo);

        assertNotNull(useCase);
    }

}