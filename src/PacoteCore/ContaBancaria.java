package PacoteCore;

import java.util.ArrayList;
import java.util.List;

public class ContaBancaria {
    
        private double SaldoConta;
        private List<String> Historico;
        
                
        public ContaBancaria(double Saldo0) {
            this.SaldoConta = Saldo0;
            Historico = new ArrayList<>();
        }

        public boolean SacarDin(double ValorS) {
            if (ValorS <= SaldoConta) {
                Historico.add("Dinheiro sacado (R$" + ValorS + ");");
                SaldoConta -= ValorS;
                return true;
            } else {
                System.out.println("Você não tem saldo para realizar essa ação!");
                return false;
            }
        }

        public boolean DepositoDin(double ValorD) {
            if (ValorD > 0) {
                SaldoConta += ValorD;
                Historico.add("Valor depositado (R$" + ValorD + ");");

                return true;
            } else {

                System.out.println("O valor digitado não pode ser adicionado!");

                return false;
            }
        }
        public boolean TranferirDin (double ValorT, String Destinatario){
            if(ValorT > 0) {
                SaldoConta -= ValorT;
                Historico.add("Valor Transferido (R$" + ValorT + ") para "+ Destinatario + ";");

                return true;
            } else {

                System.out.println("O valor digitado não pode ser tranferido! ao " + Destinatario + ".");

                return false;
            }
            
        }

        public double getSaldoConta() {
            return SaldoConta;
        }

        public List<String> getHistorico() {
            return Historico;
        }
}
