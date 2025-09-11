package co.com.pragma.usecase.admin;

import co.com.pragma.model.admin.gateways.AdminRepository;

public class AdminUseCase {

    private final AdminRepository adminRepository;

    public AdminUseCase(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

}
