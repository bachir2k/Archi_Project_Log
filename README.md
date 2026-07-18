# Projet Architecture Logicielle — Groupe X (Classe)

Site d'actualité + Services Web (SOAP/REST) + Application Client.

## Structure du dépôt

```
projet-al/
├── site/                  # Personne A — Spring Boot MVC + Thymeleaf, JPA, Spring Security
├── services-web/
│   ├── soap/               # Personne B — JAX-WS (auth + CRUD utilisateurs)
│   └── rest/                # Personne B — REST (articles JSON/XML)
├── client/                 # Personne B — Application Java Swing/JavaFX
└── db/
    └── schema.sql            # Modèle de données commun
```

## Prérequis

- Java 17+
- Maven 3.9+
- PostgreSQL (ou MySQL) — créer une base `projet_al`

## Lancement

Chaque module est un projet Spring Boot indépendant (sauf `client/` qui est une appli desktop).

```bash
# Site web (port 8080)
cd site && mvn spring-boot:run

# Service REST (port 8081)
cd services-web/rest && mvn spring-boot:run

# Service SOAP (port 8082)
cd services-web/soap && mvn spring-boot:run

# Application client
cd client && mvn javafx:run   # ou exécuter la classe Main depuis l'IDE
```

## Base de données

Le schéma est dans `db/schema.sql`. Import :

```bash
psql -U postgres -d projet_al -f db/schema.sql
```

## Endpoints (à compléter au fil de la semaine)

### Site (8080)
- `GET /` — accueil (liste articles paginée)
- `GET /articles/{id}` — détail article
- `GET /categories/{id}` — articles d'une catégorie
- `GET /admin/**` — back-office (admin)
- `GET /editeur/**` — back-office (éditeur)

### REST (8081)
- `GET /api/articles?format=json|xml`
- `GET /api/articles/par-categorie?format=json|xml`
- `GET /api/categories/{id}/articles?format=json|xml`

### SOAP (8082)
- WSDL : `http://localhost:8082/ws/utilisateurs.wsdl`
- Opérations : `authentifier(login, motDePasse)`, `listerUtilisateurs(jeton)`, `ajouterUtilisateur(jeton, ...)`, `modifierUtilisateur(jeton, ...)`, `supprimerUtilisateur(jeton, id)`

## Répartition

Voir `ROADMAP.md` (planning jour par jour) et le tableau des rôles.

## Rendu

Lien du dépôt public à envoyer avant le **dimanche 12 juillet 23h59mn59s** à `envoitp@gmail.com`, objet exact : `Projet_AL_Groupe_X_Classe`.
