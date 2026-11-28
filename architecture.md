# Architecture de GestiTaches

## Couches : ui -> service -> dao -> base

- ui      : Swing uniquement. Ni SQL, ni règle métier.
- service : règles métier. Appelle les DAO, ignore Swing.
- dao     : seul endroit du SQL. Une classe par entité.
- model   : classes simples, utilisées par les trois couches.

Dépendances autorisées : ui -> service, service -> dao, et toutes les couches -> model.
Dépendances interdites : ui -> dao, dao -> service, service -> ui.

## Packages

- `fr.usms.gestitaches.model`   : Projet, Tache, Utilisateur
- `fr.usms.gestitaches.dao`     : DatabaseConnection (existe déjà), PersistanceException, un DAO par entité
- `fr.usms.gestitaches.service` : un service par famille de règles
- `fr.usms.gestitaches.ui`      : fenêtres et panneaux Swing
- `fr.usms.gestitaches.App`     : assemble les couches et ouvre la fenêtre principale

## Règles de persistance

- Requêtes paramétrées (PreparedStatement) uniquement.
- Ressources JDBC ouvertes dans un try-with-resources.
- SQLException traduite en PersistanceException, jamais avalée.

## Règles métier, par user story
- US1 : le nom du projet est obligatoire -> ProjetService
- US2 : titre obligatoire, projet existant, statut initial "À faire" -> TacheService
- US3 : le membre assigné doit exister -> TacheService
- US4 : transitions de statut autorisées uniquement -> TacheService
- US5 : filtre par statut ou membre en SQL -> TacheDao