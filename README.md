# Event-Driven Architecture

Projeto de estudo e implementação prática de uma arquitetura orientada a eventos, com foco em **comunicação assíncrona confiável**, desacoplamento entre serviços e proteção contra falhas comuns em sistemas distribuídos.

O projeto utiliza **RabbitMQ** como Message Broker e adota uma topologia do tipo **Mediator / Orchestrator**, na qual o `workflow-service` coordena o fluxo entre os demais serviços sem acoplá-los diretamente entre si.

---

## Objetivo

O objetivo principal deste projeto é demonstrar, na prática, como construir um fluxo assíncrono entre microsserviços levando em consideração não apenas o envio de mensagens, mas também os principais desafios de confiabilidade envolvidos em uma arquitetura Event-Driven.

Entre os conceitos aplicados estão:

- Event-Driven Architecture
- Producer e Consumer
- Event Channel
- Message Broker
- Mediator / Orchestrator
- Transactional Outbox Pattern
- Consumer Acknowledgement
- Redelivery
- Retry
- Idempotência
- At Least Once Delivery
- Versionamento / Sequence Number para controle de ordenação lógica
- Processamento transacional
- Desacoplamento entre serviços

---

## Arquitetura

A arquitetura é baseada em comunicação assíncrona por meio do RabbitMQ.

Os serviços não precisam conhecer diretamente os endpoints HTTP uns dos outros. Eles publicam eventos ou comandos no broker e os consumidores interessados processam essas mensagens.

### Componentes principais

**Producer**

Serviço responsável por produzir e publicar um evento ou comando.

Exemplo:

```text
Order Service
→ publica ORDER_CREATED
```

**Consumer**

Serviço que escuta uma fila e reage à mensagem recebida.

Exemplo:

```text
Workflow Service
→ consome ORDER_CREATED
```

**Event Channel**

Canal lógico utilizado para transportar eventos entre produtores e consumidores.

Neste projeto, o RabbitMQ exerce esse papel através de exchanges, routing keys e queues.

**Message Broker**

O projeto utiliza:

```text
RabbitMQ
```

O broker é responsável por receber, rotear e entregar mensagens aos consumidores.

**Mediator / Orchestrator**

O `workflow-service` atua como mediador do fluxo.

Ele recebe eventos, consulta o estado atual do workflow, determina a próxima transição e executa as ações associadas a essa transição.

Isso evita que os serviços precisem conhecer diretamente todos os demais participantes do processo.

---

## Fluxo atual

```text
Cliente
   ↓
Order Service
   ↓
cria pedido
   ↓
ORDER_CREATED
   ↓
domain.events
   ↓
workflow.events.queue
   ↓
Workflow Service
   ↓
consulta WorkflowInstance
   ↓
identifica WorkflowTransition
   ↓
executa WorkflowTransitionAction
   ↓
┌──────────────────────────┬──────────────────────────┐
│ PROCESS_PAYMENT          │ UPDATE_ORDER_STATUS      │
↓                          ↓
domain.commands            domain.commands
↓                          ↓
payment.commands.queue     order.commands.queue
                           ↓
                           Order Service
                           ↓
                           atualiza status
```

O `workflow-service` não possui entrada REST para o fluxo de negócio. Sua entrada e saída são feitas através de mensageria AMQP.

---

## Topologia RabbitMQ

### Exchanges

```text
domain.events
domain.commands
```

`domain.events` transporta eventos de domínio.

`domain.commands` transporta comandos emitidos pelo Workflow para outros serviços.

### Queues

```text
workflow.events.queue
order.commands.queue
payment.commands.queue
```

Exemplo de binding:

```text
domain.events
    routing key: order.created
        ↓
workflow.events.queue
```

Outro exemplo:

```text
domain.commands
    routing key: order.status.update
        ↓
order.commands.queue
```

A topologia é provisionada externamente no RabbitMQ, mantendo a aplicação desacoplada da criação física das filas, exchanges e bindings.

---

# Confiabilidade da comunicação assíncrona

Uma arquitetura orientada a eventos precisa considerar que falhas podem ocorrer em vários pontos:

```text
Aplicação
↓
Banco de dados
↓
Broker
↓
Consumer
↓
Processamento
```

Por isso, o projeto aplica diferentes mecanismos de confiabilidade.

---

## Transactional Outbox Pattern

O **Transactional Outbox Pattern** evita o problema clássico de atualizar o banco e falhar antes de publicar o evento.

Sem Outbox:

```text
salva no banco ✅
↓
tenta publicar no RabbitMQ ❌
↓
evento perdido
```

Com Outbox:

```text
transação
├── altera estado da aplicação
└── grava mensagem na Outbox

COMMIT
```

Posteriormente, um scheduler busca os registros pendentes e realiza a publicação.

```text
Outbox PENDING
↓
Scheduler
↓
RabbitMQ
↓
PUBLISHED
```

Dessa forma, o dado de negócio e a intenção de publicação são persistidos na mesma transação.

---

## Retry e Redelivery

Falhas temporárias fazem parte de sistemas distribuídos.

