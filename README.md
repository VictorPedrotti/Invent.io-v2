---

![image.png](attachment:17150ebe-98a5-4154-860c-e35d8273eebc:image.png)

---

# 🧱 Microservices Overview

## 🔐 1. API Gateway

- **Keycloak**: Gerenciamento de autenticação/autorização via JWT + roles.
- **MySQL**: Persistência dos dados do Keycloak.
- **Observabilidade (futuro)**: Integração com ferramentas como Grafana, Prometheus, etc.

---

## 👤 2. User-service

- **MySQL**: Estrutura de usuários e dados de perfil (nome, email, endereço, etc).
- Consultado por outros serviços (ex: `Order-service`) para obter dados do usuário.

---

## 🔑 3. Auth-service

- Atua como *broker* entre o frontend e o Keycloak.
- Responsável pelo fluxo de login, emissão e renovação de tokens.
- **REST** via OIDC/OAuth2.

---

## 🛍️ 4. Product-service

- **MongoDB**: Armazena dados de produtos (nome, preço, categoria, imagem, descrição).
- Operações: CRUD de produtos.

---

## 🛒 5. Cart-service

- **Redis**: Armazena temporariamente os produtos adicionados ao carrinho.
- Itens possuem *TTL* (tempo de expiração).

---

## 📦 6. Procurement-service

- **MySQL**: Gestão de compras (entrada de produtos).
- Gera eventos de atualização de estoque e despesas.

---

## 🏷️ 7. Stock-service

- **MySQL**: Controle de:
    - Quantidade disponível
    - Quantidade reservada
    - Entradas e saídas
    - Histórico de movimentações
- Escuta eventos de compra (entrada) e pedido (baixa).

---

## 📑 8. Order-service

- **MySQL**: Registro de pedidos e orquestração do processo de checkout.
- Responsável por:
    - Verificação e reserva de estoque
    - Solicitação de pagamento
    - Geração de eventos de confirmação

---

## 📊 9. Reports-service (Billing)

- **MySQL**: Armazena dados financeiros:
    - Receitas (vendas confirmadas)
    - Despesas (compras de estoque)
    - Fluxo de caixa consolidado
- Escuta eventos de pedidos confirmados e compras.

---

## 💳 10. Payment-service

- **MySQL**: Armazena logs de transações e status de pagamento.
- **Não armazena dados sensíveis**.
- Escuta eventos de solicitação de pagamento e responde com sucesso ou falha.

---

## 🔔 11. Notification-service

- **RabbitMQ Consumer**: Escuta eventos de outros serviços, como:
    - `OrderConfirmedEvent`
    - `PaymentFailedEvent`
    - `ShipmentStatusUpdatedEvent` (se houver logística)
- Responsável por:
    - Envio de e-mails, SMS, notificações push
    - Templates de mensagens e logs de notificação
- Totalmente desacoplado da lógica de negócio dos demais serviços.

---

### 📡 Comunicação entre Serviços

| Serviço | Comunicação | Protocolo | Observações |
| --- | --- | --- | --- |
| **Frontend ⟷ Auth-service** | Sync | REST (OIDC/OAuth2) | ✅ O frontend se comunica com o Auth-service (delegado ao Keycloak) para login, obtenção e renovação de tokens JWT, além do gerenciamento de sessões. |
| **API Gateway ⟷ Auth-service** | Sync | REST (Interno) | ✅ O API Gateway valida tokens JWT (emitidos via Keycloak/Auth-service) antes de rotear requisições para outros serviços. Segurança na entrada. |
| **Cart-service → Product-service** | Sync | REST | ✅ Cart-service consulta detalhes dos produtos (nome, imagem, preço) diretamente do Product-service. |
| **Order-service → Stock-service** | Ambos: Sync + Async | REST + RabbitMQ | 🔁 **Sync (Validação/Reserva):** Order-service valida e reserva o estoque durante o checkout. 📨 **Async (Baixa):** Após pagamento, publica `StockDecrementRequestedEvent` para baixa definitiva. |
| **Order-service → Payment-service** | Async | RabbitMQ | 📨 Order-service publica `PaymentRequestedEvent` para processar o pagamento. |
| **Payment-service → Order-service** | Async | RabbitMQ / Webhook | 📨 Payment-service publica `PaymentSuccessfulEvent` ou `PaymentFailedEvent`, que o Order-service consome para atualizar o status. |
| **Order-service → Billing/Reports-service** | Async | RabbitMQ | 📨 Após confirmação do pedido, envia `OrderConfirmedEvent` ou `SaleRecordedEvent` para registrar a receita e atualizar o fluxo de caixa. |
| **Procurement-service → Stock-service** | Async | RabbitMQ | 📨 Envia `ProductsReceivedEvent` para o Stock-service registrar a entrada no estoque. |
| **Procurement-service → Billing/Reports-service** | Async | RabbitMQ | 📨 Envia `PurchaseOrderPaidEvent` ou `ExpenseRecordedEvent` para registrar a despesa e atualizar o fluxo de caixa. |
| **Notification-service** | Async (Consumidor) | RabbitMQ | 🔔 Serviço especializado que escuta eventos como `OrderConfirmedEvent`, `PaymentFailedEvent`, etc., e envia e-mails, push notifications etc. |

