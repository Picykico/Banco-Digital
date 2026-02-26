package PacoteCore;

import java.util.Arrays;

public class Usuario {
    private int id;
    private String NomeUsu;
    private String cpf;
    private char[] SenhaUsu;
    private char[] Senha4Usu;
    private int TSenha4Usu = 0;
    private boolean Bloqueado = false;
    
//        Classe do Usuario
    public Usuario(int id,String NomeUsu,String cpf ,char[] SenhaUsu, char[] Senha4Usu) {
        this.id = id;
        this.NomeUsu = NomeUsu;
        this.cpf = cpf;
        this.SenhaUsu = SenhaUsu.clone();
        this.Senha4Usu = Senha4Usu.clone();
        
    }
    public int getid(){
        return id;
    }
    
    public String getCPF(){
        return cpf;
    }
    
    public boolean isBloqueado(){
        return Bloqueado;
    }
    
    public String getNomeUsu() {
        return NomeUsu;
    }

    public boolean ValidarSenha(char[] TSenha) {
        return Arrays.equals(SenhaUsu, TSenha); //Este boolean vai retornar true se a SenhaUsu for igual a TSenha
    }
    
    public boolean ValidarSenha4(char[] TSenha4){
        if(Bloqueado) return false;
        
        if(Arrays.equals(Senha4Usu, TSenha4)){
            TSenha4Usu = 0;
            return true;
        }else{
            TSenha4Usu++;
            
            if(TSenha4Usu == 3){
                Bloqueado = true;
            }
            return false;
        }
        
    }

}
