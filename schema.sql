-- GestiTaches - schema de la base de donnees
-- ATTENTION : ce schema contient des erreurs volontaires.
-- Votre travail (TP1, Partie C) : les retrouver et les corriger.

CREATE TABLE utilisateur (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nom TEXT NOT NULL,
  email TEXT NOT NULL
);

CREATE TABLE projet (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nom TEXT,
  description TEXT
);

CREATE TABLE tache (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  titre TEXT NOT NULL,
  description TEXT,
  statut TEXT NOT NULL,
  echeance TEXT NOT NULL,
  utilisateur_id INTEGER NOT NULL REFERENCES utilisateur(id)
);
