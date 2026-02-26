package PacoteCore;

public class BancoDigital {

    private Usuario usuario;
    private ContaBancaria conta;

    public BancoDigital() {
        // sistema inicia sem usuário logado

    }

    public double getSaldo() {
        return conta.getSaldoConta();
    }

    public boolean Depositar(double ValorD) {
        return conta.DepositoDin(ValorD);
    }

    public boolean Sacar(double ValorS) {
        return conta.SacarDin(ValorS);
    }

    public boolean Transferir(double ValorT, String Destin) {
        return conta.TranferirDin(ValorT, Destin);
    }

    public String[] getHistorico() {
        return conta.getHistorico().toArray(new String[0]);
    }

    public ResultLogin validarUsuario(String Tcpf, char[] Tsenha, char[] Tsenha4) {

        Usuario usuario = DataBase.buscarUsuarioporCPF(Tcpf);

        if (usuario == null) {
            return ResultLogin.CPF_NAO_ENCONTRADO;
        }
        if (usuario.isBloqueado()) {
            return ResultLogin.USUARIO_BLOQUEADO;
        }
        if (!usuario.ValidarSenha(Tsenha)) {
            return ResultLogin.SENHA_INCORRETA;
        }
        if (!usuario.ValidarSenha4(Tsenha4)) {
            return ResultLogin.SENHA4_INCORRETA;
        }

        this.usuario = usuario;
        this.conta = DataBase.buscarContaporUsuarioId(String.valueOf(usuario.getid()));

        return ResultLogin.LOGIN_OK;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public ContaBancaria getConta() {
        return conta;
    }

    public ResultCadastro CadastrarUsuario(String nome, String cpf, String senha, String senha4){
        
        String cpfExi = DataBase.buscarcpf(cpf);
        
        if(cpfExi != null){
            return ResultCadastro.CPF_JA_CADASTRADO;
        }
        
        int usuarioid = DataBase.inserirUsuario(nome, cpf, senha, senha4);
        if(usuarioid == -1){
            return ResultCadastro.ERRO_INESPERADO;
        }
        
        DataBase.criarConta(usuarioid);
        
        return ResultCadastro.CADASTRO_OK;
        
    }
     
    public boolean depositarporCpf(String cpf, double valor){
        
        if(valor <= 0){
            return false;
        }
        
        Usuario usuario = DataBase.buscarUsuarioporCPF(cpf);
        
        if(usuario == null){
            return false;
        }
        
        String usuid = String.valueOf(usuario.getid());
        
        ContaBancaria conta = DataBase.buscarContaporUsuarioId(usuid);
        
        if(conta == null){
            return false;
        }
        
        return conta.DepositoDin(valor);
    }
    
    public Double getSaldoporCpf(String cpf){
        
        Usuario usuario = DataBase.buscarUsuarioporCPF(cpf);
        if(usuario == null) return null;
        
        String usuid = String.valueOf(usuario.getid());
        
        ContaBancaria conta = DataBase.buscarContaporUsuarioId(usuid); 
        if(conta == null) return null;
        
        return conta.getSaldoConta(); 
           
    }
    
    public boolean saqueporCpf(String cpf, double valor){
        
        if(valor <= 0) return false;
        
        Usuario usuario = DataBase.buscarUsuarioporCPF(cpf);
        if(usuario == null) return false;
        
        String usuid = String.valueOf(usuario.getid());
        ContaBancaria conta = DataBase.buscarContaporUsuarioId(usuid);
        
        if(conta.getSaldoConta() <= valor) return false;
        
        if(conta == null) return false;
        
        return conta.SacarDin(valor);
    }
    
    public String[] getHistoricoPorCpf(String cpf) {

    Usuario usuario = DataBase.buscarUsuarioporCPF(cpf);
    if (usuario == null) return null;

    String usuarioIdStr = String.valueOf((char) usuario.getid());
    ContaBancaria conta = DataBase.buscarContaporUsuarioId(usuarioIdStr);
    if (conta == null) return null;

    return conta.getHistorico().toArray(new String[0]);
}
}
