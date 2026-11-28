# Plan d'implementation — User story 2 : Creer une tache dans un projet

> Plan valide avant l'implementation de l'US2.

## Critere d'acceptation principal

Etant donne un projet existant, la creation d'une tache titree « Maquette de la page d'accueil » doit produire une nouvelle tache rattachee a ce projet, au statut « a faire » et sans membre assigne.

## Perimetre

- Refuser un titre nul ou vide.
- Verifier l'existence du projet avant creation.
- Fixer le statut initial a « a faire » et le membre assigne a `NULL`.
- Autoriser une description et une echeance facultatives.
- Ajouter une tache sans modifier ni supprimer les taches existantes.
- Ne pas implementer les US3 a US5.

## Fichiers et ordre de realisation

1. `src/main/java/fr/usms/gestitaches/model/Tache.java` — modele simple des champs de la tache, sans dependance SQL ni Swing.
2. `src/main/java/fr/usms/gestitaches/dao/TacheDAO.java` — insertion parametree et lecture des taches par projet; `try-with-resources` et traduction des `SQLException` en `PersistanceException`.
3. `src/main/java/fr/usms/gestitaches/service/TacheService.java` — validation du titre, du projet et du statut initial; construction d'une nouvelle tache non assignee et delegation au DAO.
4. `src/main/java/fr/usms/gestitaches/ui/MainFrame.java` — formulaire Swing de creation et liste des taches du projet selectionne; affichage des erreurs et actualisation apres succes.
5. `src/main/java/fr/usms/gestitaches/App.java` — assemblage DAO/services et injection dans l'interface, lancee sur l'EDT.
6. `src/test/java/fr/usms/gestitaches/service/TacheServiceTest.java` — tests des validations, du rattachement, du statut, de l'absence de membre et de l'absence de modification des taches existantes.
7. `src/test/java/fr/usms/gestitaches/dao/TacheDAOTest.java` — tests d'insertion et de lecture SQLite, y compris preservation des lignes existantes.

## Points a verifier

- La tache est associee au projet choisi et porte le titre saisi.
- Son statut en base est « a faire » et `utilisateur_id` est `NULL`.
- Titre nul/vide, projet inexistant ou statut initial non autorise : aucune insertion.
- Une echeance `NULL` est acceptee.
- L'ajout d'une tache ne modifie ni ne supprime les anciennes.
- Toutes les requetes utilisant des valeurs sont parametrees et les ressources JDBC fermees.
- `mvn clean test` passe.