O projeto utiliza reprocessamento para evitar que uma falha momentânea provoque perda definitiva da mensagem.

### Retry

Retry representa a política de realizar novas tentativas após uma falha.

```text
falhou
↓
retry
↓
retry
↓
retry
```

No fluxo de saída, o scheduler do Outbox continua buscando eventos `PENDING`, permitindo novas tentativas de publicação.

### Redelivery

Redelivery ocorre quando o broker entrega novamente uma mensagem que não foi confirmada com sucesso pelo consumidor.

```text
mensagem entregue
↓
processamento falha
↓
sem ACK de sucesso
↓
RabbitMQ pode entregar novamente
```

---

## Consumer Acknowledgement

O **ACK** confirma ao RabbitMQ que uma mensagem foi processada com sucesso.

Neste projeto, o Spring AMQP utiliza:

```properties
spring.rabbitmq.listener.simple.acknowledge-mode=auto
```

Com isso:

```text
RabbitMQ entrega
↓
Consumer processa
↓
transação termina com sucesso
↓
ACK
```

Se uma exceção interromper o processamento, a mensagem não é considerada processada com sucesso.

O ACK é importante para garantir que a mensagem não seja removida da fila antes da conclusão do processamento.

---

## Idempotência

Em uma arquitetura com entrega **At Least Once**, a mesma mensagem pode chegar mais de uma vez.

Por isso, os consumidores precisam ser idempotentes.

Idempotência significa:

> Processar a mesma operação mais de uma vez sem gerar efeitos colaterais duplicados.

### Redis

O `order-service` utiliza Redis em cenários de idempotência HTTP.

A chave de idempotência é armazenada com possibilidade de TTL.

```text
Idempotency-Key
↓
Redis
↓
já existe?
├── sim → evita nova execução
└── não → continua
```

A principal característica dessa abordagem é velocidade e facilidade de expiração.

Por outro lado, Redis e o banco SQL não participam, por padrão, da mesma transação atômica.

### Spring Integration + JdbcMetadataStore

Nos consumidores AMQP, o projeto utiliza:

```text
Spring Integration
+
JdbcMetadataStore
```

O `eventId` é utilizado como chave de idempotência.

```text
mensagem chega
↓
MetadataStoreSelector
↓
eventId já existe?
├── sim → ignora
└── não → processa
```

Como o `JdbcMetadataStore` utiliza o mesmo banco da aplicação, a gravação da chave pode participar da mesma transação do processamento.

No Workflow:

```text
idempotência
+
mudança de estado
+
Outbox
=
mesma transação
```

Isso permite que um rollback reverta também o registro de idempotência.

---

## At Least Once Delivery

O projeto trabalha considerando a semântica **At Least Once**.

Isso significa:

> Uma mensagem deve ser entregue pelo menos uma vez, podendo ocorrer duplicatas.

At Least Once não é apenas uma configuração isolada. Ele resulta da combinação de vários mecanismos:

```text
Outbox
+
ACK
+
Redelivery
+
Retry
+
Idempotência
```

A idempotência é essencial porque uma mensagem entregue mais de uma vez não pode causar múltiplos efeitos de negócio.

---

# Controle de mensagens fora de ordem

Em sistemas distribuídos, não é seguro assumir que duas mensagens sempre chegarão na mesma ordem em que foram produzidas.

Exemplo:

```text
version 1 → WAITING_PAYMENT
version 2 → PAYMENT_PROCESSED
```

Existe a possibilidade de:

```text
version 2 chegar primeiro
version 1 chegar depois
```

Sem proteção, o pedido poderia regredir:

```text
PAYMENT_PROCESSED
↓
WAITING_PAYMENT
```

Para evitar isso, o projeto implementa **versionamento lógico / Sequence Number**.

---

## Workflow Version

O `workflow-service` mantém uma versão na `WorkflowInstance`.

```text
INITIAL
version = 0

↓ transição

WAITING_PAYMENT
version = 1

↓ transição

próximo estado
version = 2
```

Essa versão é propagada dentro do comando enviado aos demais serviços.

Exemplo:

```json
{
  "eventType": "UPDATE_ORDER_STATUS",
  "orderId": 1,
  "version": 2,
  "eventsData": {
    "status": "PAYMENT_PROCESSED"
  }
}
```

O `order-service` armazena a última versão aplicada:

```text
workflowVersion
```

Antes de atualizar o pedido:

```text
versão recebida <= versão atual
→ ignora

versão recebida > versão atual
→ aplica atualização
```

Assim, mensagens atrasadas não conseguem sobrescrever estados mais recentes.

Esse mecanismo é diferente do `@Version` do JPA.

O `@Version` é utilizado para **Optimistic Locking**, enquanto o `workflowVersion` representa a **ordem lógica dos estados do processo distribuído**.

---

# Workflow orientado a dados

O comportamento do Workflow é definido através de três conceitos principais.

### WorkflowInstance

Representa a execução atual de um Workflow para determinado pedido.

```text
Onde o processo está?
```

Exemplo:

```text
WAITING_PAYMENT
```

