program	
	int a, aux, b;
	string nome, sobrenome, msg;
	print(nome );
	scan (nome);
	print("Sobrenome: ");
	scan (sobrenome);
	msg = "Ola, " + nome + " " +
	sobrenome + "!";
	msg = msg + "1";
	print (msg);
	scan (a);
	scan(b);
	if (a>b) then
		aux = b;
		b = a;
		a = aux;
	end
	print ("Apos a troca: ");
	print(a);
	print(b);
end
