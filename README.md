Repositório para versionamento do projeto SafeWayApp

Membros:

- Eduardo Marques Severo
- Bruno da Silva Giovanelli
- Gustavo Bueno
- Felipe Gomes
- Lucas Guilherme
- Henrique

  ➤ Conceitos básicos para utilização do GitHub:

  ⚠️ OBS: Deixe o site em inglês para o tradutor não traduzir os comandos de forma errada

   1. Baixar o git no computador;
      - Acesse este site: https://git-scm.com/downloads baixe e instale a versão para seu sistema operacional;

   2. Configurar um token de acesso:
      - Este token de acesso funciona como uma senha, para por exemplo subir um projeto para este repositório, para configurar:
      - Clique em sua foto de perfil e vá em Configurações, clique em Configurações de Desenvolvedor, clique em Token de Acesso Pessoal, clique em novo token, dê um nome ao token e defina seu tempo de expiração para nunca, selecione todas as opções disponiveis, clique em criar token, O GitHub exibirá o token gerado, copie-o e guarde-o em um local seguro, pois você não conseguirá visualizá-lo novamente após sair da página.

   3. Passo para baixar o projeto (Utilizando interface gráfica):
      - Clique no botão <>código e clique em Baixar Zip, será baixado uma pasta com todos os arquivos que estão no repositório, nela estrá a versão desejada do projeto.
   
   2.1. Passo para baixar via comandos de terminal:
     - Abra o Terminal ou Prompt de Comando no seu computador, navegue até o diretório onde você deseja salvar o projeto, utilizando o comando CD e execute o seguinte comando: git clone https://github.com/Marques-Dudu/TCC.git

     - O Git irá clonar o repositório no diretório atual.
 
     - Agora entre no android Studio, clique para abrir o projeto e vá até a pasta onde o projeto foi clonado.
 
       3. Passos básicos para utilização do github:

------------------------------------------------------------------------------------------------

📌 Conceitos Fundamentais do Git e GitHub

1. Commit

Um commit é um "salvamento" do código naquele momento. Ele registra todas as alterações feitas desde o último commit e salva no histórico do repositório.

Comando para criar um commit:

 ➥ git commit -m "Descrição do que foi alterado"

💡 Dica: Sempre use mensagens descritivas para facilitar o rastreamento das mudanças.


------------------------------------------------------------------------------------------------

2. Pull

O pull é o comando usado para baixar e mesclar as últimas alterações do repositório remoto (GitHub) para o seu repositório local.

Comando para atualizar seu código com a versão mais recente do repositório remoto:

➥ git pull origin main

💡 Dica: Isso evita conflitos quando várias pessoas trabalham no mesmo projeto.


------------------------------------------------------------------------------------------------

3. Push

O push envia suas alterações locais (commits) para o repositório remoto no GitHub.

Comando para enviar as mudanças:

➥ git push origin main

💡 Dica: Sempre faça um pull antes de um push para garantir que você está trabalhando com a versão mais atualizada.


------------------------------------------------------------------------------------------------

4. Branch

Uma branch (ramificação) permite trabalhar em novas funcionalidades sem afetar o código principal. Depois de concluída e testada, a branch pode ser mesclada à principal.

Criando uma nova branch e mudando para ela:

➥ git checkout -b nome-da-branch

Enviando a branch para o GitHub:

➥ git push origin nome-da-branch

💡 Dica: O uso de branches evita problemas no código principal enquanto uma nova funcionalidade está em desenvolvimento.


-----------------------------------------------------------------------------------------------

5. Merge

O merge combina as mudanças de uma branch com outra, geralmente unindo uma branch secundária com a principal.

Para mesclar uma branch na principal:

➥ git checkout main
➥ git merge nome-da-branch

💡 Dica: O merge pode gerar conflitos, que precisam ser resolvidos manualmente antes da finalização.


-----------------------------------------------------------------------------------------------

6. Clone

O clone baixa uma cópia completa do repositório do GitHub para o seu computador.

Comando para clonar um repositório:

➥ git clone https://github.com/seu-usuario/nome-do-repositorio.git

💡 Dica: Útil quando alguém precisa começar a trabalhar no projeto pela primeira vez.


-----------------------------------------------------------------------------------------------

7. Fork

O fork cria uma cópia independente de um repositório no GitHub, permitindo contribuições sem afetar o original. O desenvolvedor pode modificar a cópia e enviar sugestões ao repositório principal via pull request.

