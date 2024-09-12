# API de Essências

Este repositório contém uma solução para o desafio da API de Essências. A aplicação foi desenvolvida utilizando Java 17,
Spring Boot, [Bucket4j](https://bucket4j.com/) para implementação de rate limit, e Java HTTPClient para integração com a API externa.

## Executando a aplicação

Para compilar e executar, basta executar `./mvnw spring-boot:run` no root da aplicação.

Para execução dos testes use `./mvnw test`.

A definição das APIs pode ser encontrada [aqui.](/doc/openai.yaml)

Para acessar a API de essências (EssenceController), siga estas etapas:

1. Realize o login para obter o token necessário para acessar a API de essências:
```
   curl -X POST http://localhost:8080/api/v1/login \
   -H "Content-Type: application/json" \
   -d '{"username": "testuser", "password": "test"}'
``` 
2. Obtenha o token na reposta.
3. Para acessar todas as essências:

```
curl -X GET http://localhost:8080/api/v1/essences \
     -H "Authorization: Bearer YOUR_TOKEN"

```
4. Para acessar uma essência específica:
```
curl -X GET http://localhost:8080/api/v1/essences/{id} \
-H "Authorization: Bearer YOUR_JTOKEN"
```

É necessário realizar o login com o usuário `testuser` e a senha `test`, ou adicionar novos usuários em [aqui.](../src/main/resources/data.sql).

## Fora de escopo

Como se trata de uma aplicação desenvolvida para um teste, algumas simplificações foram feitas, incluindo:

* *Configuração de Credenciais:* As configurações, incluindo credenciais, foram armazenadas no arquivo `application.properties`. Em um ambiente de produção, essas informações deveriam ser gerenciadas de forma mais segura, utilizando um provider adequado para injeção na imagem.
* *Cache em Memória:* Foi utilizada uma cache em memória para simplificação. Para aplicações distribuídas, seria mais eficiente implementar uma cache descentralizada, como o Redis, para garantir um desempenho adequado e escalabilidade.
* *Testes:* Devido ao tempo limitado para o desenvolvimento, foram implementados testes apenas para o `EssenceController`. Em um cenário real, seria necessário expandir a cobertura dos testes para incluir outros componentes da aplicação.

## Principais features

A aplicação é dividida em três principais features: autenticação, controle volumétrico e cache.

### Autenticação

A autenticação na aplicação foi implementada utilizando [JWT (JSON Web Token)](https://jwt.io/). Para este teste, foi 
criada uma API de login que permite a autenticação utilizando as credenciais  usuário `testuser` e  senha `test`.

Abaixo o fluxo referente à autenticação:


![Fluxo de Autenticação](/doc/images/auth.png)

O acesso dos recursos acontece da seguinte forma:

1. O cliente realiza uma requisição à API.
2. A requisição é interceptada pelo AuthFilter, que verifica a autenticação do usuário.
3. Se o token não for encontrado ou estiver ausente, a aplicação responde com um erro.
4. Quando um token é fornecido, o AuthService realiza a validação do token e extrai o nome de usuário.
5. Se a validação do token falhar (token expirado, inválido, corrompido, usuário não encontrado), um erro é retornado.
6. Se o token for válido, o SecurityContextHolder é atualizado com as informações do usuário autenticado.
7. Em seguida, o processo padrão de autorização do Spring Security é executado.
8. Caso todas as etapas sejam bem-sucedidas, uma resposta positiva (HTTP 200) é enviada ao cliente.

### Controle volumétrico

Para implementar o controle volumétrico, foi utilizado o [Bucket4j](https://bucket4j.com/). Este controle é aplicado às 
requisições direcionadas à API de essências (EssencesController). Atualmente, qualquer acesso à API de essências é 
contabilizado. No entanto, uma alternativa seria contabilizar o acesso apenas quando a API externa é chamada, 
não quando um dado é retornado do cache. O bucket do controle volumétrico foi configurado com uma capacidade inicial 
de 5 requisições por minuto.


![Fluxo Rate Limit](/doc/images/ratelimit.png)

1. O cliente faz uma requisição para a API de essências.
2. A requisição é interceptada pelo RateLimiter, o qual verifica se há tokens disponíveis (cada token representa uma requisição permitida).
3. Se houver tokens disponíveis, o bucket consome um token e a requisição é permitida.
4. Se não houver tokens disponíveis, um erro 429 é retornado.


### Cache

A aplicação implementa cache utilizando a biblioteca [Caffeine](https://github.com/ben-manes/caffeine). Para este teste, 
foi assumido que os dados permanecerão inalterados por um período de até 30 minutos após serem inseridos no cache.

O cache mantém um máximo de 50 entradas, as quais podem ficar no cache por até 30 minutos.

![Fluxo Cache](/doc/images/cache.png)

1. O cliente faz uma requisição à API para acessar recursos de essências.
2. A aplicação verifica se o dado requisitado já está presente no cache (por exemplo, se o ID da essência já foi armazenado).
3. Se o dado estiver no cache, ele é retornado diretamente, evitando a necessidade de consultar a API externa.
4. Se o dado não estiver no cache, a aplicação faz uma requisição à API externa, obtém o dado, e o armazena no cache para futuros acessos.
