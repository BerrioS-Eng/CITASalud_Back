package com.project.citasalud.eventHandlers;

import com.project.citasalud.mfa.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailedLoginEventListener {

    private final EmailService emailService;

    @EventListener
    @Async
    public void handleFailedLoginAttemptEvent(FailedLoginAttemptEvent event) {
        if (event.getAttemptCount() == 3){
            sendSecurityAlert(event);
        }
    }

    private void sendSecurityAlert(FailedLoginAttemptEvent event) {
        String subject = "Alerta de Seguridad - Múltiples intentos fallidos de acceso";
        String content = buildEmailContent(event);

        try {
            emailService.sendEmail(event.getEmail(), subject, content);
            //log.info("Email de alerta enviado a: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Error enviando email de alerta a: {}", event.getEmail(), e);
        }
    }

    private String buildEmailContent(FailedLoginAttemptEvent event) {
        return String.format("""
            Estimado usuario,
            
            Hemos detectado múltiples intentos fallidos de acceso a su cuenta.
            
            Detalles del incidente:
            - Fecha y hora: %s
            - Dirección IP: %s
            - Número de intentos: %d
            
            Por su seguridad, la cuenta será bloqueada por los siguientes 15 minutos.
            
            ⚠️ ADVERTENCIA DE SEGURIDAD ⚠️
            Si no ha sido usted quien intentó acceder a la cuenta, es posible que alguien esté tratando de comprometer su seguridad.
            
            RECOMENDACIÓN:
            Por su seguridad, le recomendamos cambiar su contraseña inmediatamente.
            
            Si tiene alguna duda, contacte con nuestro equipo de soporte.
            
            Saludos,
            Equipo de Seguridad
            """,
                event.getAttemptTime(),
                event.getIpAddress(),
                event.getAttemptCount()
        );
    }

}
