# Instalando uma imagem do JDK 21
# FROM openjdk:21
FROM eclipse-temurin:21

# Definindo o diretório de trabalho
WORKDIR /app

# Copiando o arquivo JAR para o diretório de trabalho
#COPY target/*.jar app.jar
COPY . /app

# Comando para fazer o build do projeto
RUN ./mvnw -B clean package -DskipTests

# Expondo a porta 8081
EXPOSE 8081

# Comando para executar a aplicação
CMD ["java", "-jar", "target/apiPedidos-0.0.1-SNAPSHOT.jar"]
