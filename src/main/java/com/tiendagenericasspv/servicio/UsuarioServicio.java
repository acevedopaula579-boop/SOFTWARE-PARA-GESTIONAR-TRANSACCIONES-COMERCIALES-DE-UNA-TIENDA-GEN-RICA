package com.tiendagenericasspv.servicio;

import com.tiendagenericasspv.dto.PeticionLogin;
import com.tiendagenericasspv.dto.RespuestaMensaje;
import com.tiendagenericasspv.modelo.Usuario;
import com.tiendagenericasspv.repositorio.UsuarioRepositorio;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio con la logica de negocio del modulo de login y gestion de usuarios.
 */
@Service
public class UsuarioServicio {

    private static final String ADMIN_INICIAL = "admininicial";

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    /**
     * Autentica un usuario por su nombre de usuario y contrasena.
     * Solo los usuarios activos pueden iniciar sesion.
     */
    public RespuestaMensaje iniciarSesion(PeticionLogin peticionLogin) {
        if (estaVacio(peticionLogin.getUsuario()) || estaVacio(peticionLogin.getContrasena())) {
            throw new UsuarioInexistenteException("usuario o contraseña errados, intente de nuevo");
        }

        Optional<Usuario> opcional = usuarioRepositorio.findByUsuario(peticionLogin.getUsuario());
        if (opcional.isPresent()) {
            Usuario usuario = opcional.get();
            if (Boolean.TRUE.equals(usuario.getActivo())
                    && usuario.getContrasena().equals(peticionLogin.getContrasena())) {
                return new RespuestaMensaje(true, "Ingreso exitoso al sistema");
            }
        }

        throw new UsuarioInexistenteException("usuario o contraseña errados, intente de nuevo");
    }

    /**
     * Crea un nuevo usuario. Todos los campos son obligatorios.
     * Regla de negocio: al crear el primer usuario real (distinto de admininicial),
     * el usuario admininicial queda desactivado automaticamente.
     */
    public RespuestaMensaje crearUsuario(Usuario usuario) {
        if (faltanDatosParaCrear(usuario)) {
            throw new DatosFaltantesException("Faltan datos del usuario");
        }
        if (usuarioRepositorio.existsByCedula(usuario.getCedula())) {
            throw new CedulaErradaException("Cédula Errada");
        }
        if (usuarioRepositorio.findByUsuario(usuario.getUsuario()).isPresent()) {
            throw new DatosFaltantesException("Ya existe un usuario con ese nombre de usuario");
        }

        usuario.setActivo(true);
        usuarioRepositorio.save(usuario);

        desactivarAdministradorInicial();

        return new RespuestaMensaje(true, "Usuario Creado");
    }

    /**
     * Consulta un usuario por su cedula.
     */
    public RespuestaMensaje consultarPorCedula(Long cedula) {
        if (cedula == null || cedula <= 0) {
            throw new UsuarioInexistenteException("Usuario Inexistente");
        }

        Usuario usuario = usuarioRepositorio.findById(cedula)
                .orElseThrow(() -> new UsuarioInexistenteException("Usuario Inexistente"));

        return new RespuestaMensaje(true, "datos del usuario", usuario);
    }

    /**
     * Lista todos los usuarios del sistema.
     */
    public RespuestaMensaje listarUsuarios() {
        return new RespuestaMensaje(true, "lista de usuarios", usuarioRepositorio.findAll());
    }

    /**
     * Actualiza los datos de un usuario previamente consultado por cedula.
     */
    public RespuestaMensaje actualizarUsuario(Long cedula, Usuario datos) {
        if (faltanDatosParaActualizar(datos)) {
            throw new DatosFaltantesException("Datos faltantes");
        }
        if (cedula == null || cedula <= 0) {
            throw new CedulaErradaException("Cédula Errada");
        }

        Usuario usuario = usuarioRepositorio.findById(cedula)
                .orElseThrow(() -> new UsuarioInexistenteException("Usuario Inexistente"));

        usuario.setNombreCompleto(datos.getNombreCompleto());
        usuario.setCorreoElectronico(datos.getCorreoElectronico());
        usuario.setUsuario(datos.getUsuario());
        usuario.setContrasena(datos.getContrasena());
        usuarioRepositorio.save(usuario);

        return new RespuestaMensaje(true, "Datos del Usuario Actualizados");
    }

    /**
     * Borra un usuario previamente consultado por cedula.
     */
    public RespuestaMensaje borrarUsuario(Long cedula) {
        if (cedula == null || cedula <= 0 || !usuarioRepositorio.existsByCedula(cedula)) {
            throw new CedulaErradaException("Cédula Errada");
        }

        usuarioRepositorio.deleteById(cedula);

        return new RespuestaMensaje(true, "Datos del Usuario Borrados");
    }

    private boolean faltanDatosParaCrear(Usuario usuario) {
        return usuario.getCedula() == null || usuario.getCedula() <= 0
                || estaVacio(usuario.getNombreCompleto())
                || estaVacio(usuario.getCorreoElectronico())
                || estaVacio(usuario.getUsuario())
                || estaVacio(usuario.getContrasena());
    }

    private boolean faltanDatosParaActualizar(Usuario datos) {
        return estaVacio(datos.getNombreCompleto())
                || estaVacio(datos.getCorreoElectronico())
                || estaVacio(datos.getUsuario())
                || estaVacio(datos.getContrasena());
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    /**
     * Desactiva al usuario admininicial sin borrarlo de la base de datos.
     */
    private void desactivarAdministradorInicial() {
        usuarioRepositorio.findByUsuario(ADMIN_INICIAL).ifPresent(administrador -> {
            if (Boolean.TRUE.equals(administrador.getActivo())) {
                administrador.setActivo(false);
                usuarioRepositorio.save(administrador);
            }
        });
    }
}
