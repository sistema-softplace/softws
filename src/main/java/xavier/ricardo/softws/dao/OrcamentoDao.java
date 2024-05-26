package xavier.ricardo.softws.dao;

import xavier.ricardo.softws.tipos.Produto;
import xavier.ricardo.softws.utils.Sql;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrcamentoDao {

	public static Produto getItem(String fornecedor, String data, String orcamento, String area, String item) throws NamingException, SQLException {

		Connection bd = BancoDados.conecta();

		Produto produto = getProduto(bd, fornecedor, data, orcamento, area, item);

		String sql = String.format("select DES_PRODUTO "
						+ "from PRODUTOS "
						+ "where COD_PARCEIRO='%s' and COD_PRODUTO='%s' and SUB_CODIGO='%s' ",
				fornecedor, produto.getCodigo(), produto.getSubCodigo());
		System.out.println(sql);
		Statement cmd = bd.createStatement();
		ResultSet cursor = cmd.executeQuery(sql);

		if (cursor.next()) {
			String descricao = Sql.getString(cursor, "DES_PRODUTO");
			produto.setDescricao(descricao);
		}

		cursor.close();
		cmd.close();

		bd.close();
		return produto;
	}

	private static Produto getProduto(Connection bd, String fornecedor, String data, String orcamento, String area, String item) throws SQLException {

		Produto produto = new Produto();
		produto.setFornecedor(fornecedor);

		String sql = String.format("select i.COD_PRODUTO, i.SUB_CODIGO, i.VLR_PRECO, o.COD_TABELA, o.COD_CARACTERISTICA "
						+ "from ITENS i "
						+ "inner join ORCAMENTOS o on o.COD_FORNECEDOR=i.COD_FORNECEDOR and o.DAT_ORCAMENTO=i.DAT_ORCAMENTO and o.COD_ORCAMENTO=i.COD_ORCAMENTO "
						+ "where i.COD_FORNECEDOR='%s' and i.DAT_ORCAMENTO='%s' and i.COD_ORCAMENTO=%s and i.COD_AREA='%s' and i.SEQ_ITEM=%s ",
				fornecedor, data, orcamento, area, item);
		System.out.println(sql);
		Statement cmd = bd.createStatement();
		ResultSet cursor = cmd.executeQuery(sql);

		if (cursor.next()) {
			String codigo = cursor.getString("COD_PRODUTO");
			String subCodigo = cursor.getString("SUB_CODIGO");
			double preco = cursor.getDouble("VLR_PRECO");
			if (cursor.wasNull()) {
				preco = 0;
			}
			String tabela = cursor.getString("COD_TABELA");
			String caracteristica = cursor.getString("COD_CARACTERISTICA");
			produto.setCodigo(codigo);
			produto.setSubCodigo(subCodigo);
			produto.setPreco(preco);
			produto.setTabela(tabela);
			produto.setCaracteristica(caracteristica);
		}

		cursor.close();
		cmd.close();
		return produto;
	}
}
