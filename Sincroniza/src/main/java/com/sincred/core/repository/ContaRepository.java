package com.sincred.core.repository;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.sincred.core.model.Conta;

@Component
public class ContaRepository {

	public List<Conta> readCSVfile(String nomeArquivo) {

		List<Conta> contas = new ArrayList<>();
		CSVReader csvReader = null;
		try {

			File arquivoCsv = new File(nomeArquivo);
			FileReader fileReader = new FileReader(arquivoCsv);
			
				
			csvReader = new CSVReader(fileReader);
			String[] nextline;
			csvReader.skip(1);
			csvReader.getParser();
			
			while ((nextline = csvReader.readNext()) != null) {
				String[] data = nextline[0].split(";");
				contas.add(new Conta(data[0].trim(), data[1].trim(), data[2].trim().replace(',', '.').replace("\"", ""),
						data[3].trim()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao ler arquivo csv, verifique o formato. campos que contenham vírgula devem estar envolvidos por aspas");
		} finally {
			try {
				csvReader.close();
			} catch (IOException e) {
			}
		}
		return contas;

	}

	public void writeCSVFile(List<Conta> contas, String path) {

		String[] cabecalho = { "agencia", "conta", "saldo", "status", "resultado" };
		List<String[]> linhas = new ArrayList<>();
		for (Conta conta : contas) {
			conta = outputFormat(conta);
			linhas.add(new String[] { conta.getAgencia(), conta.getConta(), conta.getSaldo(), conta.getStatus(),
					conta.isResultado()? "Sincronizada" : "Não Sincronizada" });
		}
		try {
			Writer writer = Files.newBufferedWriter(Paths.get(path));
			//configurando o writer para sobrescrever os caracteres para delimitador, quotes e escape
			CSVWriter csvWriter = new CSVWriter(writer, ';','\'', '\\', CSVWriter.DEFAULT_LINE_END);
			csvWriter.writeNext(cabecalho, false);
			csvWriter.writeAll(linhas, false);

			csvWriter.flush();
			
			csvWriter.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao escrever arquivo csv");
		}
	}

	//esse método configura o campo saldo para ter virgula no lugar de ponto e por isso ser envolvido por aspas duplas
	private Conta outputFormat(Conta conta) {
		StringBuilder builder = new StringBuilder();
		builder.append("\"")
		.append(conta.getSaldo().replace(".", ","))
		.append("\"");
		conta.setSaldo(builder.toString());
		
		return conta;
	}

}
