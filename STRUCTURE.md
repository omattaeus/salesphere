# Entendendo decisões arquiteturais e a estrutura do projeto

## Requisitos para rodar o projeto

### Setup de ambiente:

- [Java 21](https://www.java.com/pt-BR/)
  - Instalar [`Java 21`](https://www.oracle.com/br/java/technologies/downloads/#java21)
- [Spring Boot 3.3.2](https://spring.io/projects/spring-boot)
  - Usando [`JVM`](https://github.com/nvm-sh/nvm)
    - `winget search Oracle.JDK`
   
### Como rodar na minha máquina?

- Clone o projeto `git clone https://github.com/omattaeus/salesphere.git`
  
- Rode `javac Starup.java`
- Pronto 🎉

# Documentação do Sistema de Gerenciamento de Produtos - Salesphere

## 1. Introdução
Este sistema gerencia produtos e suas quantidades em estoque, enviando alertas por e-mail quando o estoque está baixo. A arquitetura inclui serviços para manipulação de produtos e um agendador para verificar periodicamente o estoque.

## 2. Pacotes e Classes
### 2.1 com.salesphere.salesphere.services
2.1.1 ProductService
   
### Descrição:
O serviço ProductService gerencia as operações relacionadas aos produtos, como recuperação de produtos, criação de novos produtos, e verificação e notificação de estoque baixo.

### Dependências:

- `ProductRepository:` Repositório para acessar dados dos produtos.
- `ProductMapper:` Mapeador para converter entre entidades de produto e DTOs.
- `JavaMailSender:` Enviador de e-mails para notificações de estoque baixo.

### Métodos:

- `getAllProducts()`
Recupera todos os produtos do repositório e os converte em DTOs.

      public List<ProductResponseDTO> getAllProducts()

- `createProduct(ProductRequestDTO productRequestDTO)`
Cria um novo produto a partir dos dados fornecidos. Lança uma exceção se os dados do produto estiverem vazios ou inválidos.

      public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO)

- `getProductsWithLowStock()`
Recupera todos os produtos cujo estoque está abaixo da quantidade mínima.

      public List<Product> getProductsWithLowStock()

- `checkStock()`
Verifica produtos com estoque baixo e envia alertas por e-mail para cada produto com estoque abaixo do mínimo.

      public void checkStock()

- `sendLowStockAlert(Product product)`
Envia um e-mail de alerta sobre o produto com estoque baixo.

      public void sendLowStockAlert(Product product)

### 2.2 com.salesphere.salesphere.services.scheduler
2.2.1 StockScheduler

#### Descrição:
O agendador StockScheduler é responsável por verificar periodicamente o estoque e acionar a verificação do estoque baixo.

### Dependências:

- `ProductService:` Serviço que realiza a verificação do estoque e envio de alertas.

### Métodos:

- `checkStockPeriodically()`
Método agendado para execução periódica (uma vez por hora) que chama o método checkStock() do ProductService.

      @Scheduled(cron = "0 0 * * * *")
      public void checkStockPeriodically()

### 2.3 com.salesphere.salesphere.models
2.3.1 Product

### Descrição:
A classe Product representa um produto no sistema, incluindo informações detalhadas sobre o produto e seu estado de estoque.

### Anotações:

- `@Entity: Marca a classe como uma entidade JPA.`
- `@Table(name = "tb_product"): Define o nome da tabela no banco de dados.`

### Atributos:

- `id`
Identificador único do produto.

        private Long id;
        
- `productName`
Nome do produto.

      private String productName;

- `description`
Descrição do produto.

      private String description;

- `brand`
Marca do produto.

      private String brand;

- `purchasePrice`
Preço de compra do produto.

      private Double purchasePrice;

- `salePrice`
Preço de venda do produto.


      private Double salePrice;

- `stockQuantity`
Quantidade em estoque.

      private Long stockQuantity;

- `minimumQuantity`
Quantidade mínima em estoque.

      private Long minimumQuantity;

- `codeSku`
Código SKU único do produto.


      private String codeSku;

- `category`
  
Categoria do produto.

    private Category category;

- `availability`
Status de disponibilidade do produto.

      private AvailabilityEnum availability

Construtor:

- `Product(Long id, String productName, String description, String brand, Category category, Double purchasePrice, Double salePrice, Long stockQuantity, Long minimumQuantity, String codeSku, AvailabilityEnum availability)`

Construtor com todos os campos necessários para criar um objeto Product.


    public Product(Long id, String productName, String description, 
                  String brand, Category category, Double purchasePrice,
                  Double salePrice, Long stockQuantity, Long           
                  minimumQuantity, String codeSku,
                  AvailabilityEnum availability)

## 3. Uso
### 3.1 Verificação de Estoque

A verificação de estoque é realizada periodicamente pelo StockScheduler, que invoca o método checkStock() do ProductService. Se algum produto estiver com estoque abaixo do mínimo, um alerta é enviado por e-mail.

### 3.2 Criação de Produtos

Produtos são criados através do método createProduct() do ProductService, que valida os dados e salva o novo produto no repositório.

### 3.3 Recuperação de Produtos

Todos os produtos podem ser recuperados usando o método getAllProducts() do ProductService.

# Documentação da Classe Category

A classe Category representa uma categoria no sistema, associada a uma lista de produtos.

## 1. Anotações e Dependências
   
- `@Entity`
Marca a classe como uma entidade JPA que será mapeada para uma tabela no banco de dados.

- `@Table(name = "tb_category")`
Define o nome da tabela no banco de dados como tb_category.

- `@Getter` e `@Setter`
Geram automaticamente os métodos getters e setters para os atributos da classe.

- `@AllArgsConstructor`
Gera um construtor com todos os campos da classe.

- `@NoArgsConstructor`
Gera um construtor sem argumentos.

2. Atributos
   
- `id`
Identificador único da categoria.
Tipo: `Long`
Anotações:

- `@Id`
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`

- `categoryEnum`
Enum que representa a categoria.
Tipo: `CategoryEnum`

Anotações:

- `@Enumerated(EnumType.STRING)`
- `@Column(name = "category", unique = true, length = 20)`

- `products`
Lista de produtos associados a esta categoria.
Tipo: `List<Product>`

Anotações:

- `@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)`

## 3. Relacionamentos
- `products`
Relacionamento de um-para-muitos com a entidade Product. A categoria pode estar associada a vários produtos.

## 4. Exemplos
### 4.1 Exemplo de Criação de Categoria

    Category category = new Category();
    category.setCategoryEnum(CategoryEnum.ELECTRONICS);

### 4.2 Exemplo de Acesso aos Produtos da Categoria

    List<Product> products = category.getProducts();
