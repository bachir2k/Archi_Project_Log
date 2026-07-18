-- ============================================================
-- Schéma BDD — Projet Architecture Logicielle
-- MySQL (WampServer / phpMyAdmin)
--
-- Utilisation :
--   - Via phpMyAdmin : onglet "Importer" -> choisir ce fichier
--   - Via ligne de commande (mysql fourni par Wamp, ex :
--     C:\wamp64\bin\mysql\mysqlXX\bin\mysql.exe) :
--       mysql -u root -p < db/schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS projet_al
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE projet_al;

CREATE TABLE utilisateur (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    login           VARCHAR(50)  NOT NULL UNIQUE,
    mot_de_passe    VARCHAR(255) NOT NULL,   -- hashé (BCrypt)
    nom             VARCHAR(100),
    email           VARCHAR(150),
    role            VARCHAR(20)  NOT NULL DEFAULT 'VISITEUR', -- VISITEUR / EDITEUR / ADMIN
    date_creation   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE categorie (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom             VARCHAR(100) NOT NULL UNIQUE,
    description     TEXT
) ENGINE=InnoDB;

CREATE TABLE article (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre               VARCHAR(200) NOT NULL,
    description         VARCHAR(500),          -- résumé affiché sur la liste
    contenu             TEXT NOT NULL,
    date_publication    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    categorie_id        BIGINT,
    auteur_id           BIGINT,
    CONSTRAINT fk_article_categorie FOREIGN KEY (categorie_id) REFERENCES categorie(id) ON DELETE SET NULL,
    CONSTRAINT fk_article_auteur FOREIGN KEY (auteur_id) REFERENCES utilisateur(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Jetons d'authentification pour l'accès au service SOAP.
-- Générés/supprimés uniquement par un admin depuis le back-office.
CREATE TABLE jeton (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    valeur          VARCHAR(255) NOT NULL UNIQUE,
    utilisateur_id  BIGINT NOT NULL,
    date_creation   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_expiration DATETIME,
    actif           TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_jeton_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_article_categorie ON article(categorie_id);
CREATE INDEX idx_article_date ON article(date_publication DESC);
CREATE INDEX idx_jeton_valeur ON jeton(valeur);

-- Jeu de données minimal pour développer sans attendre le back-office
INSERT INTO utilisateur (login, mot_de_passe, nom, email, role)
VALUES ('admin', '$2a$10$replaceWithBCryptHash', 'Admin Principal', 'admin@example.com', 'ADMIN');

INSERT INTO categorie (nom, description) VALUES
  ('Politique', 'Actualités politiques'),
  ('Sport', 'Actualités sportives'),
  ('Technologie', 'Actualités tech');
