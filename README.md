# Spring Boot 2 to 3 Migration Example

Este projeto é um exemplo de migração do Spring Boot 2.7 para 3.4.4, demonstrando as principais mudanças necessárias, especialmente em relação aos pacotes de persistência Jakarta.

## Principais Mudanças

1. Migração de `javax.*` para `jakarta.*`
2. Atualização para Java 17
3. Atualização das dependências do Spring Boot

## Estrutura do Projeto

- `build.gradle.kts`: Arquivo de configuração do Gradle com as dependências atualizadas
- `src/main/java/br/rnp/pgf/database/entities/GrupoDespesa.java`: Exemplo de entidade usando Jakarta Persistence

## Como Executar

1. Clone o repositório
2. Execute `./gradlew build`
3. Execute `./gradlew bootRun`

## Requisitos

- Java 17 ou superior
- Gradle 7.x ou superior

## Contribuições

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou enviar pull requests.