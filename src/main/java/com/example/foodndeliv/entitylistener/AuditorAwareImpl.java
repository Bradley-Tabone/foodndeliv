package com.example.foodndeliv.entitylistener;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String>{

    @Override
    public Optional<String> getCurrentAuditor() {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            String custId = request.getHeader("X-Custid");

            //Test
            System.out.println("getCurrentAuditor Custid: "+custId);

            if (custId != null && !custId.isEmpty()) {
                return Optional.of(custId);
            }
        }

        return Optional.of("N/A");
    }
}
