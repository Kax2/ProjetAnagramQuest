# ProjetAnagramQuest
Projet Anagram Quest du cours Java Réseau

# Serveur UDP 

Ouvrez le fichier "Serveur_UDP" dans IntelliJ : 

![Choix_projet](img/udp/1.png)

Une bannière bleue pourrait apparaître si vous n'avez pas la version 18 de JDK (voir ci-dessous). Il est préférable de la télécharger afin de ne pas rencontrer d'erreurs. 

<img src="img/udp/2.png" alt="alt text" width="100%" height="100%">

Cliquez sur le fichier Serveur_UDP

<img src="img/udp/3.png" alt="alt text" width="50%" height="50%">

Cliquez sur l'icone verte "RUN":

<img src="img/udp/4.png" alt="alt text" width="50%" height="50%">

Un terminal va s'ouvrir en bas de l'application. Lorsqu'aucun paquet n'est reçu, un message d'erreur sera affiché en rouge, comme ci-dessous. 

Sinon vous pouvez envoyer un message à partir d'un nouveau terminal. 

Pour cela, entrez la commande suivante : 

```sh
nc -u localhost 20000
```

Puis écrivez le message que vous souhaitez envoyer à l'application. Une liste des commandes valides est disponible en PDF [ici](Documentation_Serveur_UDP.pdf)

![Choix_projet](img/udp/5.png)


# Serveur Web (VertX)

Commencez par ouvrir le projet dans IntelliJ, puis cliquez sur le fichier MainVerticle : 

<img src="img/web/1.png" alt="alt text" width="60%" height="60%">

A droite de l'application se trouve un onglet "Maven". Cliquez dessus

<img src="img/web/2.png" alt="alt text" width="50%" height="50%">

Puis cliquez sur l'icone "Execute Maven Goal" : 

<img src="img/web/3.png" alt="alt text" width="100%" height="100%">

Il faudra chercher la commande suvante dans la fenêtre qui s'affiche : 

```java
mvn compile exec:java
```

<img src="img/web/4.png" alt="alt text" width="100%" height="100%">

Un terminal s'ouvre alors et affichie "INFO: Succeeded in deploying verticle" si tout s'est executé correctement : 

<img src="img/web/5.png" alt="alt text" width="100%" height="100%">

Vous pouvez alors utiliser le serveur web en ecrivant l'adresse suivante dans un navigateur : 

```sh
localhost:8888
```

La page suivante devrait alors s'afficher :

<img src="img/web/6.png" alt="alt text" width="100%" height="100%">

# Application Android 

