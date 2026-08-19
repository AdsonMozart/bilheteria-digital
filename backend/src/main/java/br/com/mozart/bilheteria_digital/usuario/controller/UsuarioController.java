package br.com.mozart.bilheteria_digital.usuario.controller;

import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import br.com.mozart.bilheteria_digital.usuario.dto.UsuarioLogadoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Dados do usuario autenticado")
public class UsuarioController {

    @GetMapping("/me")
    @Operation(summary = "Buscar usuario logado")
    public ResponseEntity<UsuarioLogadoResponse> buscarUsuarioLogado(@AuthenticationPrincipal Usuario usuario){
        UsuarioLogadoResponse response = new UsuarioLogadoResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getNivelAcesso().name()
        );
        return ResponseEntity.ok(response);
    }
}
