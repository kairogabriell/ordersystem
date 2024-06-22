# Order System
## Visão Geral
O Projeto em questão é um desafio técnico que consiste em integrar dois sistemam, onde um fornecera um arquivo contendo pedidos exemplo:
```
0000000002                                     Medeiros00000123450000000111      256.2420201201
0000000001                                      Zarelli00000001230000000111      512.2420211201
0000000001                                      Zarelli00000001230000000122      512.2420211201
0000000002                                     Medeiros00000123450000000122      256.2420201201
```
O segundo sistema funcionara por meio de uma API RESET, onde ele precisa ler e converter os arquivos enviados para o formato JSON normalizado, tendo tambem a possibilidade de consultar pedidos por ID e por um range de datas solicitado.
As tecnologias utilizadas para o desenvolvimento foram JAVA e PostgreSQL tendo como objetivo ser mais simples e otimizado possivel.

## Abordagem
Como os dados gerados no sistema legado são padronizados por tamanho do seu valor, a solução abordada foi cirar uma anáslise 
onde o tamanho dos campos foram mapeados conforme o anunciado para obter cada informção na linha do arquivo onde ela é porcessada e salva no banco de dados criado

Tabela com tipo e tamanho do dado:

| campo            | tamanho | tipo                           |
|------------------|---------|--------------------------------|
| id usuário       | 10      | numérico                       |
| nome             | 45      | texto                          |
| id pedido        | 10      | numérico                       |
| d produto        | 10      | numérico                       |
| valor do produto | 12      | decimal                        |
| data compra      | 8       | numérico ( formato: yyyymmdd ) |


## Technology Stack
- **Java 17**: A principal linguagem de programação utilizada para a implementação.
- **PostgreSQL**: Banco de dados relacional usado para armazenar os dados de pedidos normalizados.
- **Jetty**: Servidor HTTP usado para lidar com as solicitações da API REST.
- **JaCoCo**: Usado para gerar relatório de analise de copertura de testes no codigo.
- **Gson**: Biblioteca usada para serialização e desserialização JSON.
- **Gradle**: Ferramenta de automação de build usada para gerenciar dependências do projeto e ciclo de vida do build.
- **Flyway**: Ferramenta de migração para gerenciamento de mudanças no esquema do BD de maneira controlada e versionada.

## Escolhas de arquitetura
- **Arquitetura de microsserviços**: o sistema é projetado usando uma arquitetura de microsserviços para garantir modularidade e escalabilidade.
- **REST API**: A comunicação entre os sistemas é feita via REST API, que é simples e amplamente suportada.
- **Arquitetura em camadas**: a base de código é dividida em múltiplas camadas (Controlador, Serviço, Repositório) para promover a separação de interesses e melhorar a capacidade de manutenção.

## Endpoints
### 1. Post Upload
- **URL**: `/upload`
- **Method**: `POST`
- **Descrição**: Endpoint responsável por receber o arquivo do sistema antigo, analisa-o e salva os dados processados no banco de dados PostgreSQL  
- **Request**: `multipart/form-data` com o arquivo a ser carregado.
- **Response**:
    - `200 OK`: Upload realizado com sucesso.
    - `400 Bad Request`: Extensão de arquivo inválida. Somente arquivos .txt são permitidos.
    - `500 Internal Server Error`: Upload falhou: {error}
  
### 2. Get Orders
- **URL**: `/orders`
- **Method**: `GET`
- **Descrição**: Endpoint responsável por recuperar os pedidos salvos no banco de dados, podendo ser enviados parametros como ID do pedido e o intervalo de datas desejados o retorno dos dados é paginado.
- **Parâmetros de consulta**:
- `order_id` (opcional): Filtre por ID do pedido. 
- `start_date` (opcional): Filtrar por data de início.
- `end_date` (opcional): Filtrar por data de término.
- observação: caso o start_date seja enviado é necessario enviar end_date e vice versa
- `page` (opcional): Número da página para paginação, por padrão ira retornar sempre a página 1.
- `limit` (opcional): Número de registros por página para paginação, por padrão o limite sera 100 registros por página.
- - **Response**:
- `200 OK`:

 ```json
{
    "data": [
      {
        "userId": 22,
        "name": "Bebela",
        "orders": [
          {
            "orderId": 2,
            "total": "75,50",
            "date": "2024-06-21",
              "products": [
                    {
                      "productId": 777,
                      "value": "25,50"
                    },
                    {
                      "productId": 888,
                      "value": "50,00"
                    }
              ]
          }
        ]
      }
    ],
    "pagination": 
      {
        "entries": 1,
        "totalEntries": 2,
        "page": 1,
        "limit": 1,
        "totalPages": 2
      }
}
  ```
