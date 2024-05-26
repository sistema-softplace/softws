package xavier.ricardo.softws;    

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import xavier.ricardo.softws.dao.*;
import xavier.ricardo.softws.tipos.*;
import xavier.ricardo.softws.utils.PdfEncerramento;

public class SoftService {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String version() {
		return "soft v2.14.8(17/03/2024)";
	}

	@GET
	@Path("/login/{usuario}/{senha}/{versao}")
	@Produces(MediaType.TEXT_PLAIN)
	public String login(@PathParam("usuario") String usuario, @PathParam("senha") String senha,
						@PathParam("versao") String versao) {

		System.out.println(new Date() + " softws login: " + usuario + " " + versao);
		try {
			return UsuarioDao.login(usuario.toLowerCase(), senha);

		} catch (NamingException | SQLException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	@GET
	@Path("/lista/{usuario}/{data}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAgenda(@PathParam("usuario") String usuario, @PathParam("data") String data) {

		System.out.println(new Date() + " softws agenda: " + usuario + " " + data);
		try {
			Agenda agenda = new AgendaDao().lista(usuario, data);
			Gson gson = new Gson();
			return gson.toJson(agenda);

		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/listames/{usuario}/{data}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAgendaMes(@PathParam("usuario") String usuario, @PathParam("data") String data) {

		System.out.println(new Date() + " softws agendames: " + usuario + " " + data);
		try {
			AgendaMes agenda = new AgendaDao().listaMes(usuario, data);
			Gson gson = new Gson();
			return gson.toJson(agenda);

		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/listaNF/{nf}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAgendaNF(@PathParam("nf") String nf) {

		System.out.println(new Date() + " softws agendaNF: " + nf);
		try {
			Agenda agenda = new AgendaDao().listaNF(nf);
			Gson gson = new Gson();
			return gson.toJson(agenda);

		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/usuarios")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUsuarios() {

		System.out.println(new Date() + " softws usuarios");
		try {
			Usuarios usuarios = new UsuarioDao().lista();
			Gson gson = new Gson();
			return gson.toJson(usuarios);

		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@POST
	@Path("/salva/{nome}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String adicionarLista(@PathParam("nome") String nome, String json) {

		System.out.println(new Date() + " softws salva: " + nome);
		Gson gson = new Gson();
		Pdf pdf = gson.fromJson(json, Pdf.class);
		byte[] data = Base64.getDecoder().decode(pdf.getConteudo());
		try {
			OutputStream stream = new FileOutputStream("/usr/local/tomcat/webapps/ROOT/soft/pedidos/" + nome);
			stream.write(data);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "ok";
	}

	@PUT
	@Path("/reagendar/{usuario}/{data}/{previsao}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String reagendar(@PathParam("usuario") String usuario, @PathParam("data") String data, @PathParam("previsao") String previsao) {

		System.out.println(new Date() + " softws agenda reagendar " + usuario + " " + data + " " + previsao);
		try {
			AgendaDao.reagenda(usuario, data, previsao);
		} catch (SQLException | NamingException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "ok";
	}

	@POST
	@Path("/encerra")
	@Consumes(MediaType.APPLICATION_JSON)
	public String encerra(String json) {

		System.out.println(new Date() + " softws agenda encerra");
		System.out.println(json);
		try {
			Gson gson = new Gson();
			Encerramento encerramento = gson.fromJson(json, Encerramento.class);
			AgendaDao.encerra(encerramento);
			PdfEncerramento pdf = new PdfEncerramento();
			pdf.gera(json);
		} catch (SQLException | NamingException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "ok";
	}

	@POST
	@Path("/foto")
	@Consumes(MediaType.APPLICATION_JSON)
	public String foto(String json) {

		try {
			Gson gson = new Gson();
			Imagem imagem = gson.fromJson(json, Imagem.class);
			System.out.println(new Date() + " softws foto " + imagem.getFornecedor()
					+ " " + imagem.getData() + " " + imagem.getOrcamento() + " " + imagem.getId());
			AnexoDao.anexa(imagem);
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "ok";
	}

	@POST
	@Path("/fotoAgenda")
	@Consumes(MediaType.APPLICATION_JSON)
	public String fotoAgenda(String json) {

		try {
			Gson gson = new Gson();
			ImagemAgenda imagem = gson.fromJson(json, ImagemAgenda.class);
			System.out.println(new Date() + " softws foto agenda " + imagem.getUsuario()
					+ " " + imagem.getData() + " " + imagem.getId());
			AnexoDao.anexaAgenda(imagem);
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "ok";
	}

	@GET
	@Path("/pedido/{fornecedor}/{data}/{orcamento}/{pedido}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPedido(@PathParam("fornecedor") String fornecedor,
							@PathParam("data") String data,
							@PathParam("orcamento") int codOrcamento,
							@PathParam("pedido") int codPedido) {

		System.out.println(new Date() + " softws pedido: " + fornecedor + " " + data + " " + codOrcamento + " " + codPedido);
		try {
			Pedido pedido = new PedidoDao().getPedido(fornecedor, data, codOrcamento, codPedido);
			Gson gson = new Gson();
			return gson.toJson(pedido);

		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/anexos/{fornecedor}/{data}/{orcamento}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAnexos(@PathParam("fornecedor") String fornecedor,
							@PathParam("data") String data,
							@PathParam("orcamento") int codOrcamento) {

		System.out.println(new Date() + " softws getAnexos: " + fornecedor + " " + data + " " + codOrcamento);
		try {
			List<Anexo> anexos = AnexoDao.getAnexos(fornecedor, data, codOrcamento);
			Compromisso compromisso = new Compromisso();
			compromisso.setAnexos(anexos);
			Gson gson = new Gson();
			return gson.toJson(compromisso);

		} catch (NamingException | SQLException | ParseException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/anexosAgenda/{usuario}/{data}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAnexosAgenda(@PathParam("usuario") String usuario,
								  @PathParam("data") String data) {

		System.out.println(new Date() + " softws getAnexosAgenda: " + usuario + " " + data);
		try {
			List<Anexo> anexos = AnexoDao.getAnexosAgenda(usuario, data);
			Compromisso compromisso = new Compromisso();
			compromisso.setAnexos(anexos);
			Gson gson = new Gson();
			return gson.toJson(compromisso);

		} catch (NamingException | SQLException | ParseException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/produto/{fornecedor}/{codigo}/{subcodigo}/{tabela}/{caracteristica}")
	@Produces(MediaType.TEXT_HTML)
	public String getProduto(@PathParam("fornecedor") String fornecedor,
							 @PathParam("codigo") String codigo,
							 @PathParam("subcodigo") String subCodigo,
							 @PathParam("tabela") String tabela,
							 @PathParam("caracteristica") String caracteristica) {

		System.out.println(new Date() + " softws produto: " + fornecedor
				+ " " + codigo
				+ " " + subCodigo
				+ " " + tabela
				+ " " + caracteristica);

		try {
			Produto produto = ProdutoDao.getProduto(fornecedor, codigo, subCodigo, tabela, caracteristica);
			String html = "<html><meta \"charset=utf-8\"/><body>" + produto.toHtml() + "</body></html>";
			System.out.println(html);
			return html;

		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return "<html><meta \"charset=utf-8\"/><body>" + e.getMessage() + "</body></html>";
		}
	}

	@GET
	@Path("/item/{fornecedor}/{data}/{orcamento}/{area}/{item}")
	@Produces(MediaType.TEXT_HTML)
	public String getItem(@PathParam("fornecedor") String fornecedor,
						  @PathParam("data") String data,
						  @PathParam("orcamento") String orcamento,
						  @PathParam("area") String area,
						  @PathParam("item") String item) {

		System.out.println(new Date() + " softws produto: " + fornecedor
				+ " " + data
				+ " " + orcamento
				+ " " + area
				+ " " + item);

		try {
			Produto produto = OrcamentoDao.getItem(fornecedor, data, orcamento, area, item);
			String html = "<html><meta \"charset=utf-8\"/><body>" + produto.toHtml() + "</body></html>";
			System.out.println(html);
			return html;

		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return "<html><meta \"charset=utf-8\"/><body>" + e.getMessage() + "</body></html>";
		}
	}
}
