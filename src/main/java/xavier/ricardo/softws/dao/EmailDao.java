package xavier.ricardo.softws.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import javax.naming.NamingException;

import xavier.ricardo.softws.tipos.PrmsEmail;

public class EmailDao {

	public static PrmsEmail recupera() throws NamingException, SQLException {
		Connection bd = BancoDados.conecta();
		PrmsEmail prms = recupera(bd);
		bd.close();
		return prms;
	}
	
	public static PrmsEmail recupera(Connection bd) throws SQLException {

		PrmsEmail prms = new PrmsEmail();
		
		String sql = "select REMETENTE, USUARIO, SENHA, DESTINATARIOS, ASSUNTO, TEXTO "
				+ "from EMAIL_APP ";

		PreparedStatement cmd = bd.prepareStatement(sql);

		ResultSet cursor = cmd.executeQuery();

		if (cursor.next()) {
		
			String remetente = cursor.getString("remetente");
			String usuario = cursor.getString("usuario");
			String senha = cursor.getString("senha");
			String destinatarios = cursor.getString("destinatarios");
			String assunto = cursor.getString("assunto");
			String texto = cursor.getString("texto");
			
			prms.setRemetente(remetente.trim());
			prms.setUsuario(usuario.trim());
			prms.setSenha(senha.trim());
			prms.setDestinatarios(Arrays.asList(destinatarios.split("\r\n")));
			prms.setAssunto(assunto);
			prms.setTexto(texto);
	
		}
	
		cursor.close();
		cmd.close();
		
		return prms;
		
	}
	

}
