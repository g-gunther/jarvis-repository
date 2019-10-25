# Introduction

L'objectif de ce module est d'exposer un ensemble de service REST pour uploader des modules (jar et data) du projet jarvis. Cette application enregistre également un  service DNS afin de faire du service discovery depuis des applications sur le même réseau.

# Endpoints

Les endpoints exposés sont les suivants:

* GET /jar/list: liste l'ensemble de plugins disponibles et la dernière version associée
* GET /jar/{jarName}/version/list: liste l'ensemble des versions disponibles pour un plugin donné
* GET /jar/{jarName}/version/latest: ensemble des informations de la dernière version d'un plugin donné
* GET /jar/{jarName}/version/latest/download: téléchargement de la dernière version du plugin
* GET /jar/{jarName}/version/{version}/download: téléchargement d'une version donnée du plugin
* POST /jar/{jarName}/{version}: upload d'une version du plugin
* GET /jar/{jarName}/version/latest/data/download: téléchargement de la dernière version du fichier de données du plugin 
* GET /jar/{jarName}/version/{version}/data/download: téléchargement du fichier de donnée du plugin pour une version donnée
* POST /jar/{jarName}/{version}/data: upload d'une version des données du plugin

# Service discovery

jarvis-repository utilise les librairies suivantes pour enregistrer le service DNS:

```xml
<dependency>
	<groupId>net.posick</groupId>
	<artifactId>mdnsjava</artifactId>
	<version>2.2.0</version>
</dependency>
<dependency>
	<groupId>dnsjava</groupId>
	<artifactId>dnsjava</artifactId>
	<version>2.1.6</version>
</dependency>
```

L'application démarre également un DNS service monitor dans un thread dédié pour s'assurer que le service est toujours enregistré (il peut arriver que celui-ci ne le soit plus). Ce thread va tenter de résoudre le service et si celui-ci n'existe pas, le recréé.

Le service est enregistré sous le nom suivant: jarvis.com.gguproject.jarvis:repository_v0.0.1._http.local

# Configuration

Le fichier `configuration.properties` permet de configurer les propriétés suivantes:
* repository: emplacement ou sont stocker les plugins et données
* server.port: port exposant les différents endpoints