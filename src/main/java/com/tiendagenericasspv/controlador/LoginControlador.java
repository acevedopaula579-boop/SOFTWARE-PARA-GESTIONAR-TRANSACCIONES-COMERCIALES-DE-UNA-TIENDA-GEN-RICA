package com.tiendagenericasspv.controlador;

import com.tiendagenericasspv.dto.PeticionLogin;
import com.tiendagenericasspv.dto.RespuestaMensaje;
import com.tiendagenericasspv.servicio.UsuarioServicio;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST del modulo de login.
 */
@RestController
@RequestMapping("/api/login")
public class LoginControlador {

    private final UsuarioServicio usuarioServicio;

    public LoginControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @PostMapping
    public RespuestaMensaje iniciarSesion(@RequestBody PeticionLogin peticionLogin) {
        return usuarioServicio.iniciarSesion(peticionLogin);
    }
}
