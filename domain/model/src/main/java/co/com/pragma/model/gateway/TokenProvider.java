package co.com.pragma.model.gateway;

import co.com.pragma.model.login.TokenPayload;
import co.com.pragma.model.exception.BusinessException;

public interface TokenProvider {


    String generateToken(Object ignore);

    TokenPayload verify(String token) throws BusinessException;
}
