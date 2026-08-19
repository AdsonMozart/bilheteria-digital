package br.com.mozart.bilheteria_digital.catalogo.controller;

import br.com.mozart.bilheteria_digital.catalogo.dto.DetalheCatalogoResponse;
import br.com.mozart.bilheteria_digital.catalogo.dto.ItemCatalogoResponse;
import br.com.mozart.bilheteria_digital.catalogo.service.CatalogoService;
import br.com.mozart.bilheteria_digital.evento.domain.OrigemExterna;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/catalogo")
@Tag(name = "Catalogo externo", description = "Busca em TMDb e Ticketmaster")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar itens no catalogo externo")
    public ResponseEntity<List<ItemCatalogoResponse>> buscar(
            @NotNull(message = "Origem e obrigatoria") @RequestParam OrigemExterna origem,
            @NotBlank(message = "Termo de busca e obrigatorio") @RequestParam String q
    ) {
        return ResponseEntity.ok(catalogoService.buscar(origem, q));
    }

    @GetMapping("/{origem}/{idExterno}")
    @Operation(summary = "Detalhar item do catalogo externo")
    public ResponseEntity<DetalheCatalogoResponse> detalhar(
            @NotNull(message = "Origem e obrigatoria") @PathVariable OrigemExterna origem,
            @NotBlank(message = "Id externo e obrigatorio") @PathVariable String idExterno
    ) {
        return ResponseEntity.ok(catalogoService.detalhar(origem, idExterno));
    }
}
