package co.com.pragma.api.admin;

import co.com.pragma.api.mapper.AdminMapper;
import co.com.pragma.usecase.admin.AdminUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminHandler {

    private final AdminUseCase adminUseCase;
    private final AdminMapper adminMapper;


}
