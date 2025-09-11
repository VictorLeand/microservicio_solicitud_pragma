package co.com.pragma.api.validator;

import co.com.pragma.model.exception.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    public <T> T validate(T object) {
        Set<ConstraintViolation<T>> errors = validator.validate(object);

        if (errors.isEmpty()) {
            return object;
        } else {
            String errorDetails = errors.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new BusinessException(errorDetails);
        }
    }
}