---

# 📌 Planejamento de Endpoints

### ✅ **1. Auth-service**

**Responsabilidade:** Integração com Keycloak para gerenciar a autenticação de usuários, emissão e validação de tokens JWT (JSON Web Tokens), e controle de sessões. É o ponto de contato para o login do usuário.

**Endpoints REST (Síncronos):**

- `POST /auth/login`
    - **Descrição:** Realiza o login do usuário com credenciais (usuário/senha) e retorna tokens de acesso (access token) e de renovação (refresh token) do Keycloak.
- `POST /auth/refresh`
    - **Descrição:** Utiliza o token de renovação para obter um novo token de acesso, evitando que o usuário precise fazer login novamente após a expiração de seu token de acesso.
- `POST /auth/logout`
    - **Descrição:** Encerra a sessão do usuário no Keycloak e invalida o token.
- `GET /auth/profile`
    - **Descrição:** Retorna dados básicos do perfil do usuário extraídos diretamente do token JWT decodificado (ex: ID do usuário, nome, roles).

---

### 👤 **2. User-service**

**Responsabilidade:** Gerenciar e armazenar os dados de perfil detalhados dos usuários (compradores e donos de loja) que não são estritamente de segurança.

**Banco de Dados:** MySQL

**Endpoints REST (Síncronos):**

- `GET /users`
    - **Descrição:** Lista todos os usuários registrados. (Acesso restrito a administradores/donos de loja).
- `GET /users/{id}`
    - **Descrição:** Obtém detalhes de um usuário específico por ID. (Acesso: usuário pode ver o próprio, admins podem ver todos).
- `POST /users`
    - **Descrição:** Cria um novo registro de usuário. (Pode ser usado para auto-cadastro ou por admins).
- `PUT /users/{id}`
    - **Descrição:** Atualiza os dados de perfil de um usuário. (Acesso: usuário pode atualizar o próprio, admins podem atualizar qualquer um).
- `DELETE /users/{id}`
    - **Descrição:** Remove um usuário. (Acesso restrito a administradores).

---

### 🛍️ **3. Product-service**

**Responsabilidade:** Gerenciar o cadastro, consulta, atualização e exclusão de todos os produtos disponíveis para venda na loja.

**Banco de Dados:** MySQL

**Endpoints REST (Síncronos):**

- `GET /products`
    - **Descrição:** Lista todos os produtos (pode ter filtros/paginação).
- `GET /products/{id}`
    - **Descrição:** Retorna os detalhes completos de um produto específico.
- `POST /products`
    - **Descrição:** Cria um novo produto. (Acesso restrito a donos de loja/administradores).
- `PUT /products/{id}`
    - **Descrição:** Atualiza as informações de um produto existente. (Acesso restrito a donos de loja/administradores).
- `DELETE /products/{id}`
    - **Descrição:** Remove um produto. (Acesso restrito a donos de loja/administradores).

---

### 🛒 **4. Cart-service**

**Responsabilidade:** Gerenciar os itens que um comprador adiciona ao seu carrinho de compras.

**Banco de Dados:** Redis (recomendado para alta performance e dados temporários do carrinho) ou MySQL (se houver requisito de persistência a longo prazo do carrinho antes da finalização).

