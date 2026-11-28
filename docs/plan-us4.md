# Plan d'implementation — User story 4 : Changer le statut d'une tache

> Plan valide avant l'implementation de l'US4.

## Perimetre

- Autoriser « a faire » vers « en cours » ou « termine ».
- Autoriser « en cours » vers « termine ».
- Rendre le statut « termine » definitif.
- Refuser toute valeur de statut non autorisee et toute transition non permise.
- Ne pas implementer les filtres de l'US5.

## Fichiers et ordre de realisation

1. `src/main/java/fr/usms/gestitaches/service/TacheService.java` — valider la tache, les trois statuts et la transition depuis son statut actuel; refuser les transitions invalides.
2. `src/main/java/fr/usms/gestitaches/dao/TacheDAO.java` — mettre a jour le statut avec `PreparedStatement`, conditionner l'operation au statut precedent et traduire les erreurs SQL en `PersistanceException`.
3. `src/main/java/fr/usms/gestitaches/ui/TachePanel.java` — proposer seulement les transitions permises, desactiver le changement pour une tache terminee et actualiser la liste apres succes.
4. `src/test/java/fr/usms/gestitaches/service/TacheServiceTest.java` — tester chaque transition valide, les statuts invalides, les transitions interdites, une tache inconnue et le gel du statut termine.
5. `src/test/java/fr/usms/gestitaches/dao/TacheDAOTest.java` — verifier la mise a jour conditionnelle et la conservation des autres champs.

## Points a verifier

- Les transitions permises correspondent aux criteres de `spec.md`.
- Un statut invalide, une transition arriere ou une tache terminee reste inchange.
- Une modification concurrente du statut fait echouer la mise a jour conditionnelle.
- Les ressources JDBC utilisent `try-with-resources`; les erreurs SQL sont traduites.
- `mvn clean test` passe.
