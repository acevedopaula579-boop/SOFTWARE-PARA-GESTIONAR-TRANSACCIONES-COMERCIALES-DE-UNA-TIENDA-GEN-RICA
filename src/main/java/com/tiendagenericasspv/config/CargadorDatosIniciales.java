package com.tiendagenericasspv.config;

import com.tiendagenericasspv.modelo.Usuario;
import com.tiendagenericasspv.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Carga el usuario administrador inicial al arrancar la aplicacion si no existe.
 */
@Component
public class CargadorDatosIniciales implements CommandLineRunner {

    private final UsuarioRepositorio usuarioRepositorio;

    @Value("${tienda.admin-inicial.cedula}")
    private Long cedulaAdministradorInicial;

    @Value("${tienda.admin-inicial.usuario}")
    private String usuarioAdministradorInicial;

    @Value("${tienda.admin-inicial.contrasena}")
    private String contrasenaAdministradorInicial;

    public CargadorDatosIniciales(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    public void run(String... argumentos) {
        if (usuarioRepositorio.existsByCedula(cedulaAdministradorInicial)) {
            return;
        }

        Usuario administradorInicial = new Usuario();
        administradorInicial.setCedula(cedulaAdministradorInicial);
        administradorInicial.setNombreCompleto("Administrador Inicial");
        administradorInicial.setCorreoElectronico("admininicial@tiendagenericasspv.com");
        administradorInicial.setUsuario(usuarioAdministradorInicial);
        administradorInicial.setContrasena(contrasenaAdministradorInicial);
        administradorInicial.setActivo(true);

        usuarioRepositorio.save(administradorInicial);
    }
}
