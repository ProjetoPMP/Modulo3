# **Módulo 3 – Kafka Lambda Consumer (Função Assíncrona)**

Este repositório implementa o **Módulo 3** do projeto final, com o objetivo de criar um serviço do tipo **Lambda Function**, responsável por consumir eventos de um tópico Kafka de forma assíncrona, seguindo boas práticas de arquitetura orientada a eventos.

O módulo demonstra:

* Criação automática de tópicos Kafka
* Um consumidor leve e reativo baseado em Spring
* Processamento de eventos em tempo real
* Execução isolada, rápida e sem estado (estilo “lambda”)
* Utilização de Docker para orquestração
* Pipeline CI/CD para publicação automática da imagem Docker

---

# **1. Arquitetura Geral**

A arquitetura do módulo é composta pelos seguintes elementos:

```
modulo3/
│── docker-compose.yml
│── Dockerfile
│── pom.xml
│── .github/workflows/up.yml
│── src/main/java/com/mod3/lambda
│   ├── Mod3LambdaApplication.java
│   ├── config/KafkaConfig.java
│   └── consumer/KafkaLambdaListener.java
│── src/main/resources/application.yml
```

### Componentes principais:

* **Kafka Broker**: recebe e distribui eventos.
* **Lambda Listener**: função acionada automaticamente quando mensagens chegam ao tópico.
* **Configuração Kafka**: cria o tópico dinamicamente durante a inicialização.

A aplicação foi projetada para ser minimalista, rápida e escalável.

---

# **2. Fluxo de Funcionamento**

1. O Docker Compose inicia o Kafka e o serviço Lambda.
2. O Spring Boot inicializa e cria automaticamente o tópico configurado.
3. O Listener começa a escutar mensagens.
4. Quando uma nova mensagem chega, a função é executada e processa o conteúdo.
5. A saída aparece no console como demonstração do comportamento assíncrono.

Exemplo de saída:

```
A mensagem chegou: cadastro realizado com sucesso
```

---

# **3. Configuração do Kafka**

O arquivo `KafkaConfig.java` cria o tópico definido em `application.yml`:

```java
@Bean
public NewTopic topic() {
    return new NewTopic(topicName, 1, (short) 1);
}
```

O nome do tópico é externo:

```
app.kafka.topic-name: ${KAFKA_TOPIC:citizen-topic}
```

Esse padrão permite que o módulo seja reutilizado por qualquer outro sistema com diferentes tópicos.

---

# **4. Listener Assíncrono (Estilo Lambda)**

A classe `KafkaLambdaListener.java` implementa o comportamento reativo:

```java
@KafkaListener(
        topics = "${app.kafka.topic-name}",
        groupId = "${spring.kafka.consumer.group-id}"
)
public void listen(String message) {
    System.out.println("A mensagem chegou: " + message);
}
```

Principais características:

* Não mantém estado interno
* Processamento isolado
* Execução imediata para cada mensagem
* Excelente para arquiteturas de eventos

---

# **5. Execução via Docker Compose**

Com Docker instalado, execute:

```
docker-compose up --build
```

Serviços criados:

| Serviço        | Porta | Descrição                                    |
| -------------- | ----- | -------------------------------------------- |
| kafka          | 9092  | Broker Kafka padrão                          |
| modulo3-lambda | —     | Aplicação que escuta eventos automaticamente |

Variáveis fundamentais:

```
KAFKA_BOOTSTRAP_SERVERS=modulo3-kafka:29092
KAFKA_TOPIC=citizen-topic
```

A aplicação conecta automaticamente no Kafka e inicia o listener.

---

# **6. Testando o Consumo de Mensagens**

Após subir o ambiente:

### Enviar mensagens manualmente:

```
docker exec -it modulo3-kafka kafka-console-producer \
  --broker-list modulo3-kafka:29092 \
  --topic citizen-topic
```

Digite:

```
mensagem de teste
```

A aplicação exibirá:

```
A mensagem chegou: mensagem de teste
```

---

# **7. Integração Contínua – GitHub Actions**

Este módulo utiliza o workflow:

```
.github/workflows/up.yml
```

O pipeline é acionado em pushes na branch principal, sempre que arquivos essenciais são alterados:

* Dockerfile
* pom.xml
* src/**
* o próprio `up.yml`

Funções do pipeline:

* Compila o projeto
* Gera a imagem Docker
* Autentica no Docker Hub
* Publica automaticamente as imagens com as tags:

  * `latest`
  * `${{ github.run_number }}`

Exemplo de publicação automática:

```
usuario/modulo3-lambda:latest
usuario/modulo3-lambda:152
```

Essa abordagem reforça práticas modernas de DevOps, garantindo versionamento reprodutível.

---
