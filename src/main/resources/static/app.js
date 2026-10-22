const API_URL = '/api/produtos';

// ==========================================
// CONFIGURAÇÃO DE AUTENTICAÇÃO (Spring Security)
// ==========================================
// Se você configurou usuário/senha no application.properties, use-os aqui.
// Se mudou no arquivo properties, atualize aqui também.
// Remove as constantes AUTH_USER e AUTH_PASS antigas
// E substitua a função getHeaders por esta:

const getHeaders = () => {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
};

// Adicione também uma função de logout
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    window.location.href = '/login.html';
}

// ==========================================
// FUNÇÕES DA API
// ==========================================

// 1. Listar todos os produtos
async function carregarProdutos() {
    try {
        const response = await fetch(API_URL, { headers: getHeaders() });
        
        if (response.status === 401 || response.status === 403) {
            alert('Erro de autenticação. Verifique o usuário e senha no arquivo app.js');
            return;
        }

        const produtos = await response.json();
        renderizarTabela(produtos);
    } catch (error) {
        console.error('Erro ao carregar produtos:', error);
        alert('Erro ao conectar com o servidor.');
    }
}

// 2. Buscar produto por nome
async function buscarProdutos() {
    const termo = document.getElementById('inputBusca').value.trim();
    
    if (termo === '') {
        carregarProdutos();
        return;
    }

    try {
        const response = await fetch(`${API_URL}/buscar?nome=${encodeURIComponent(termo)}`, { headers: getHeaders() });
        const produtos = await response.json();
        renderizarTabela(produtos);
    } catch (error) {
        console.error('Erro na busca:', error);
    }
}

// 3. Cadastrar ou Atualizar produto
document.getElementById('formProduto').addEventListener('submit', async (e) => {
    e.preventDefault();

    const id = document.getElementById('produtoId').value;
    const produto = {
        nome: document.getElementById('nome').value,
        descricao: document.getElementById('descricao').value,
        quantidade: parseInt(document.getElementById('quantidade').value),
        quantidadeMinima: parseInt(document.getElementById('quantidadeMinima').value),
        preco: parseFloat(document.getElementById('preco').value)
    };

    const metodo = id ? 'PUT' : 'POST';
    const url = id ? `${API_URL}/${id}` : API_URL;

    try {
        const response = await fetch(url, {
            method: metodo,
            headers: getHeaders(),
            body: JSON.stringify(produto)
        });

        if (response.ok) {
            alert(id ? 'Produto atualizado com sucesso!' : 'Produto cadastrado com sucesso!');
            cancelarEdicao();
            carregarProdutos();
        } else {
            alert('Erro ao salvar o produto.');
        }
    } catch (error) {
        console.error('Erro ao salvar:', error);
    }
});

// 4. Preparar edição
function prepararEdicao(id, nome, descricao, quantidade, qtdMinima, preco) {
    document.getElementById('produtoId').value = id;
    document.getElementById('nome').value = nome;
    document.getElementById('descricao').value = descricao || '';
    document.getElementById('quantidade').value = quantidade;
    document.getElementById('quantidadeMinima').value = qtdMinima;
    document.getElementById('preco').value = preco;

    document.getElementById('formTitle').innerText = 'Editar Produto';
    document.getElementById('btnSalvar').innerText = 'Salvar Alterações';
    document.getElementById('btnCancelar').style.display = 'inline-block';

    // Rola a tela para o formulário
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// 5. Cancelar edição
function cancelarEdicao() {
    document.getElementById('formProduto').reset();
    document.getElementById('produtoId').value = '';
    
    document.getElementById('formTitle').innerText = 'Cadastrar Novo Produto';
    document.getElementById('btnSalvar').innerText = 'Cadastrar Produto';
    document.getElementById('btnCancelar').style.display = 'none';
}

// 6. Deletar produto
async function deletarProduto(id) {
    if (!confirm('Tem certeza que deseja excluir este produto?')) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'DELETE',
            headers: getHeaders()
        });

        if (response.ok) {
            alert('Produto excluído com sucesso!');
            carregarProdutos();
        } else {
            alert('Erro ao excluir o produto.');
        }
    } catch (error) {
        console.error('Erro ao deletar:', error);
    }
}

// ==========================================
// RENDERIZAÇÃO DA TABELA
// ==========================================
function renderizarTabela(produtos) {
    const tbody = document.querySelector('#tabelaProdutos tbody');
    const msgVazia = document.getElementById('mensagemVazia');
    tbody.innerHTML = '';

    if (produtos.length === 0) {
        msgVazia.style.display = 'block';
        return;
    } else {
        msgVazia.style.display = 'none';
    }

    produtos.forEach(p => {
        const tr = document.createElement('tr');
        
        // Verifica se o estoque está baixo
        const estoqueBaixo = p.quantidade < p.quantidadeMinima;
        const statusBadge = estoqueBaixo 
            ? '<span class="badge badge-baixo">⚠️ Estoque Baixo</span>' 
            : '<span class="badge badge-ok">✅ OK</span>';

        tr.innerHTML = `
            <td>${p.id}</td>
            <td><strong>${p.nome}</strong></td>
            <td>${p.descricao || '-'}</td>
            <td>${p.quantidade}</td>
            <td>${p.quantidadeMinima}</td>
            <td>R$ ${p.preco.toFixed(2).replace('.', ',')}</td>
            <td>${statusBadge}</td>
            <td>
                <button class="btn-edit" onclick="prepararEdicao(${p.id}, '${p.nome}', '${p.descricao || ''}', ${p.quantidade}, ${p.quantidadeMinima}, ${p.preco})">Editar</button>
                <button class="btn-delete" onclick="deletarProduto(${p.id})">Excluir</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Inicializa a tabela quando a página carrega
document.addEventListener('DOMContentLoaded', carregarProdutos);