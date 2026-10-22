const API_URL = '/api/produtos';

// Headers com Token JWT
const getHeaders = () => {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
};

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    window.location.href = '/login.html';
}

// ==========================================
// CONSULTA AVANÇADA
// ==========================================
document.getElementById('formFiltros').addEventListener('submit', async (e) => {
    e.preventDefault();

    const nome = document.getElementById('filtroNome').value;
    const status = document.getElementById('filtroStatus').value;
    const precoMin = document.getElementById('filtroPrecoMin').value;
    const precoMax = document.getElementById('filtroPrecoMax').value;

    // Monta os parâmetros da URL (Query Params)
    const params = new URLSearchParams();
    if (nome) params.append('nome', nome);
    if (status) params.append('status', status);
    if (precoMin) params.append('precoMin', precoMin);
    if (precoMax) params.append('precoMax', precoMax);

    try {
        const response = await fetch(`${API_URL}/consulta-avancada?${params.toString()}`, {
            headers: getHeaders()
        });
        const produtos = await response.json();
        renderizarTabela(produtos);
    } catch (error) {
        console.error('Erro na consulta:', error);
        alert('Erro ao consultar os produtos.');
    }
});

// ==========================================
// LIMPAR FILTROS
// ==========================================
function limparFiltros() {
    document.getElementById('formFiltros').reset();
    document.getElementById('tabelaConsulta').querySelector('tbody').innerHTML = '';
    document.getElementById('contadorResultados').innerText = '0 produtos encontrados';
    document.getElementById('msgVazia').style.display = 'none';
}

// ==========================================
// RENDERIZAR TABELA
// ==========================================
function renderizarTabela(produtos) {
    const tbody = document.querySelector('#tabelaConsulta tbody');
    const msgVazia = document.getElementById('msgVazia');
    const contador = document.getElementById('contadorResultados');
    
    tbody.innerHTML = '';
    contador.innerText = `${produtos.length} produto(s) encontrado(s)`;

    if (produtos.length === 0) {
        msgVazia.style.display = 'block';
        return;
    } else {
        msgVazia.style.display = 'none';
    }

    produtos.forEach(p => {
        const tr = document.createElement('tr');
        
        const estoqueBaixo = p.quantidade < p.quantidadeMinima;
        const estoqueZerado = p.quantidade === 0;
        
        let statusBadge = '<span class="badge badge-ok">✅ OK</span>';
        if (estoqueZerado) {
            statusBadge = '<span class="badge badge-baixo">❌ Zerado</span>';
        } else if (estoqueBaixo) {
            statusBadge = '<span class="badge badge-baixo">⚠️ Baixo</span>';
        }

        tr.innerHTML = `
            <td>${p.id}</td>
            <td><strong>${p.nome}</strong></td>
            <td>${p.descricao || '-'}</td>
            <td>${p.quantidade}</td>
            <td>${p.quantidadeMinima}</td>
            <td>R$ ${p.preco.toFixed(2).replace('.', ',')}</td>
            <td>${statusBadge}</td>
        `;
        tbody.appendChild(tr);
    });
	
	

}

async function gerarRelatorioPDF() {
    try {
        const token = localStorage.getItem('token');
        
        // Mostra um feedback visual (opcional)
        const btn = event.target;
        const textoOriginal = btn.innerText;
        btn.innerText = 'Gerando...';
        btn.disabled = true;

        const response = await fetch('/api/relatorios/produtos-dynamic', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Erro ao gerar relatório');
        }

        // Converte a resposta em um Blob (arquivo binário)
        const blob = await response.blob();
        
        // Cria um link temporário para forçar o download
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'relatorio_produtos.pdf'; // Nome do arquivo que será baixado
        document.body.appendChild(a);
        a.click();
        
        // Limpa o link temporário
        a.remove();
        window.URL.revokeObjectURL(url);

        // Restaura o botão
        btn.innerText = textoOriginal;
        btn.disabled = false;

    } catch (error) {
        console.error('Erro ao gerar relatório:', error);
        alert('Erro ao gerar o relatório. Verifique se você está logado.');
        
        // Restaura o botão em caso de erro
        const btn = event.target;
        btn.innerText = '📄 Gerar Relatório PDF';
        btn.disabled = false;
    }
}