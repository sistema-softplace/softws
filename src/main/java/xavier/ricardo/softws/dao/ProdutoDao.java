package xavier.ricardo.softws.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;

import xavier.ricardo.softws.tipos.Produto;
import xavier.ricardo.softws.utils.Sql;

public class ProdutoDao {

	public static void main(String[] args) {
		calculaFormula("-027,10+018,62+IPI+010+030", 6961.64, 5, 0., 0.);
		calculaFormula("-051-006+IPI+010+052", 6961.64, 5, 0., 0.);

	}

	public static Produto getProduto(String fornecedor, String codigo, String subCodigo, String tabela, String caracteristica) throws NamingException, SQLException {

		Connection bd = BancoDados.conecta();

		Produto produto = new Produto();
		produto.setFornecedor(fornecedor);
		produto.setCodigo(codigo);
		produto.setSubCodigo(subCodigo);
		produto.setTabela(tabela);
		produto.setCaracteristica(caracteristica);

		String sql = String.format("select DES_PRODUTO "
						+ "from PRODUTOS "
						+ "where COD_PARCEIRO='%s' and COD_PRODUTO='%s' and SUB_CODIGO='%s' ",
						fornecedor, codigo, subCodigo);
		System.out.println(sql);
		Statement cmd = bd.createStatement();
		ResultSet cursor = cmd.executeQuery(sql);
		
		if (cursor.next()) {
			String descricao = Sql.getString(cursor, "DES_PRODUTO");
			produto.setDescricao(descricao);
		}
		
		cursor.close();
		cmd.close();

		double preco = calculaPreco(bd, fornecedor, codigo, subCodigo, tabela, caracteristica);
		produto.setPreco(preco);

		bd.close();
		return produto;
	}

	private static double calculaPreco(Connection bd, String fornecedor, String codigo, String subCodigo, String tabela, String caracteristica) throws SQLException {

		double precoTabela = getPrecoTabela(bd, fornecedor, codigo, subCodigo, tabela);
		double frete = getFrete(bd, fornecedor, caracteristica);
		String formula = getFormula(bd, fornecedor, caracteristica);
		double ipi = getIpi(bd, fornecedor, codigo, subCodigo);
		double preco = calculaFormula(formula, precoTabela, ipi, frete, 0.);
		return preco;
	}

	private static double calculaFormula(String formula, double valor, double ipi, double frete, double freteReal) {

		formula = formula.replace(",", ".");
		int length = formula.trim().length();
		for (int i = 0; i < length; i += 4) {
			int decimais = 0;
			if ((i + 4) < length && formula.charAt(i + 4) == '.') {
				decimais = 3;
			}

			if (formula.charAt(i) == 'x') {
				double d = Double.parseDouble(formula.substring(i + 1, i + 1 + 3 + decimais));
				valor *= d;

			} else {
				if (formula.substring(i, i + 4).equals("+IPI")) {
					valor += valor * ipi / 100.;

				} else if (formula.substring(i, i + 4).equals("+FRE")) {
					valor += valor * frete / 100.;

				} else if (formula.substring(i, i + 4).equals("+FRR")) {
					valor += freteReal;

				} else {
					double d = Double.parseDouble(formula.substring(i, i + 4 + decimais));
					valor += valor * d / 100.;
				}

			}
			i += decimais;
		}
		return valor;
	}

	private static double getPrecoTabela(Connection bd, String fornecedor, String codigo, String subCodigo, String tabela) throws SQLException {

		double preco = 0;
		String sql = String.format("select VLR_PRECO "
						+ "from PRECOS "
						+ "where COD_PARCEIRO='%s' and COD_TABELA='%s' and COD_PRODUTO='%s' and SUB_CODIGO='%s' ",
						fornecedor, tabela, codigo, subCodigo);
		System.out.println(sql);
		Statement cmd = bd.createStatement();
		ResultSet cursor = cmd.executeQuery(sql);

		if (cursor.next()) {
			preco = cursor.getDouble("VLR_PRECO");
		}

		cursor.close();
		cmd.close();
		return preco;
	}

	private static double getFrete(Connection bd, String fornecedor, String caracteristica) throws SQLException {

		double frete = 0;
		String sql = String.format("select PER_FRETE "
						+ "from CARACTERISTICAS "
						+ "where COD_FORNECEDOR='%s' and COD_CARACTERISTICA='%s' ",
						fornecedor, caracteristica);
		System.out.println(sql);
		Statement cmd = bd.createStatement();
		ResultSet cursor = cmd.executeQuery(sql);

		if (cursor.next()) {
			frete = cursor.getDouble("PER_FRETE");
			if (cursor.wasNull()) {
				frete = 0;
			}
		}

		cursor.close();
		cmd.close();
		return frete;
	}

	private static String getFormula(Connection bd, String fornecedor, String caracteristica) throws SQLException {

		String formula = "";
		String sql = String.format("select DES_FORMULA "
						+ "from CARACTERISTICAS "
						+ "where COD_FORNECEDOR='%s' and COD_CARACTERISTICA='%s' ",
						fornecedor, caracteristica);
		System.out.println(sql);
		Statement cmd = bd.createStatement();
		ResultSet cursor = cmd.executeQuery(sql);

		if (cursor.next()) {
			formula = Sql.getString(cursor, "DES_FORMULA");
		}

		cursor.close();
		cmd.close();
		return formula;
	}

	private static double getIpi(Connection bd, String fornecedor, String codigo, String subCodigo) throws SQLException {

		double ipi = 0;
		String sql = String.format("select PER_IPI "
						+ "from PRODUTOS "
						+ "where COD_PARCEIRO='%s'and COD_PRODUTO='%s' and SUB_CODIGO='%s' ",
						fornecedor, codigo, subCodigo);
		System.out.println(sql);
		Statement cmd = bd.createStatement();
		ResultSet cursor = cmd.executeQuery(sql);

		if (cursor.next()) {
			ipi = cursor.getDouble("PER_IPI");
		}

		cursor.close();
		cmd.close();
		return ipi;
	}
}
