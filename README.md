Stadiumverwaltungssystem

Dieses Projekt implementiert ein Verwaltungssystem für ein Stadion, das Sitzplätze in Blöcken, Reihen und Platznummern organisiert. Auf Basis dieser Struktur wird für jede Person bzw. jeden Sitzplatz eine eindeutige ID generiert. Das System ermöglicht es, jederzeit den aktuellen Status eines Sitzplatzes einzusehen.

🎯 Ziel des Projekts

Das System soll eine einfache und zuverlässige Verwaltung aller Stadionplätze ermöglichen. Jeder Platz kann den folgenden Status annehmen:

FREI – der Platz ist verfügbar

BESETZT – der Platz ist aktuell vergeben

RESERVIERT – der Platz ist vorgemerkt

Diese werden über ein Enum modelliert, um klare und robuste Statusdefinitionen zu gewährleisten.

🧩 Funktionsumfang

Verwaltung einer Stadionstruktur bestehend aus:

Block

Reihe

Platznummer

Automatische Erstellung einer eindeutigen ID pro Platz (z. B. Block-Reihe-Platz)

Abfragen des Platzstatus (frei, besetzt, reserviert)

Ändern des Platzstatus

Übersicht aller Plätze eines Blocks oder des gesamten Stadions

Gültige Platzvalidierung (existiert der Platz?)

🛠️ Technologien & Konzepte

Enums für Statusverwaltung

Objektorientierte Modellierung von Block, Reihe, Platz

Einsatz von verschiedenen Datenstrukturen (LinkedList ,Set ,ArraysList ) 

Anwendung von Streams , Lambda-Ausdrücken 



🚀 Einsatzmöglichkeiten

Verwaltung von Stadion- oder Veranstaltungsplätzen

Lernprojekt für OOP, Enums und Datenstrukturen

Grundlage für Ticketing- oder Reservierungssysteme
