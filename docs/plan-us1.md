# Plan d'implementation — User story 1 : Creer un projet

> Plan initial valide, mis a jour apres l'ajout du critere de description facultative dans `spec.md`.

## Rappel de la user story

En tant que chef de projet, je veux créer un nouveau projet afin d'organiser le travail de l'équipe.

| # | Critère | Comment le plan y répond |
|---|---|---|
| CA1 | Création du projet « Site web » | La saisie passe par `ProjetService`, puis `ProjetDAO.creer()` insère une ligne SQLite |
| CA1 | Le projet apparaît dans la liste sans tâche | `ProjetDAO.trouverTous()` recharge les projets sans charger de tâches |
| CA1 | La fiche affiche le nom saisi | `ProjetDAO.trouverParId()` restitue le nom et `MainFrame` l'affiche |
| CA1 | Une courte description peut être ajoutée, mais n'est pas obligatoire | Champ de description facultatif, transmis par le service et persisté par le DAO; valeur vide enregistrée comme `NULL` |
| CL1 | Nom vide ou composé d'espaces | `ProjetService` normalise avec `trim()` puis refuse |
| CL1 | Nom déjà existant | `ProjetService` vérifie le nom normalisé avant l'insertion |
| CL1 | Échec sans modification | Aucun appel d'insertion n'a lieu pour une entrée invalide ou un doublon |

## Périmètre

**Inclus :**
- création d'un projet avec un nom obligatoire et une description facultative ;
- affichage de la liste et de la fiche du projet, description comprise ;
- refus des noms vides et déjà utilisés ;
- persistance SQLite avec `PreparedStatement` ;
- tests JUnit 5.

**Exclu :**
- modification ou suppression ;
- tâches, membres et autres user stories ;
- authentification.

## Fichiers concernés

| Fichier | Action | Contenu |
|---|---|---|
| `schema.sql` | Modifier | `nom TEXT NOT NULL UNIQUE` |
| `src/main/java/fr/usms/gestitaches/model/Projet.java` | Inchangé | Modèle simple avec identifiant, nom et description |
| `src/main/java/fr/usms/gestitaches/dao/ProjetDAO.java` | Modifier | Insérer et relire le nom et la description avec des opérations JDBC paramétrées |
| `src/main/java/fr/usms/gestitaches/dao/DatabaseConnection.java` | Modifier | Conserver SQLite par défaut avec une propriété système utilisable par les tests |
| `src/main/java/fr/usms/gestitaches/dao/PersistanceException.java` | Inchangé | Traduction des erreurs SQL |
| `src/main/java/fr/usms/gestitaches/service/ProjetService.java` | Modifier | Nettoyer le nom et la description, vérifier les doublons, déléguer l'insertion |
| `src/main/java/fr/usms/gestitaches/ui/MainFrame.java` | Modifier | Ajouter le champ facultatif de description et l'afficher dans la fiche |
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
- `creer(nom, description)` utilise `INSERT INTO projet (nom, description) VALUES (?, ?)` et récupère la clé générée.
- La surcharge `creer(nom)` conserve la compatibilité en déléguant avec une description `null`.
- `trouverTous()` liste les projets avec `SELECT ... FROM projet ORDER BY id`.
- `trouverParId()` charge une fiche avec `WHERE id = ?`.
- Chaque ressource SQL est ouverte dans un `try-with-resources`.
- Toute `SQLException` est traduite en `PersistanceException`.

### Étape 3 — Service

`creerProjet(nom, description)` :
1. refuse `null`, vide ou uniquement composé d'espaces ;
2. applique `trim()` ;
3. convertit la description vide ou composée d'espaces en `null`, sinon la normalise avec `trim()` ;
4. vérifie le doublon avec le nom normalisé ;
5. appelle le DAO ;
6. propage les erreurs de persistance.

La surcharge `creerProjet(nom)` délègue avec une description `null`.

### Étape 4 — Interface

- conserver le champ de nom, le bouton de création et la liste Swing ;
- ajouter un champ texte facultatif « Description » ;
- vider le champ uniquement après succès ;
- recharger depuis SQLite après succès ;
- afficher les erreurs métier ou de persistance ;
- afficher le nom et la description dans la fiche du projet sélectionné.

### Étape 5 — Tests

Les tests couvrent les créations avec description et sans description, la normalisation d'une description vide, les noms invalides, les doublons, les lectures et la persistance.

## Vérification

Exécuter `mvn clean test` et vérifier qu'aucun test US1 n'échoue.
