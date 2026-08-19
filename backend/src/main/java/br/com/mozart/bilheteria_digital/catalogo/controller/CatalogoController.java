package br.com.mozart.bilheteria_digital.catalogo.controller;

import br.com.mozart.bilheteria_digital.catalogo.dto.DetalheCatalogoResponse;
import br.com.mozart.bilheteria_digital.catalogo.dto.ItemCatalogoResponse;
import br.com.mozart.bilheteria_digital.catalogo.service.CatalogoService;
import br.com.mozart.bilheteria_digital.evento.domain.OrigemExterna;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ItemCatalogoResponse>> buscar(
            @RequestParam OrigemExterna origem,
            @RequestParam String q
    ) {
        return ResponseEntity.ok(catalogoService.buscar(origem, q));
    }

    @GetMapping("/{origem}/{idExterno}")
    public ResponseEntity<DetalheCatalogoResponse> detalhar(
            @PathVariable OrigemExterna origem,
            @PathVariable String idExterno
    ) {
        return ResponseEntity.ok(catalogoService.detalhar(origem, idExterno));
    }
}
