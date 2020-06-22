import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Client extends Thread {
	// RF03
	public static Cesar cripter = new Cesar();
	public static boolean done = false;
    private static Socket conexao;
    
    public Client(Socket s){
        conexao = s;
    }

    public void run(){
        try{
            BufferedReader entrada = new BufferedReader(
				new InputStreamReader(conexao.getInputStream())
			);
			receiveMessage(entrada);
        }catch(IOException e){
            System.out.println("IOException> " + e);
        } finally {
			done = true;
		}
	}
	
	private void receiveMessage(BufferedReader entrada) {
		try {
			String linha;
			while(true){
				linha = entrada.readLine();
				if(linha == null){
					System.out.println("Conexao encerrada!");
					break;
				}
				// RF03 RF06
				String decoded = cripter.decrypt(linha);
				System.out.println();
				System.out.println(decoded);
			}
		} catch (IOException e) {
            System.out.println("IOException> " + e);
		}
	}

	public static void sendMessage(BufferedReader teclado, PrintStream saida) {
		String linha;
		try {
			while(true){
				System.out.print("> ");
				linha = teclado.readLine();
				if(done) break;
				// RF03 RF04
				String message = cripter.encrypt(linha);
				saida.println(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    public static void main(String[] args) {
    	System.out.println("Starting...");
        try{
            Socket conexao = new Socket("localhost", 5858);

            PrintStream saida = new PrintStream(conexao.getOutputStream());
            System.out.println("Connected!");
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
			// RF03 RF04
			saida.println(cripter.encrypt(teclado.readLine()));
            Thread t = new Client(conexao);
			t.start();
			sendMessage(teclado, saida);
        }catch(IOException e){
            System.out.println("Exception do main");
            System.out.println("IOException: " + e);
        }
    }
}

class Cesar {
	private final int key = 3;
	
	public String encrypt(String text) {
		int textSize = text.length();
		StringBuilder encrypted = new StringBuilder();
		
		for(int index = 0; index < textSize; ++index) {
            int ascii = ((int) text.charAt(index)) + key;
            if (isUppercase(text, index))
            	encryptUppercase(text, index, ascii);
            if (isLowercase(text, index))
            	encryptLowercase(text, index, ascii);
  
            encrypted.append((char) ascii);  
        }
		return encrypted.toString();
	}
	
	public String decrypt(String encrypted) {
		StringBuilder decrypted = new StringBuilder();
		int textSize = encrypted.length();
		
		for (int index = 0; index < textSize; ++index) {
			int dec = ((int) encrypted.charAt(index)) - key;
			if (isUppercase(encrypted, index))
				decryptUppercase(encrypted, index, dec);
			if (isLowercase(encrypted, index))
				decryptLowercase(encrypted, index, dec);
			
			decrypted.append((char) dec);
		}
		
		return decrypted.toString();
	}
	
	private void encryptUppercase(String text, int index, int ascii) {
		eUpper(text, index, ascii, 0, 0);
	}
	private void eUpper(String text, int index, int ascii, int total, int m) {
		if (ascii <= 90) return;
    	eUpper(text, index, 64 + m, text.charAt(index) + key, total - 90);
	}
	
	private void encryptLowercase(String text, int index, int ascii) {
        eLower(text, index, ascii, 0, 0);
	}
	private void eLower(String text, int index, int ascii, int total, int m) {
		if (ascii <= 122) return;
		eLower(text, index, 96 + m, text.charAt(index) + key, total - 122);
	}
	
	private void decryptUppercase(String text, int index, int ascii) {
		dUpper(text, index, ascii, 0, 0);
	}
	private void dUpper(String text, int index, int ascii, int total, int m) {
		if (ascii >= 90) return;
		dUpper(text, index, 64 - m, text.charAt(index) - key, total - 122);
	}
	
	private void decryptLowercase(String text, int index, int ascii) {
		dLower(text, index, ascii, 0, 0);
	}
	private void dLower(String text, int index, int ascii, int total, int m) {
		if (ascii >= 90) return;
		dLower(text, index, 96 - m, text.charAt(index) - key, total - 122);
	}
	
	private boolean isUppercase(String text, int index) {
		return ((int) text.charAt(index)) > 65 && ((int) text.charAt(index)) <  90;
	}
	private boolean isLowercase(String text, int index) {
		return ((int) text.charAt(index)) > 97 && ((int) text.charAt(index)) <  122;
	}
}