- `400 Bad Request`: Parâmetros de consulta inválidos.
- `500 Internal Server Error`: Ocorreu um erro durante a recuperação de dados.

## Executando o Projeto

### Pré-requisitos
- Java 17
- Maven
- PostgreSQL

### Configurar

1. **Clone o repositório**:
```bash
  https://github.com/kairogabriell/ordersystem.git
  cd ordersystem
 ```
2. **Configure o banco de dados PostgreSQL**:
- Crie um banco de dados chamado `ordersystem`.
- Crie um banco de dados chamado `ordersystem_test`.
- Atualize as credenciais do banco de dados no arquivo de configuração
  - Para testes:  ```application-test.properties ```
  - Para Produção  ```application.properties ```

3. **Build**
  -  Windows:
   ```bash
     gradlew.bat clean build
  ```
   - Linux/macOS:
   ```bash
     ./gradlew clean build
  ```

4. **Execute as migrações de banco de dados**:
- Ambiente de teste:
  - Windows:
     ```bash
        gradlew.bat migrateTestDb
      ```
  - Linux/macOS:
     ```bash
       ./gradlew migrateTestDb
    ```
- Ambiente de produção:
  -  Windows:
       ```bash
        gradlew.bat migrateProdDb
       ```
    - Linux/macOS:
       ```bash
         ./gradlew migrateProdDb
      ```
5. **Execute o aplicativo**:
- Ambiente de teste:
  - Windows:
      ```bash
         gradlew.bat runTest
       ```
  - Linux/macOS:
     ```bash
       ./gradlew runTest
    ```
- Ambiente de produção:
  - Windows:
     ```bash
      gradlew.bat runMain
    ```
  - Linux/macOS:
     ```bash
       ./gradlew runMain
    ```

### Teste
- **Executar testes**:
    -  Windows:
       ```bash
         gradlew.bat test
        ```
    - Linux/macOS:
        ```bash
         ./gradlew test
        ```
### Relatório de Teste
  - **Executar testes**:
    -  Windows:
         ```bash
          gradlew.bat test jacocoTestReport
        ```
    - Linux/macOS:
       ```bash
         ./gradlew test jacocoTestReport
       ```
### Estrutura do Projeto
```
├───gradle
│   └───wrapper
│           gradle-wrapper.jar
│           gradle-wrapper.properties
│
└───src
    ├───main
    │   ├───java
    │   │   └───com
    │   │       └───example
    │   │           └───ordersystem
    │   │               │   Main.java
    │   │               │
    │   │               ├───controller
    │   │               │       OrderController.java
    │   │               │
    │   │               ├───model
    │   │               │       Order.java
    │   │               │       OrderItem.java
    │   │               │       Product.java
    │   │               │       User.java
    │   │               │
    │   │               ├───repository
    │   │               │       OrderItemRepository.java
    │   │               │       OrderRepository.java
    │   │               │       ProductRepository.java
    │   │               │       UserRepository.java
    │   │               │
    │   │               ├───service
    │   │               │       FileProcessingService.java
    │   │               │       OrderDTO.java
    │   │               │       OrderService.java
    │   │               │       PaginatedResponseDTO.java
    │   │               │       PaginationDTO.java
    │   │               │       ProductDTO.java
    │   │               │       UserDTO.java
    │   │               │
    │   │               └───util
    │   │                       ApplicationConfig.java
    │   │                       DatabaseUtil.java
    │   │
    │   └───resources
    │       │   application-test.properties
    │       │   application.properties
    │       │
    │       └───db
    │           └───migration
    │                   V1__initial_schema.sql
    │
    └───test
        ├───java
        │   └───com
        │       └───example
        │           └───ordersystem
        │               │   MainTest.java
        │               │
        │               ├───controller
        │               │       OrderControllerTest.java
        │               │
        │               ├───repository
        │               ├───service
        │               │       FileProcessingServiceTest.java
        │               │       OrderServiceTest.java
        │               │
        │               └───util
        │                       DatabaseTestSetup.java
        │
        └───resources
                example-file.csv
                example-file.txt
```