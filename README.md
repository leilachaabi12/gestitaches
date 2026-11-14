# GestiTaches — dépôt de départ

Point de départ du projet fil rouge du module « Génie logiciel assisté par l'IA générative ».

## Contenu

| Fichier | Rôle |
|---|---|
| `docs/besoin.md` | Expression du besoin recueillie auprès de la cliente |
| `spec.md` | Spécification à compléter (TP1, Partie B) |
| `schema.sql` | Schéma de la base, **avec des erreurs volontaires** à corriger (TP1, Partie C) |
| `instructions-projet.modele.md` | Modèle d'instructions permanentes pour l'assistant IA (TP1, Partie D) |
| `pom.xml` | Projet Maven : Java 25, pilote JDBC SQLite |
| `src/main/java/…/App.java` | Vérifie que le projet compile et que la base est accessible |
| `src/main/java/…/dao/DatabaseConnection.java` | Ouvre les connexions vers `gestitaches.db` |

## Vérifier que tout fonctionne

```
mvn -q compile exec:java
```

Résultat attendu :

```
GestiTaches - depot de depart
Connexion a la base gestitaches.db : OK
```

## Créer la base à partir du schéma

```
sqlite3 gestitaches.db < schema.sql
```

Le fichier `gestitaches.db` n'est pas versionné (voir `.gitignore`) : il se recrée à tout moment à partir de `schema.sql`.
