@echo off
set SPRING_PROFILES_ACTIVE=dev
set SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/yt_platform
set SPRING_DATASOURCE_USERNAME=postgres
set SPRING_DATASOURCE_PASSWORD=postgres
call mvnw.cmd spring-boot:run