**Endpoints REST (Síncronos):**

- `GET /cart/{userId}`
    - **Descrição:** Obtém o carrinho de compras do usuário especificado. O `userId` deve ser validado via token para garantir que o usuário acesse apenas seu próprio carrinho.
- `POST /cart/{userId}/add`
    - **Descrição:** Adiciona um item (produto e quantidade) ao carrinho do usuário. O `Cart-service` pode consultar o `Product-service` (sincronamente) para obter detalhes do produto.
- `PUT /cart/{userId}/update`
    - **Descrição:** Atualiza a quantidade de um item já existente no carrinho.
- `DELETE /cart/{userId}/remove`
    - **Descrição:** Remove um item específico do carrinho.
- `DELETE /cart/{userId}/clear`
    - **Descrição:** Limpa todos os itens do carrinho do usuário.

---

### 📦 **5. Procurement-service**

**Responsabilidade:** Gerenciar o processo de compra e entrada de produtos na loja (reabastecimento de estoque) do ponto de vista do dono da loja. Inclui gerenciamento de pedidos de compra (POs) para fornecedores.

**Banco de Dados:** MySQL

**Endpoints REST (Síncronos):**

- `GET /procurements`
    - **Descrição:** Lista todas as ordens de compra (POs) registradas.
- `GET /procurements/{id}`
    - **Descrição:** Obtém os detalhes de uma ordem de compra específica.
- `POST /procurements`
    - **Descrição:** Cria uma nova ordem de compra (PO) para fornecedores.
- `PUT /procurements/{id}/status`
    - **Descrição:** Atualiza o status de uma ordem de compra (ex: "enviado ao fornecedor", "recebido parcial", "recebido completo", "pago").
    - **Comunicação Assíncrona:** Ao mudar o status para "recebido completo" ou similar, este serviço **deve publicar um evento** na RabbitMQ (`ProductsReceivedEvent`) que será consumido pelo `Stock-service` e pelo `Billing/Reports-service`.

---

### 🏷️ **6. Stock-service**

**Responsabilidade:** Controlar as quantidades e movimentações de estoque para cada produto (entradas, saídas, reservas). É a fonte da verdade sobre a disponibilidade de estoque.

**Banco de Dados:** MySQL

**Endpoints REST (Síncronos):**

- `GET /stock/products`
    - **Descrição:** Retorna o estoque atual de todos os produtos.
- `GET /stock/products/{id}`
    - **Descrição:** Retorna o estoque atual de um produto específico.
- `GET /stock/history/{productId}`
    - **Descrição:** Fornece o histórico de todas as movimentações de estoque para um produto (entradas, saídas, ajustes).

**Comunicação Assíncrona (Consumo de Eventos RabbitMQ):**

- `POST /stock/entry` (Lógica interna, não um endpoint REST público)
    - **Comportamento:** O `Stock-service` **ouve** eventos `ProductsReceivedEvent` (publicados pelo `Procurement-service`) para processar a entrada de produtos e incrementar o estoque.
- `POST /stock/decrement` (Lógica interna, não um endpoint REST público)
    - **Comportamento:** O `Stock-service` **ouve** eventos `StockDecrementRequestedEvent` (publicados pelo `Order-service` após a confirmação de pagamento) para processar a baixa definitiva do estoque.

**Comunicação Síncrona (Chamada REST Recebida):**

- O `Order-service` **chama o `Stock-service` sincronicamente** para **validar a disponibilidade e fazer a reserva** de estoque durante o processo de checkout. Esta é uma operação crucial que exige feedback imediato.

---

### 📑 **7. Order-service**

**Responsabilidade:** Gerenciar o registro e o status dos pedidos de compra feitos pelos clientes. Orquestra o fluxo de checkout, integrando com estoque e pagamento.

**Banco de Dados:** MySQL

**Endpoints REST (Síncronos):**

- `GET /orders`
    - **Descrição:** Lista todos os pedidos (filtrável por usuário, status, etc.).
- `GET /orders/{id}`
    - **Descrição:** Retorna os detalhes completos de um pedido específico.
