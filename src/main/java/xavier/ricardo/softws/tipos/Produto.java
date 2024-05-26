package xavier.ricardo.softws.tipos;

public class Produto {
	
	private String fornecedor;
	private String codigo;
	private String subCodigo;
	private String descricao;
	private String tabela;
	private String caracteristica;
	private double preco;
	public String getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getSubCodigo() {
		return subCodigo;
	}
	public void setSubCodigo(String subCodigo) {
		this.subCodigo = subCodigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String toHtml() {
		return "<h1>" + fornecedor + "</h1>"
				+  "<h1>" + tabela + "</h1>"
				+  "<h1>" + caracteristica + "</h1>"
				+  "<h1>" + codigo + " - " + subCodigo + "</h1>"
				+  "<h1>" + descricao + "</h1>"
				+  "<h1>" + String.format("R$ %.2f", preco) + "</h1>"
				+  "<img src=\"/foto/" + codigo.trim() + subCodigo.trim() + ".jpg\" alt=\"foto\">";

	}
	
	private String trataAcentuacao(String s) {
		return s.replaceAll("Á", "&Aacute;")
				.replaceAll("É", "&Eacute;")
				.replaceAll("Í", "&Iacute;")
				.replaceAll("Ó", "&Oacute;")
				.replaceAll("Ú", "&Uacute;")
				.replaceAll("Â", "&Atilde;")
				.replaceAll("Õ", "&Otilde;")
				.replaceAll("Â", "&Acirc;")
				.replaceAll("Ê", "&Ecirc;")
				.replaceAll("Ô", "&Ocirc")
				.replaceAll("Ç", "&Ccedil;");
	}

	public String getTabela() {
		return tabela;
	}

	public void setTabela(String tabela) {
		this.tabela = tabela;
	}

	public String getCaracteristica() {
		return caracteristica;
	}

	public void setCaracteristica(String caracteristica) {
		this.caracteristica = caracteristica;
	}

	public double getPreco() {
		return preco;
	}

	public void setPreco(double preco) {
		this.preco = preco;
	}
}
