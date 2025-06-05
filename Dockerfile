# Étape 1 : Build de l'application avec Gradle
FROM gradle:8.4-jdk17 AS builder

# Copier le projet dans le conteneur
WORKDIR /home/ktor-app
COPY . .

# Générer le fat JAR (shadowJar requis dans ton build.gradle.kts)
RUN gradle shadowJar --no-daemon

# Étape 2 : Image minimale d'exécution
FROM eclipse-temurin:17-jdk-alpine

# Dossier de travail
WORKDIR /app

# Copier uniquement le JAR généré
COPY --from=builder /home/ktor-app/build/libs/*.jar app.jar

# Définir le port sur lequel l'app écoute (modifiable selon ton application.conf)
EXPOSE 8080

# Lancer l'application
CMD ["java", "-jar", "app.jar"]