- `POST /orders`
    - **Descrição:** **Inicia o processo de criação de um novo pedido.** Este endpoint recebe os dados do carrinho (do `Cart-service` ou frontend), **chama o `Stock-service` sincronamente para validar e reservar o estoque**, e então **publica um evento** (`PaymentRequestedEvent`) para o `Payment-service` na RabbitMQ.
- `PUT /orders/{id}/status`
    - **Descrição:** Atualiza o status de um pedido (ex: "processando", "pago", "enviado", "cancelado").
    - **Comportamento:** Mais comumente, o próprio `Order-service` **atualiza seu status ao consumir eventos** da RabbitMQ (ex: `PaymentSuccessfulEvent`, `PaymentFailedEvent`, `ShipmentUpdatedEvent` - se houver um serviço de entrega).

**Comunicação Assíncrona (Publicação de Eventos RabbitMQ):**

- Publica eventos como `PaymentRequestedEvent`, `OrderConfirmedEvent` (após pagamento bem-sucedido), `OrderCancelledEvent`.

**Comunicação Assíncrona (Consumo de Eventos RabbitMQ):**

- Consome eventos do `Payment-service` (ex: `PaymentSuccessfulEvent`, `PaymentFailedEvent`) para atualizar o status interno do pedido.

---

### 📊 **8. Reports-service**

**Responsabilidade:** Consolidar dados de diferentes serviços para gerar relatórios financeiros e de negócio (vendas, despesas, fluxo de caixa, etc.) para os donos da loja.

**Banco de Dados:** MySQL (contendo dados otimizados para relatórios, replicados de outros serviços)

**Endpoints REST (Síncronos):**

- `GET /reports/summary`
    - **Descrição:** Retorna um resumo financeiro geral ou dashboard.
- `GET /reports/sales`
    - **Descrição:** Gera um relatório detalhado de vendas.
- `GET /reports/expenses`
    - **Descrição:** Gera um relatório de despesas (compras de estoque, etc.).
- `GET /reports/cashflow`
    - **Descrição:** Apresenta o fluxo de caixa consolidado.

**Comunicação Assíncrona (Consumo de Eventos RabbitMQ):**

- **Comportamento:** Este serviço é primariamente um **consumidor de eventos**. Ele **ouve** eventos de outros serviços (ex: `OrderConfirmedEvent` do `Order-service`, `PurchaseOrderPaidEvent` do `Procurement-service`) para construir e manter seu próprio banco de dados de relatórios.

---

### 💳 **9. Payment-service**

**Responsabilidade:** Interagir com gateways de pagamento externos para processar transações financeiras.

**Banco de Dados:** MySQL (opcional, para logs e status internos das transações de pagamento, mas não armazena dados sensíveis de cartão).

**Comunicação Assíncrona (Consumo e Publicação de Eventos RabbitMQ):**

- **Comportamento:** O `Payment-service` **ouve** eventos `PaymentRequestedEvent` (publicados pelo `Order-service`) para iniciar o processo de pagamento com o gateway externo.
- **Comportamento:** Após a resposta do gateway, ele **publica eventos** na RabbitMQ (`PaymentSuccessfulEvent` ou `PaymentFailedEvent`) para notificar o `Order-service` (e outros interessados) sobre o resultado.

**Endpoints REST (Síncronos - Opcional, para consulta de status ou webhooks):**

- `POST /payments/webhook/{gateway}`
    - **Descrição:** Endpoint para receber callbacks/webhooks de gateways de pagamento externos (se aplicável).
- `GET /payments/{transactionId}`
    - **Descrição:** Permite consultar o status de uma transação de pagamento específica.

---

### 🔔 **10. Notification-service**

**Responsabilidade:** Enviar diversas notificações para os usuários (e-mails, SMS, notificações push) em resposta a eventos do sistema.

**Banco de Dados:** MySQL (para logs de notificações enviadas, filas de reenvio, etc.).

**Comunicação Assíncrona (Consumo de Eventos RabbitMQ):**

- **Comportamento:** Este serviço é principalmente um **consumidor de eventos**. Ele **ouve** eventos de outros serviços (ex: `OrderConfirmedEvent` do `Order-service`, `PaymentFailedEvent` do `Payment-service`, `PasswordResetRequestedEvent` do `Auth-service` para e-mail de redefinição).
- **Não expõe API pública para envio de notificações**, pois isso desacoplaria a lógica de envio da lógica de negócio. Ações de envio são sempre disparadas por eventos.