### WorkflowTransition

Define qual transição deve acontecer para uma combinação de estado + evento.

```text
INITIAL + ORDER_CREATED
→ WAITING_PAYMENT
```

### WorkflowTransitionAction

Define quais ações precisam ocorrer depois de uma transição.

Exemplo:

```text
PROCESS_PAYMENT
UPDATE_ORDER_STATUS
```

Uma mesma transição pode gerar múltiplas ações.

Exemplo:

```text
ORDER_CREATED
↓
INITIAL → WAITING_PAYMENT
↓
PROCESS_PAYMENT
+
UPDATE_ORDER_STATUS
```

As actions são configuradas no banco, permitindo que o fluxo seja parcialmente orientado por configuração.

---

# Benefícios da arquitetura

## Desacoplamento

Os serviços não precisam conhecer diretamente a implementação dos demais.

```text
Producer
→ Broker
→ Consumer
```

Isso reduz dependências diretas entre microsserviços.

## Escalabilidade

Consumidores podem ser escalados independentemente de produtores.

Novas instâncias podem consumir mensagens da mesma fila conforme a necessidade.

## Resiliência

Mensagens podem permanecer no broker enquanto um consumidor estiver temporariamente indisponível.

## Evolução

Novos consumidores podem ser adicionados sem alterar diretamente o produtor.

## Processamento assíncrono

Operações não precisam bloquear umas às outras enquanto aguardam processamento em outros serviços.

---

# Tecnologias utilizadas

```text
Java 17
Spring Boot
Spring Data JPA
Spring AMQP
Spring Integration
RabbitMQ
Redis
H2
Maven
```

---

# Estrutura dos serviços

## Order Service

Responsável pelo domínio de pedidos.

Principais responsabilidades:

```text
criação de pedidos via REST
idempotência HTTP
persistência do pedido
publicação de eventos
Transactional Outbox
consumo de comandos do Workflow
idempotência AMQP
controle de workflowVersion
```

## Workflow Service

Responsável pela orquestração do processo.

Principais responsabilidades:

```text
consumir eventos
controlar WorkflowInstance
resolver WorkflowTransition
executar WorkflowTransitionAction
gerar comandos
Transactional Outbox
idempotência AMQP
versionamento lógico
```

## Payment Service

Responsável pelo processamento de pagamentos.

O Workflow já possui suporte à geração do comando:

```text
PROCESS_PAYMENT
```

através da fila:

```text
payment.commands.queue
```

---

# Fluxo transacional do Workflow

```text
RabbitMQ
↓
WorkflowEventListener
↓
verificação de idempotência
↓
WorkflowEventDataFactory
↓
WorkflowService
↓
WorkflowInstance
↓
WorkflowTransition
↓
WorkflowTransitionAction
↓
WorkflowEventNotifier
↓
ApplicationEventPublisher
↓
WorkflowApplicationEventListener
↓
WorkflowCommandFactory
↓
Outbox
↓
COMMIT
```

Após o commit:

```text
OutboxScheduler
↓
WorkflowCommandPublisher
↓
RabbitMQ
```

---

# Conceitos de confiabilidade aplicados

| Problema                                    | Estratégia                         |
| ------------------------------------------- | ---------------------------------- |
| Falha entre banco e publicação              | Transactional Outbox               |
| Mensagem duplicada                          | Idempotência                       |
| Consumer falha durante processamento        | ACK + Redelivery                   |
| Falha temporária                            | Retry                              |
| Entrega uma ou mais vezes                   | At Least Once                      |
| Mensagens fora de ordem                     | Sequence Number / Workflow Version |
| Concorrência entre instâncias de consumidor | Metadata Store compartilhado       |
| Acoplamento entre serviços                  | Event-Driven + Broker              |
| Coordenação do processo                     | Mediator / Workflow                |

---

# Próximas evoluções

Algumas evoluções naturais para o projeto são:

```text
Publisher Confirm
Dead Letter Queue (DLQ)
políticas explícitas de retry e backoff
Payment Service completo
Preparation Service
observabilidade com métricas, logs e tracing distribuído
testes de integração com RabbitMQ
Testcontainers
```

O **Publisher Confirm** permitirá que o produtor confirme explicitamente que o RabbitMQ recebeu a mensagem antes de marcar o registro do Outbox como publicado.

A **DLQ** permitirá separar mensagens que falharam repetidamente para posterior análise ou reprocessamento.

---

# Conclusão

Este projeto vai além de simplesmente publicar e consumir mensagens.

O objetivo é demonstrar os principais problemas encontrados em sistemas distribuídos orientados a eventos e implementar mecanismos para tornar a comunicação assíncrona mais confiável.

A solução combina:

```text
Event-Driven Architecture
+
Mediator / Workflow
+
RabbitMQ
+
Transactional Outbox
+
Idempotência
+
Acknowledgement
+
Retry / Redelivery
+
At Least Once
+
Sequence Number
```

O resultado é uma arquitetura mais desacoplada, resiliente, escalável e preparada para lidar com falhas reais de comunicação entre serviços.
