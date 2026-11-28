# GestiTaches — spécification

> Fichier à compléter dans le TP1, Partie B, à partir de `docs/besoin.md`.

## User story 1 — Créer un projet

En tant que Chef de projet, je veux creer un projet afin de organiser les taches de mon equipe.

Critères d'acceptation :

- Étant donné qu'aucun projet « Site web » n'existe
  Quand je crée un projet avec le nom « Site web »
  Alors le projet apparaît dans la liste des projets, sans aucune tâche
- Étant donné que j'ai créé un projet
  Quand je consulte sa fiche
  Alors son nom est affiché tel que je l'ai saisi
- Étant donné qu'un projet 
  Quand je crée un projet avec le nom « Site web »
  Alors on peux ajouter une courte description mais pas obligatoires

Cas limite : creation dun projet avec un nom deja existant, la création est refusée avec un message indiquant que ce projet existe deja

## User story 2 — Créer une tâche dans un projet
En tant que chef de projet ou memebre de l equipe, je veux creer une taches afin de <de décomposer le travail à réaliser..

Critères d'acceptation :

- Étant donné un projet existant
  Quand j'ajoute une tâche avec le titre « Maquette de la page d'accueil »
  Alors la tâche est rattachée à ce projet, avec le statut « à faire » et sans membre assigné
- Étant donné un projet qui contient d'autre taches
  Quand j'en ajoute une tache
  Alors le projet affiche tous les taches sans changer les anciens

Cas limite1 : si le titre est vide, la tâche est refusée avec un message d'erreur et le projet reste inchangé.
Cas limite2 : si le statut est different de {à faire}, la tâche est refusée avec un message d'erreur et le projet reste inchangé.

## User story 3 — Assigner une tâche à un membre
En tant que chef de proojet, je veux assigner une tache à un membre de l equipe afin que chacun sache ce dont il est responsable.

Critères d'acceptation :

- Étant donné une tâche sans responsable et un membre existant
  Quand j'assigne la tâche à ce membre
  Alors la tâche affiche ce membre comme responsable
- Étant donné une tâche déjà assignée à un premier membre
  Quand je l'assigne à un second membre
  Alors le second membre remplace le premier comme responsable

Cas limite : si le membre n'existe pas, l'assignation est refusée avec un message d'erreur et la tâche conserve son responsable actuel (ou reste sans responsable)

## User story 4 — Changer le statut d'une tâche
En tant que membre de l equipe, je veux hanger le statut d'une taches afin de suivre l'avancement du travail.

Critères d'acceptation :

- Étant donné une tâche au statut « à faire »
  Quand je la passe à « en cours »
  Alors son statut devient « en cours »
- Étant donné une tâche au statut « en cours » ou « à faire »
  Quand je la passe à « terminé »
  Alors son statut devient « terminé »
- Étant donné une tâche au statut « terminé » 
  Quand je la consulte 
  Alors son statut est figé


Cas limite : si le statut demandé n'est pas l'une des trois valeurs autorisées (« à faire », « en cours », « terminé »), le changement est refusé et le statut de la tâche reste inchangé.

## User story 5 — Filtrer les tâches par statut ou par membre
En tant que chef de projet, je veux filtrer les taches par statut ou par personne assignée  afin de retrouver rapidement les données qui m'intéressent.

Critères d'acceptation :

- Étant donné un projet contenant des tâches à faire, en cours et terminées
  Quand je filtre sur le statut « en cours »
  Alors seules les tâches au statut « en cours » sont affichées
- Étant donné un projet dont les tâches sont assignées à plusieurs membres
  Quand je filtre sur un membre
  Alors seules les tâches assignées à ce membre sont affichées

Cas limite : 

## Contraintes techniques

- Langage : java25
- Interface graphique :
- Persistance :
- Build :
- Conventions de nommage :
  Java 25, Swing, SQLite via JDBC, Maven