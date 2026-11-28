# Plan d'implementation — User story 3 : Assigner une tache a un membre

> Plan valide avant l'implementation de l'US3.

## Perimetre

- Choisir le responsable depuis la liste des membres existants.
- Permettre qu'une tache reste sans responsable.
- Une tache n'a qu'un seul responsable; une nouvelle assignation remplace la precedente.
- Refuser l'assignation a un membre inexistant sans changer le responsable actuel.
- Ne pas implementer les changements de statut (US4) ni les filtres (US5).

## Fichiers et ordre de realisation

1. `src/main/java/fr/usms/gestitaches/model/Utilisateur.java` — modele simple d'un membre (`id`, `nom`, `email`).
2. `src/main/java/fr/usms/gestitaches/dao/UtilisateurDAO.java` — lister les membres et rechercher par identifiant avec JDBC parametre.
3. `src/main/java/fr/usms/gestitaches/dao/TacheDAO.java` — rechercher une tache et mettre a jour uniquement `utilisateur_id`, y compris a `NULL`.
4. `src/main/java/fr/usms/gestitaches/service/TacheService.java` — verifier l'existence de la tache et du membre, puis deleguer l'assignation.
5. `src/main/java/fr/usms/gestitaches/ui/TachePanel.java` — lister les taches, proposer les membres existants et l'option « Non assignee », puis afficher le responsable.
6. `src/main/java/fr/usms/gestitaches/ui/MainFrame.java` — integrer `TachePanel` a l'ecran de projet sans acces direct aux DAO.
7. `src/main/java/fr/usms/gestitaches/App.java` — assembler les DAO/services et les fournir a l'interface.
8. `src/test/java/fr/usms/gestitaches/service/TacheServiceTest.java` — membres de test; tester assignation, remplacement, desassignation et refus d'un membre inconnu sans perte de l'assignation precedente.

## Points a verifier

- Une tache non assignee peut recevoir le membre choisi dans la liste.
- Le nom du responsable est visible avec la tache.
- Assigner un second membre remplace le premier sans creer une seconde assignation.
- L'option « Non assignee » sauvegarde `utilisateur_id = NULL`.
- Un membre inexistant est refuse et l'assignation actuelle reste inchangee.
- L'interface depend du service, pas du DAO.
- Les requetes sont parametrees, les ressources JDBC fermees et les `SQLException` traduites en `PersistanceException`.
- `mvn clean test` passe.
