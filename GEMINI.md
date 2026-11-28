# GestiTaches — instructions du projet

> Modèle à compléter dans le TP1, Partie D, puis à renommer selon votre assistant :
> `CLAUDE.md` (Claude Code), `GEMINI.md` (Gemini CLI), `AGENTS.md` (Cursor),
> `.github/copilot-instructions.md` (GitHub Copilot).
> Supprimez ces lignes d'explication une fois le fichier complété.

## Le projet en deux lignes

Application de bureau de gestion de tâches pour une petite équipe : un chef de projet crée des projets et des tâches, les assigne à des membres, et chacun suit leur statut. Le périmètre est limité aux cinq user stories de spec.md.

## Pile technique

- Java 17 
- Interface graphique : Swing
- Base de données : SQLite, accès par JDBC (sqlite-jdbc)
- Build : Maven (mvn clean test), tests JUnit 5

## Conventions
- Packages sous fr.usms.gestitaches : model, dao, service, ui
- Classes en PascalCase, méthodes et variables en camelCase, constantes en MAJUSCULES_AVEC_UNDERSCORE, une classe publique par fichier
- Tables et colonnes SQL en minuscules avec underscore (projet, tache, membre_id), fichiers en UTF-8

## Règles à toujours respecter

- Requêtes SQL toujours paramétrées (PreparedStatement), jamais de concaténation de saisies
- Aucun mot de passe, clé ni jeton dans le code ni dans Git
- Statuts autorisés : à faire (défaut), en cours, terminé, vérifiés avant écriture en base
- Connexions et fichiers ouverts dans un try-with-resources
- Chaque critère d'acceptation et chaque cas limite de spec.md a au moins un test
- Ressources JDBC ouvertes dans un try-with-resources.
- Une SQLException est traduite en PersistanceException,
  jamais avalée (ni catch vide, ni printStackTrace seul).

## Façon de travailler

Présenter un plan et attendre mon accord avant toute modification de fichier
Traiter une seule user story à la fois, dans l'ordre de spec.md, avec un commit par story
Lancer mvn clean test avant de déclarer une story terminée
Ne modifier que les fichiers listés dans ma demande.
Si un autre fichier est nécessaire, s'arrêter et demander.

## Documents de référence

- `spec.md` : user stories et critères d'acceptation
- `schema.sql` : schéma de la base de données
- architecture.md : couches et dépendances autorisées