**Endpoints REST (Síncronos - Opcional, para administração/depuração):**

- `GET /notifications/logs`
    - **Descrição:** Lista um histórico das notificações enviadas (para auditoria ou depuração).
- `GET /notifications/{id}`
    - **Descrição:** Retorna detalhes de uma notificação específica.

---

## 👤 User-service

### 🧾 Entidade: `User`

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | UUID / BIGINT* | PK. Idealmente o mesmo ID do Keycloak |
| keycloak_id | VARCHAR | Opcional. Útil para mapear com Keycloak |
| first_name | VARCHAR | Nome |
| last_name | VARCHAR | Sobrenome |
| email | VARCHAR | UNIQUE |
| phone_number | VARCHAR |  |
| cpf_cnpj | VARCHAR | UNIQUE.  |
| user_type | ENUM / VARCHAR | 'COMPRADOR', 'DONO_LOJA' |
| created_at | DATETIME |  |
| updated_at | DATETIME |  |
| deleted_at | DATETIME |  |

### 📍 Entidade: `Address`

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| user_id | BIGINT | FK para `User.id` |
| street | VARCHAR |  |
| number | VARCHAR |  |
| complement | VARCHAR | Nullable |
| neighborhood | VARCHAR |  |
| city | VARCHAR |  |
| state | VARCHAR |  |
| zip_code | VARCHAR |  |
| is_default | BOOLEAN | Marca o endereço padrão |
| created_at | DATETIME |  |
| updated_at | DATETIME |  |

**Relacionamento:** `User 1:N Address`

---

## 🛍️ Product-service

### 📦 Entidade: `Product`

| Campo | Tipo | Observações |
| --- | --- | --- |
| id **✅** | String | PK |
| name **✅** | VARCHAR |  |
| description **✅** | TEXT | Nullable |
| price **✅** | DECIMAL(10,2) |  |
| category_id **✅** | BIGINT | FK (nullable) para `Category.id` |
| sku **✅** | VARCHAR | UNIQUE. Código único do produto |
| weight **✅** | DECIMAL(10,2) | Nullable |
| dimensions **✅** | VARCHAR | Nullable. Ex: "10x15x5 cm" |
| image_url **✅** | VARCHAR | URL de imagem (ex: S3, Cloudinary) |
| is_active **✅**  | BOOLEAN | Ativa ou desativa a venda |
| created_at **✅** | DATETIME |  |
| updated_at **✅** | DATETIME |  |
| deleted_at **✅** | DATETIME |  |

### 🗂️ Entidade: `Category`

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| name | VARCHAR | UNIQUE |
| description | TEXT | Nullable |
| parent_category_id | BIGINT | FK para `Category.id` (nullable) |
| created_at | DATETIME |  |
| updated_at | DATETIME |  |
| deleted_at | DATETIME |  |

**Relacionamento:** `Category 1:N Product`

---

## 🛒 Cart-service

### 🛒 Entidade: `Cart`

| Campo | Tipo | Observações |
| --- | --- | --- |
| user_id | BIGINT | PK. Um carrinho por usuário |
| total_amount | DECIMAL(10,2) | Calculado |
| last_updated_at | DATETIME |  |
| created_at | DATETIME |  |

### 🧾 Entidade: `CartItem`

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| cart_user_id | BIGINT | FK para `Cart.user_id` |
| product_id | BIGINT | Referência (não é FK direto) |
| quantity | INT |  |
| unit_price | DECIMAL(10,2) | Preço no momento de adição |
| item_total | DECIMAL(10,2) | Calculado |
| added_at | DATETIME |  |

**Relacionamento:** `Cart 1:N CartItem`

## 📦 Procurement-service

### 📥 Entidade: `Purchase`

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| supplier_name | VARCHAR | Nome do fornecedor |
| total_amount | DECIMAL(10,2) | Valor total da compra |
| status | ENUM | Ex: 'PENDENTE', 'RECEBIDA', 'CANCELADA' |
| received_at | DATETIME | Nullable – quando a compra foi recebida |
| created_at | DATETIME |  |

