package xavier.ricardo.softws.dao;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.naming.NamingException;

import xavier.ricardo.softws.tipos.Anexo;
import xavier.ricardo.softws.tipos.Imagem;
import xavier.ricardo.softws.tipos.ImagemAgenda;

public class AnexoDao {

	public static List<Anexo> getAnexos(String fornecedor, String data, int orcamento) throws SQLException, ParseException, NamingException, IOException {

		String sql = "select COD_ANEXO, DES_ARQ_ANEXO, DES_CONTEUDO "
				+ "from ANEXOS_ORCAMENTO "
				+ "where COD_FORNECEDOR=? and DAT_ORCAMENTO=? and COD_ORCAMENTO=? ";

		Connection bd = BancoDados.conecta();
		PreparedStatement cmd = bd.prepareStatement(sql);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		List<Anexo> anexos = new ArrayList<Anexo>();

		cmd.setString(1, fornecedor);
		cmd.setDate(2, new java.sql.Date(df.parse(data).getTime()));
		cmd.setInt(3, orcamento);

		ResultSet cursor = cmd.executeQuery();

		while (cursor.next()) {

			String codigo = cursor.getString("COD_ANEXO");
			String descricao = cursor.getString("DES_ARQ_ANEXO");
			if (descricao.toLowerCase().trim().endsWith(".pdf") || descricao.toLowerCase().trim().endsWith(".jpg")) {

				Anexo anexo = new Anexo();
				anexo.setCodigo(codigo);
				anexo.setDescricao(descricao);
				try {

					String arq = String.format("/usr/local/tomcat/webapps/ROOT/soft/%s%d%s.%s",
							fornecedor, orcamento, codigo.replace(" ", ""),
							descricao.toLowerCase().trim().endsWith(".pdf") ? "pdf" : "jpg");
					System.out.println(arq);
					File pdf = new File(arq);

					if (!pdf.exists()) {
						arq = String.format("/Program Files (x86)/Apache Software Foundation/Tomcat 9.0/webapps/ROOT/soft/%s%d%s.%s",
								fornecedor, orcamento, codigo.replace(" ", ""),
								descricao.toLowerCase().trim().endsWith(".pdf") ? "pdf" : "jpg");
						System.out.println(arq);
						pdf = new File(arq);

					}

					InputStream bs = cursor.getBlob("DES_CONTEUDO").getBinaryStream();
					OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pdf), "ISO-8859-1");
					int ch;
					while ((ch = bs.read()) != -1) {
						writer.write(ch);
					}
					bs.close();
					writer.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
				anexos.add(anexo);
			}
		}

		cursor.close();
		cmd.close();
		bd.close();

		return anexos;
	}

	public static List<Anexo> getAnexosAgenda(String usuario, String data) throws SQLException, ParseException, NamingException, IOException {

		String sql = "select COD_ANEXO, DES_ARQ_ANEXO, DES_CONTEUDO "
				+ "from ANEXOS "
				+ "where COD_USUARIO=? and DAT_AGENDAMENTO=?";

		Connection bd = BancoDados.conecta();
		PreparedStatement cmd = bd.prepareStatement(sql);

		List<Anexo> anexos = new ArrayList<Anexo>();

		cmd.setString(1, usuario);
		cmd.setString(2,data);

		ResultSet cursor = cmd.executeQuery();

		while (cursor.next()) {

			String codigo = cursor.getString("COD_ANEXO");
			String descricao = cursor.getString("DES_ARQ_ANEXO");
			if (descricao.toLowerCase().trim().endsWith(".pdf") || descricao.toLowerCase().trim().endsWith(".jpg")) {

				Anexo anexo = new Anexo();
				anexo.setCodigo(codigo);
				anexo.setDescricao(descricao);
				try {

					String arq = String.format("/usr/local/tomcat/webapps/ROOT/soft/%s%s.%s",
							usuario, codigo.replace(" ", ""),
							descricao.toLowerCase().trim().endsWith(".pdf") ? "pdf" : "jpg");
					System.out.println(arq);
					File pdf = new File(arq);

					if (!pdf.exists()) {
						arq = String.format("/Program Files (x86)/Apache Software Foundation/Tomcat 9.0/webapps/ROOT/soft/%s%s.%s",
								usuario, codigo.replace(" ", ""),
								descricao.toLowerCase().trim().endsWith(".pdf") ? "pdf" : "jpg");
						System.out.println(arq);
						pdf = new File(arq);

					}

					InputStream bs = cursor.getBlob("DES_CONTEUDO").getBinaryStream();
					OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pdf), "ISO-8859-1");
					int ch;
					while ((ch = bs.read()) != -1) {
						writer.write(ch);
					}
					bs.close();
					writer.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
				anexos.add(anexo);
			}
		}

		cursor.close();
		cmd.close();
		bd.close();

		return anexos;
	}

	public static void anexa(Imagem imagem) throws NamingException, SQLException {

		String sql = String.format("insert into ANEXOS_ORCAMENTO values('%s', '%s', %d, '%s', '%s.jpg', ?)",
				imagem.getFornecedor(), imagem.getData(), imagem.getOrcamento(), imagem.getId(), imagem.getId());

		System.out.println(sql);
		Connection bd = BancoDados.conecta();
		PreparedStatement cmd = bd.prepareStatement(sql);
		byte[] data = Base64.getDecoder().decode(imagem.getImage64().replace("\n", ""));
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		cmd.setBinaryStream(1, stream);
		cmd.executeUpdate();
		cmd.close();

		bd.close();
	}

	public static void anexaAgenda(ImagemAgenda imagem) throws NamingException, SQLException {

		String sql = String.format("insert into ANEXOS values('%s', '%s', '%s', '%s.jpg', ?)",
				imagem.getUsuario(), imagem.getData(), imagem.getId(), imagem.getId());

		System.out.println(sql);
		Connection bd = BancoDados.conecta();
		PreparedStatement cmd = bd.prepareStatement(sql);
		byte[] data = Base64.getDecoder().decode(imagem.getImage64().replace("\n", ""));
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		cmd.setBinaryStream(1, stream);
		cmd.executeUpdate();
		cmd.close();

		bd.close();
	}
}