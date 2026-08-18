package br.com.mozart.bilheteria_digital.assento.service;

import br.com.mozart.bilheteria_digital.assento.domain.Assento;
import br.com.mozart.bilheteria_digital.assento.dto.AssentoResponse;
import br.com.mozart.bilheteria_digital.assento.repository.AssentoRepository;
import br.com.mozart.bilheteria_digital.evento.domain.Evento;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssentoService {

    private final AssentoRepository assentoRepository;

    public AssentoService(AssentoRepository assentoRepository) {
        this.assentoRepository = assentoRepository;
    }

    public void gerarAssentosParaEvento(Evento evento) {
        if (!evento.possuiAssentos() || assentoRepository.existsByEvento_Id(evento.getId())) {
            return;
        }

        List<Assento> assentos = new ArrayList<>();
        int quantidadePorFileira = 10;

        for (int i = 0; i < evento.getCapacidade(); i++) {
            int indiceFileira = i / quantidadePorFileira;
            int numero = (i % quantidadePorFileira) + 1;
            String fileira = String.valueOf((char) ('A' + indiceFileira));

            assentos.add(new Assento(evento, "UNICO", fileira, numero));
        }

        assentoRepository.saveAll(assentos);
    }

    public List<AssentoResponse> listarAssentosDoEvento(Long eventoId) {
        return assentoRepository.findByEvento_Id(eventoId)
                .stream()
                .map(AssentoResponse::from)
                .toList();
    }
}
