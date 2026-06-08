package com.yt.projetos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProjetosApplication {

	public static void main(String[] args) {
		try {
			ensureDatabaseExists();
		} catch (Exception e) {
			System.err.println("Warning: failed to ensure database exists: " + e.getMessage());
		}
		SpringApplication.run(ProjetosApplication.class, args);
	}

	private static void ensureDatabaseExists() {
		String dsUrl = System.getenv("SPRING_DATASOURCE_URL");
		if (dsUrl == null || dsUrl.isBlank()) {
			dsUrl = "jdbc:postgresql://localhost:5432/yt_platform";
		}
		String dbUser = System.getenv("SPRING_DATASOURCE_USERNAME");
		if (dbUser == null || dbUser.isBlank()) dbUser = "postgres";
		String dbPass = System.getenv("SPRING_DATASOURCE_PASSWORD");
		if (dbPass == null) dbPass = "postgres";

		// extract database name from URL
		String dbName = null;
		try {
			Pattern p = Pattern.compile("jdbc:postgresql://[^/]+/([^\\?]+)");
			Matcher m = p.matcher(dsUrl);
			if (m.find()) {
				dbName = m.group(1);
			}
		} catch (Exception ignored) {}
		if (dbName == null || dbName.isBlank()) {
			System.out.println("Could not determine DB name from SPRING_DATASOURCE_URL, skipping auto-create.");
			return;
		}

		// build admin URL pointing to 'postgres' default database
		String adminUrl = dsUrl.replaceFirst("/[^/?]+(\\?.*)?$", "/postgres$1");

		System.out.println("Checking database '" + dbName + "' on: " + adminUrl);
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// driver not available on classpath
		}

		try (Connection conn = DriverManager.getConnection(adminUrl, dbUser, dbPass);
			 Statement st = conn.createStatement()) {
			String checkSql = "SELECT 1 FROM pg_database WHERE datname='" + dbName + "'";
			try (ResultSet rs = st.executeQuery(checkSql)) {
				if (!rs.next()) {
					System.out.println("Database '" + dbName + "' does not exist — creating...");
					st.executeUpdate("CREATE DATABASE \"" + dbName + "\";");
					System.out.println("Database created: " + dbName);
				} else {
					System.out.println("Database already exists: " + dbName);
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException("Error ensuring database exists: " + ex.getMessage(), ex);
		}
	}

}
