package br.com.mozart.bilheteria_digital.evento.domain;

import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "organizador_id", nullable = false)
    private Usuario organizador;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem_externa", nullable = false)
    private OrigemExterna origemExterna;

    @Column(name = "id_evento_externo", length = 120)
    private String idEventoExterno;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false)
    private TipoEvento tipoEvento;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "url_imagem", length = 500)
    private String urlImagem;

    @Column(name = "nome_local", nullable = false, length = 200)
    private String nomeLocal;

    @Column(name = "endereco_local", length = 300)
    private String enderecoLocal;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_capacidade", nullable = false)
    private TipoCapacidade tipoCapacidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer capacidade;

    @Column(name = "capacidade_vendida", nullable = false)
    private Integer capacidadeVendida = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEvento status = StatusEvento.RASCUNHO;

    @Column(name = "data_criacao", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dataCriacao;


    // construtores
    protected Evento() {

    }

    public Evento(Usuario organizador,
                  OrigemExterna origemExterna,
                  String idEventoExterno,
                  String titulo,
                  TipoEvento tipoEvento,
                  String descricao,
                  String urlImagem,
                  String nomeLocal,
                  String enderecoLocal,
                  LocalDateTime dataHora,
                  TipoCapacidade tipoCapacidade,
                  BigDecimal preco,
                  Integer capacidade) {
        this.organizador = organizador;
        this.origemExterna = origemExterna;
        this.idEventoExterno = idEventoExterno;
        this.titulo = titulo;
        this.tipoEvento = tipoEvento;
        this.descricao = descricao;
        this.urlImagem = urlImagem;
        this.nomeLocal = nomeLocal;
        this.enderecoLocal = enderecoLocal;
        this.dataHora = dataHora;
        this.tipoCapacidade = tipoCapacidade;
        this.preco = preco;
        this.capacidade = capacidade;
    }


    // metodos atribuitivos de comportamentos para status
    public void publicar() {
        this.status = StatusEvento.PUBLICADO;
    }

    public void cancelar() {
        this.status = StatusEvento.CANCELADO;
    }

    // metodo comparativo para status
    public boolean estaPublicado() {
        return this.status == StatusEvento.PUBLICADO;
    }

    // metodos comparativos para tipoCapacidade
    public boolean possuiAssentos() {
        return this.tipoCapacidade == TipoCapacidade.ASSENTOS;
    }

    public boolean possuiCapacidadeGeral() {
        return this.tipoCapacidade == TipoCapacidade.GERAL;
    }

    // gets
    public Long getId() {
        return id;
    }

    public Usuario getOrganizador() {
        return organizador;
    }

    public OrigemExterna getOrigemExterna() {
        return origemExterna;
    }

    public String getIdEventoExterno() {
        return idEventoExterno;
    }

    public String getTitulo() {
        return titulo;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public String getNomeLocal() {
        return nomeLocal;
    }

    public String getEnderecoLocal() {
        return enderecoLocal;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public TipoCapacidade getTipoCapacidade() {
        return tipoCapacidade;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public Integer getCapacidadeVendida() {
        return capacidadeVendida;
    }

    public StatusEvento getStatus() {
        return status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}
