package com.sincred.core;

import java.util.ArrayList;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.sincred.core.model.Conta;
import com.sincred.core.repository.ContaRepository;
import com.sincred.core.service.ReceitaService;

@SpringBootApplication
public class SincronizacaoReceita implements CommandLineRunner {

	@Bean
	public ContaRepository getContaRepository() {
		return new ContaRepository();
	}

	@Bean
	public ReceitaService getReceitaService() {
		return new ReceitaService();
	}

	@Override
	public void run(String... args) throws Exception {
		if (args[0] != null || args[0].isEmpty()) {
			List<Conta> contas = getContaRepository().readCSVfile(args[0]);
			List<Conta> contasAux = new ArrayList<>();
			for (Conta conta : contas) {
				System.out.println("Processando conta " + conta.getConta());
				conta.setResultado(getReceitaService().atualizarConta(conta.getAgencia(),
						conta.getConta().replace("-", ""), Double.valueOf(conta.getSaldo()), conta.getStatus()));
				contasAux.add(conta);
				System.out.println("Processamento da conta " + conta.getConta() + " concluído.");

			}
			System.out.println("Processamento do lote finalizado.");
			getContaRepository().writeCSVFile(contasAux, args[0]);
		} else {
			throw new Exception("Insira um caminho completo para o arquivo CSV incluindo a extensão");
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(SincronizacaoReceita.class, args);
	}

}
