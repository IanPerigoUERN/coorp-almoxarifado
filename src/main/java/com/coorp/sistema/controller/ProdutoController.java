package com.coorp.sistema.controller;

import com.coorp.sistema.model.Produto;
import com.coorp.sistema.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable(name = "id") Long id) {
        return produtoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public List<Produto> buscarPorNome(@RequestParam String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    @GetMapping("/abaixo-minimo")
    public List<Produto> abaixoDoMinimo() {
        return produtoRepository.findProdutosAbaixoDoMinimo();
    }

    @PostMapping
    public Produto cadastrar(@RequestBody Produto produto) {
        return produtoRepository.save(produto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable(name = "id") Long id,
                                             @RequestBody Produto produtoAtualizado) {
        return produtoRepository.findById(id)	
                .map(produto -> {
                    produto.setNome(produtoAtualizado.getNome());
                    produto.setDescricao(produtoAtualizado.getDescricao());
                    produto.setQuantidade(produtoAtualizado.getQuantidade());
                    produto.setQuantidadeMinima(produtoAtualizado.getQuantidadeMinima());
                    produto.setPreco(produtoAtualizado.getPreco());
                    return ResponseEntity.ok(produtoRepository.save(produto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable(name = "id") Long id) { // 👈 Adicione o name = "id"
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
 // GET - Consulta avançada com filtros
    // GET - Consulta avançada com filtros
    @GetMapping("/consulta-avancada")
    public List<Produto> consultaAvancada(
            @RequestParam(required = false, name = "nome") String nome,
            @RequestParam(required = false, name = "status") String status,
            @RequestParam(required = false, name = "precoMin") Double precoMin,
            @RequestParam(required = false, name = "precoMax") Double precoMax) {
        
        List<Produto> todos = produtoRepository.findAll();
        
        return todos.stream()
            .filter(p -> nome == null || p.getNome().toLowerCase().contains(nome.toLowerCase()))
            .filter(p -> precoMin == null || p.getPreco() >= precoMin)
            .filter(p -> precoMax == null || p.getPreco() <= precoMax)
            .filter(p -> {
                if (status == null || status.isEmpty()) return true;
                if (status.equals("baixo")) return p.getQuantidade() < p.getQuantidadeMinima();
                if (status.equals("zerado")) return p.getQuantidade() == 0;
                if (status.equals("ok")) return p.getQuantidade() >= p.getQuantidadeMinima();
                return true;
            })
            .toList();
    }
}