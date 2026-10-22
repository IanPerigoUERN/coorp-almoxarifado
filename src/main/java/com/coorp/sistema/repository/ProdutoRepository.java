package com.coorp.sistema.repository;

import com.coorp.sistema.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("SELECT p FROM Produto p WHERE p.quantidade < p.quantidadeMinima")
    List<Produto> findProdutosAbaixoDoMinimo();

    List<Produto> findByNomeContainingIgnoreCase(String nome);
}