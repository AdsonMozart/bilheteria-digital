package br.com.mozart.bilheteria_digital.usuario.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcessoUsuario nivelAcesso;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;


    // construtores
    protected Usuario() {

    }
    public Usuario(String nome, String email, String senhaHash, AcessoUsuario nivelAcesso) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.nivelAcesso = nivelAcesso;
    }


    // gets
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public AcessoUsuario getNivelAcesso() {
        return nivelAcesso;
    }
}
