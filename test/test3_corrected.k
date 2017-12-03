program
	int pontuacao, pontuacaoMaxima;
	string	disponibilidade;	//	Before:	int disponibilidade;
	int pontuacaoMinima; 		// Before:	string pontuacaoMinima;
	disponibilidade = "Sim";
	pontuacaoMinima = 50;
	pontuacaoMaxima = 100;
	/* Entrada de dados
	Verifica aprovação de candidatos */
	do
	print("Pontuacao Candidato: ");
		scan(pontuacao);
		print("Disponibilidade Candidato: ");
		scan(disponibilidade);

		if ((pontuacao > pontuacaoMinima) && (disponibilidade=="Sim")) then
			print("Candidato aprovado");
		else
			print("Candidato reprovado");
		end
	while (pontuacao >= 0) end
end
