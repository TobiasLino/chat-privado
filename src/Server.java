import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

// RF01
public class Server extends Thread {
	private final String[] sendAction = {"#AA#", "#vdlx#","gr#fkdw$"};
    private static Vector clientes;
    private Socket conexao;
    private String meuNome;

    public Server(Socket s) {
        conexao = s;
    }

    @SuppressWarnings("unchecked")
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            PrintStream saida = new PrintStream(conexao.getOutputStream());

            meuNome = entrada.readLine();
            if (meuNome == null) {
                return;
            }
            clientes.add(saida);
            String linha = entrada.readLine();
            while (linha != null && !(linha.trim().equals(""))) {
                sendToAll(saida, sendAction[0], linha);

                linha = entrada.readLine();
            }
            sendToAll(saida, sendAction[1], sendAction[2]);
            clientes.remove(saida);
            conexao.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }

    //enviar uma mensagem para todos, menos para o próprio
    @SuppressWarnings("unchecked")
// RF05
    public void sendToAll(PrintStream saida, String acao, String linha) throws IOException {
        File f = new File(".msg.txt");
        FileWriter fw = new FileWriter(f, true);
        PrintWriter pw = new PrintWriter(fw);

        Enumeration<PrintStream> e = clientes.elements();
        while (e.hasMoreElements()) {
            PrintStream chat = (PrintStream) e.nextElement();
            if (chat != saida) {
                pw.println(meuNome + acao + linha);

                fw.close();
                chat.println(meuNome + acao + linha);
            }
        }
    }

	public static void main(String[] args) {
        clientes = new Vector<PrintStream>();
        try {
            ServerSocket s = new ServerSocket(5858);
// RF02            
            while (true) {
                System.out.println("Esperando alguem se conectar...");
                Socket conexao = s.accept();
                System.out.println("Connectou!");

                Thread t = new Server(conexao);
                t.start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }
}
