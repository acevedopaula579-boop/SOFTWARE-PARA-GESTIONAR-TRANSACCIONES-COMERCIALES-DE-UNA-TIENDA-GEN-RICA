package com.tiendagenericasspv.controlador;

import com.tiendagenericasspv.dto.RespuestaMensaje;
import com.tiendagenericasspv.modelo.Usuario;
import com.tiendagenericasspv.servicio.CedulaErradaException;
import com.tiendagenericasspv.servicio.UsuarioServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST del modulo de gestion de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @PostMapping
    public ResponseEntity<RespuestaMensaje> crearUsuario(@RequestBody Usuario usuario) {
        RespuestaMensaje respuesta = usuarioServicio.crearUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/{cedula}")
    public RespuestaMensaje consultarUsuario(@PathVariable Long cedula) {
        return usuarioServicio.consultarPorCedula(cedula);
    }

    @GetMapping
    public RespuestaMensaje listarUsuarios() {
        return usuarioServicio.listarUsuarios();
    }

    @PutMapping("/{cedula}")
    public RespuestaMensaje actualizarUsuario(@PathVariable Long cedula, @RequestBody Usuario datos) {
        return usuarioServicio.actualizarUsuario(cedula, datos);
    }

    @DeleteMapping("/{cedula}")
    public RespuestaMensaje borrarUsuario(@PathVariable Long cedula) {
        return usuarioServicio.borrarUsuario(cedula);
    }

    @DeleteMapping("")
    public RespuestaMensaje borrarUsuarioSinCedula() {
        throw new CedulaErradaException("Cédula Errada");
    }
}
