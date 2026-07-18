-- ============================================================
-- Schéma BDD — Projet Architecture Logicielle
-- PostgreSQL (adapter les types AUTO_INCREMENT/SERIAL pour MySQL)
-- ============================================================

CREATE TABLE utilisateur (
    id              BIGSERIAL PRIMARY KEY,
    login           VARCHAR(50)  NOT NULL UNIQUE,
    mot_de_passe    VARCHAR(255) NOT NULL,   -- hashé (BCrypt)
    nom             VARCHAR(100),
    email           VARCHAR(150),
    role            VARCHAR(20)  NOT NULL DEFAULT 'VISITEUR', -- VISITEUR / EDITEUR / ADMIN
    date_creation   TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE categorie (
    id              BIGSERIAL PRIMARY KEY,
    nom             VARCHAR(100) NOT NULL UNIQUE,
    description     TEXT
);

CREATE TABLE article (
    id              BIGSERIAL PRIMARY KEY,
    titre           VARCHAR(200) NOT NULL,
    description     VARCHAR(500),          -- résumé affiché sur la liste
    contenu         TEXT NOT NULL,
    date_publication TIMESTAMP NOT NULL DEFAULT now(),
    categorie_id    BIGINT REFERENCES categorie(id) ON DELETE SET NULL,
    auteur_id       BIGINT REFERENCES utilisateur(id) ON DELETE SET NULL
);

-- Jetons d'authentification pour l'accès au service SOAP.
-- Générés/supprimés uniquement par un admin depuis le back-office.
CREATE TABLE jeton (
    id              BIGSERIAL PRIMARY KEY,
    valeur          VARCHAR(255) NOT NULL UNIQUE,
    utilisateur_id  BIGINT NOT NULL REFERENCES utilisateur(id) ON DELETE CASCADE,
    date_creation   TIMESTAMP NOT NULL DEFAULT now(),
    date_expiration TIMESTAMP,
    actif           BOOLEAN NOT NULL DEFAULT TRUE
);

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