💡 Dica: Útil em projetos open-source ou quando você não tem acesso direto ao repositório.


-----------------------------------------------------------------------------------------------

8. Pull Request

Um pull request (PR) é uma solicitação para mesclar alterações de uma branch para outra. É comumente usado em colaboração para revisão de código antes de integrar mudanças.

💡 Dica: No GitHub, você pode abrir um pull request pela interface web e adicionar revisores para aprovar as mudanças.


-----------------------------------------------------------------------------------------------

🚀 Passo a Passo para Subir uma Nova Versão do Projeto do Android Studio para o GitHub

Agora que entendemos os conceitos, vamos aplicar na prática subindo uma nova versão do projeto e mesclando-a à branch principal.



⚠️ Obs: LEMBRE DE AVISAR NO GRUPO DE WHATSAPP, ANTES DE MESCLAR UMA BRANCH NOVA A BRANCH PRINCIPAL

----------------------------------------------------------------------------------------------


1️⃣ Abrir o projeto no Android Studio

Certifique-se de que o projeto já está configurado com o Git. Caso não esteja:

Vá para VCS → Enable Version Control Integration...

Escolha Git.

Entre na aba de terminal do android studio (prompt de comandos) e digite o seguinte comando para inciar o GIT:

➫  git init 

Este comando irá criar um diretório (pasta) oculto dentro do projeto do Android Studio, onde voce irá adicionar as novas modifiçacões do projeto e irá subir para o repositório remoto do GitHub, este diretório é chamado de repositório local, pois é um repositório que está na sua máquina.

Agora conecte ao repositorio do Gihub (este) com o seguinte comando:  

➫ git remote add origin https://github.com/Marques-Dudu/TCC.git

------------------------------------------------------------------------------------------------

2️⃣ Criar uma nova branch para a versão

Antes de modificar o código, crie uma branch para a nova versão, substitua "nova-versao" por um nome com a funcionalidade que esta sendo implementada no projeto, como por exemplo: SafewayAppCHAT:

➫  git checkout -b nova-versao

------------------------------------------------------------------------------------------------

3️⃣ Fazer as alterações no código

Modifique o projeto conforme necessário.

Após terminar, verifique quais arquivos foram alterados:

➫  git status

-------------------------------------------------------------------------------------------------

4️⃣ Adicionar e confirmar as mudanças

Para adicionar os arquivos alterados ao seu repositório local:

➫  git add .

Para criar um commit:

➫  git commit -m "Implementação da nova versão"

-------------------------------------------------------------------------------------------------

5️⃣ Enviar para o GitHub

Envie as alterações para o repositório remoto do Github, substitua "nova-versao" pelo nome que foi dado a sua branch:

➫  git push origin nova-versao

------------------------------------------------------------------------------------------------

6️⃣ Criar um Pull Request no GitHub

Acesse o repositório no GitHub.

Clique em Pull Requests → New Pull Request.

Selecione a branch nova-versao para ser mesclada com main.

Adicione uma descrição e envie a solicitação.

⚠️ Obs: LEMBRE DE AVISAR NO GRUPO DE WHATSAPP, ANTES DE MESCLAR UMA BRANCH NOVA A BRANCH PRINCIPAL

------------------------------------------------------------------------------------------------

7️⃣ Revisar e Mesclar a Nova Versão

No GitHub, revise as mudanças e, se estiver tudo certo, clique em Merge Pull Request.

Depois, exclua a branch antiga, pois não será mais necessária, lembre de substituir "nova-versao" pelo nome que foi dado a sua branch:

➫  git branch -d nova-versao
➫  git push origin --delete nova-versao

⚠️ Obs: LEMBRE DE AVISAR NO GRUPO DE WHATSAPP, ANTES DE MESCLAR UMA BRANCH NOVA A BRANCH PRINCIPAL

------------------------------------------------------------------------------------------------

8️⃣ Atualizar o Código Local

Agora que a versão foi mesclada na main, todos os desenvolvedores devem atualizar seus códigos locais com:

➫  git checkout main
➫  git pull origin main

------------------------------------------------------------------------------------------------

🎯 Resumo

1. Crie uma branch para desenvolver uma nova versão.


2. Faça alterações, adicione arquivos ao Git e crie um commit.


3. Envie as alterações para o GitHub (push).


4. Abra um pull request para solicitar a integração na branch principal.


5. Mescle as mudanças (merge) e exclua a branch temporária.


6. Atualize o código local com git pull.
