FROM openjdk:17-jdk-slim

# Spécifier le répertoire de travail dans l'image
WORKDIR /app

# Copier le fichier JAR généré par Maven dans l'image
COPY target/PortailRH-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port sur lequel l'application va écouter
EXPOSE 8082

# Commande pour exécuter l'application Java
ENTRYPOINT ["java", "-jar", "app.jar"]