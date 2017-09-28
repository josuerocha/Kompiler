	int: a, aux$, b;
	string nome, sobrenome, msg;
	print(Nome: );
	scan (nome);
	print("Sobrenome: ");
	scan (sobrenome);
	msg = "Ola, " + nome + " " +
	sobrenome + "!";
	msg = msg + 1;
	print (msg);
	scan (a);
	scan(b);
	if (a>b) then
		aux = b;
		b = a;
		a = aux;
	end;
	print ("Apos a troca: ");
	out(a);
	out(b)
end
