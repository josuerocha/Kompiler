START
PUSHN 3
PUSHN 3
PUSHS "Nome: "
WRITES
READ
STOREL 3
PUSHS "Sobrenome: "
WRITES
READ
STOREL 4
PUSHS "!"
PUSHL 4
PUSHS " "
PUSHL 3
PUSHS "Ola, "
CONCAT
CONCAT
CONCAT
CONCAT
STOREL 5
PUSHS "1"
PUSHL 5
CONCAT
STOREL 5
PUSHL 5
WRITES
READ
ATOI
STOREL 0
READ
ATOI
STOREL 2
PUSHL 0
PUSHL 2
SUP
NOT
JZ A
JUMP B
A: PUSHL 2
STOREL 1
PUSHL 0
STOREL 2
PUSHL 1
STOREL 0
B: PUSHS "Apos a troca: "
WRITES
PUSHL 0
WRITEI
PUSHL 2
WRITEI
STOP
