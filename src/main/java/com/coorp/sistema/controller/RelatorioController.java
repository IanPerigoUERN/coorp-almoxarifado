package com.coorp.sistema.controller;

import com.coorp.sistema.model.Produto;
import com.coorp.sistema.repository.ProdutoRepository;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping("/produtos-dynamic")
    public ResponseEntity<byte[]> gerarRelatorioProdutos() {
        List<Produto> produtos = produtoRepository.findAll();

        // 1. Criar a fonte de dados (DRDataSource)
        // Os nomes dos campos devem ser os mesmos usados nas colunas
        DRDataSource dataSource = new DRDataSource("nome", "descricao", "quantidade", "preco");
        
        for (Produto p : produtos) {
            dataSource.add(
                p.getNome(),
                p.getDescricao() != null ? p.getDescricao() : "",
                p.getQuantidade(),
                BigDecimal.valueOf(p.getPreco())
            );
        }

        // 2. Construir o relatório
        JasperReportBuilder report = report();
        
        try {
            report
                .title(cmp.text("Relatório de Produtos - Coorp").setStyle(stl.style().bold().setFontSize(16)))
                .pageFooter(cmp.pageXofY())
                .columns(
                    col.column("Nome", "nome", type.stringType()),
                    col.column("Descrição", "descricao", type.stringType()),
                    col.column("Qtd.", "quantidade", type.integerType()),
                    col.column("Preço (R$)", "preco", type.bigDecimalType())
                )
                .setDataSource(dataSource);

            // 3. Exportar para PDF em memória
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            report.toPdf(outputStream);
            
            byte[] pdfBytes = outputStream.toByteArray();

            // 4. Configurar a resposta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "relatorio_produtos.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (DRException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}