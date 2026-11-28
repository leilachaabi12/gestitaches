# Plan d'implementation — User story 1 : Creer un projet

> Plan valide avant toute modification du code (voir `CLAUDE.md`, « Facon de travailler »).

## Rappel de la user story

En tant que chef de projet, je veux créer un nouveau projet afin d'organiser le travail de l'équipe.

| # | Critère | Comment le plan y répond |
|---|---|---|
| CA1 | Création du projet « Site web » | La saisie passe par `ProjetService`, puis `ProjetDAO.creer()` insère une ligne SQLite |
| CA1 | Le projet apparaît dans la liste sans tâche | `ProjetDAO.trouverTous()` recharge les projets sans charger de tâches |
| CA1 | La fiche affiche le nom saisi | `ProjetDAO.trouverParId()` restitue le nom et `MainFrame` l'affiche |
| CL1 | Nom vide ou composé d'espaces | `ProjetService` normalise avec `trim()` puis refuse |
| CL1 | Nom déjà existant | `ProjetService` vérifie le nom normalisé avant l'insertion |
| CL1 | Échec sans modification | Aucun appel d'insertion n'a lieu pour une entrée invalide ou un doublon |

## Périmètre

**Inclus :**
- création d'un projet avec un nom obligatoire ;
- affichage de la liste et de la fiche du projet ;
- refus des noms vides et déjà utilisés ;
- persistance SQLite avec `PreparedStatement` ;
- tests JUnit 5.

**Exclu :**
- description saisie dans l'interface ;
- modification ou suppression ;
- tâches, membres et autres user stories ;
- authentification.

## Fichiers concernés

| Fichier | Action | Contenu |
|---|---|---|
| `schema.sql` | Modifier | `nom TEXT NOT NULL UNIQUE` |
| `src/main/java/fr/usms/gestitaches/model/Projet.java` | Inchangé | Modèle simple avec identifiant, nom et description |
| `src/main/java/fr/usms/gestitaches/dao/ProjetDAO.java` | Modifier | Table initialisée avec nom obligatoire et unique; opérations JDBC paramétrées |
| `src/main/java/fr/usms/gestitaches/dao/DatabaseConnection.java` | Modifier | Conserver SQLite par défaut avec une propriété système utilisable par les tests |
| `src/main/java/fr/usms/gestitaches/dao/PersistanceException.java` | Inchangé | Traduction des erreurs SQL |
| `src/main/java/fr/usms/gestitaches/service/ProjetService.java` | Modifier | Nettoyer le nom, vérifier les doublons, déléguer l'insertion |
| `src/main/java/fr/usms/gestitaches/ui/MainFrame.java` | Modifier si nécessaire | Messages d'erreur et rechargement après création réussie |
| `src/main/java/fr/usms/gestitaches/App.java` | Inchangé | Lancement Swing sur l'EDT |
| `pom.xml` | Modifier | Ajouter JUnit 5 et Surefire |
| `src/test/java/.../ProjetServiceTest.java` | Créer | Cas métier US1 |
| `src/test/java/.../ProjetDAOTest.java` | Créer | Persistance US1 |

## Détail des étapes

### Étape 1 — Modèle

Conserver `Projet` comme classe simple dans `fr.usms.gestitaches.model`, sans SQL ni Swing.

### Étape 2 — Schéma et DAO

- `projet.nom` est obligatoire et unique.
- `ProjetDAO.initialiserTable()` crée la table avec les mêmes contraintes.
- `existeParNom()` utilise `SELECT 1 ... WHERE nom = ? LIMIT 1`.
- `creer()` utilise `INSERT INTO projet (nom, description) VALUES (?, NULL)` et récupère la clé générée.
- `trouverTous()` liste les projets avec `SELECT ... FROM projet ORDER BY id`.
- `trouverParId()` charge une fiche avec `WHERE id = ?`.
- Chaque ressource SQL est ouverte dans un `try-with-resources`.
- Toute `SQLException` est traduite en `PersistanceException`.

### Étape 3 — Service

`creerProjet(nom)` :
1. refuse `null`, vide ou uniquement composé d'espaces ;
2. applique `trim()` ;
3. vérifie le doublon avec le nom normalisé ;
4. appelle le DAO ;
5. propage les erreurs de persistance.

### Étape 4 — Interface

- conserver le champ de nom, le bouton de création et la liste Swing ;
- vider le champ uniquement après succès ;
- recharger depuis SQLite après succès ;
- afficher les erreurs métier ou de persistance ;
- afficher la fiche du projet sélectionné.

### Étape 5 — Tests

Les tests couvrent les créations réussies, les noms invalides, les doublons, les lectures et la persistance.

## Vérification

Exécuter `mvn clean test` et vérifier qu'aucun test US1 n'échoue.