### 📦 Entidade: `PurchaseItem`

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| purchase_id | BIGINT | FK para `Purchase.id` |
| product_id | BIGINT | FK/Ref para produto |
| quantity | INT |  |
| unit_cost | DECIMAL(10,2) | Custo unitário |
| subtotal | DECIMAL(10,2) | quantity × unit_cost |

**Relacionamento:** `Purchase 1:N PurchaseItem`

---

## 📊 Stock-service

### 🧾 Entidade: `Stock`

| Campo | Tipo | Observações |
| --- | --- | --- |
| product_id | BIGINT | PK (1:1 com produto) |
| quantity_total | INT | Quantidade total em estoque |
| quantity_reserved | INT | Reservado para pedidos pendentes |
| quantity_available | INT | Calculado = total - reservado |
| updated_at | DATETIME |  |

### 🧾 Entidade: `StockMovement`

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| product_id | BIGINT | FK |
| type | ENUM | 'ENTRADA', 'SAÍDA', 'AJUSTE' |
| origin | VARCHAR | Ex: 'COMPRA', 'VENDA', 'DEVOLUÇÃO' |
| quantity | INT |  |
| created_at | DATETIME |  |

**Relacionamento:** `Product 1:N StockMovement`

---

## 📦 Order-service

### 📦 Entidade: `Order`

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| user_id | BIGINT | Comprador |
| total_amount | DECIMAL(10,2) |  |
| status | ENUM | 'PENDENTE', 'PAGO', 'CANCELADO', 'ENVIADO' |
| payment_transaction_id | VARCHAR | Nullable. ID da transação no `Payment-service` (referência lógica). |
| shipping_address_street | VARCHAR | **Denormalizado** |
| shipping_address_number | VARCHAR | **Denormalizado** |
| shipping_address_complement | VARCHAR | **Denormalizado** |
| shipping_address_neighborhood | VARCHAR | **Denormalizado** |
| shipping_address_city | VARCHAR | **Denormalizado** |
| shipping_address_state | VARCHAR | **Denormalizado** |
| shipping_address_zip_code | VARCHAR | **Denormalizado** |
| created_at | DATETIME |  |
| updated_at | DATETIME |  |

### 📦 Entidade: `OrderItem`

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| order_id | BIGINT | FK para `Order.id` |
| product_id | BIGINT | Referência a produto |
| quantity | INT |  |
| unit_price | DECIMAL(10,2) | Preço na data da venda |
| total_price | DECIMAL(10,2) | Calculado |

**Relacionamento:** `Order 1:N OrderItem`

---

## 📈 Reports-service

### 🧾 Entidade: `Transaction`

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| type | ENUM | 'RECEITA', 'DESPESA' |
| reference_id | BIGINT | ID do pedido ou compra |
| reference_type | ENUM | 'ORDER', 'PURCHASE' |
| amount | DECIMAL(10,2) |  |
| created_at | DATETIME |  |

---

## 💰 Payment-service

### 🧾 Entidade: `PaymentLog`

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| order_id | BIGINT | FK para `Order.id` |
| provider | VARCHAR | Ex: 'Stripe', 'MercadoPago' |
| transaction_id | VARCHAR | ID de referência do gateway |
| status | ENUM | 'SUCESSO', 'FALHA', 'PENDENTE' |
| message | TEXT | Resposta do gateway |
| created_at | DATETIME |  |

---

## 🔔 Notification-service

### 🧾 Entidade: `Notification`

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| user_id | BIGINT | FK opcional (pode ser global) |
| type | ENUM | 'EMAIL', 'SMS' |
| event_source | VARCHAR | Serviço que gerou o evento (ex: `'ORDER_SERVICE'`, `'PAYMENT_SERVICE'`). |
| recipient | VARCHAR | Endereço de e-mail ou número de telefone do destinatário. |
| subject | VARCHAR | Assunto (se aplicável) |
| message | TEXT | Conteúdo completo |
| sent_status | ENUM | 'ENVIADO', 'FALHOU', 'AGUARDANDO' |
| sent_at | DATETIME |  |
| created_at | DATETIME |  |
| error_message | TEXT | Nullable. Mensagem de erro em caso de falha no envio. |
