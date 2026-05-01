[![License: FCL](https://img.shields.io/badge/License-Fair_Core-blue.svg)](LICENSE)
[![Paper](https://img.shields.io/badge/Paper-26.1.2-orange.svg)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-25-blue.svg)](https://openjdk.org)
[![Version](https://img.shields.io/badge/Version-1.0.0-green.svg)](https://github.com/ZO3N/EasyTPA/releases)

> 💡 **Usage Commercial :** Ce plugin est gratuit pour les particuliers.
> Si vous êtes une entreprise ou que vous générez des revenus avec ce plugin,
> vous **devez** me contacter pour obtenir une autorisation.

# EasyTPA
The EasyTPA for your mc server

---

## ✨ Fonctionnalités

- **Téléportation entre joueurs** — `/tpa`, `/tpahere`, `/tpaccept`, `/tpdeny`, `/tpcancel`, `/tptoggle`
- **Homes** — `/home`, `/sethome`, `/delhome` avec limite par joueur et tiers de permissions
- **Warps** — `/warp`, `/setwarp`, `/delwarp`, `/warps` avec propriété par joueur et liste cliquable
- **Spawn** — `/spawn`, `/setspawn`
- **Retour** — `/back` (position précédente ou lieu de mort configurable)
- **Téléportation aléatoire** — `/rtp` (alias `/wild`) avec chargement async des chunks
- **Rechargement** — `/tpreload`
- **Cooldown global** configurable entre deux téléportations (bypass par permission)
- **Délai anti-mouvement** — la téléportation est annulée si le joueur bouge
- **Timeout TPA** configurable — les demandes expirent automatiquement
- **Système de permissions optionnel** — désactivable pour les serveurs sans plugin de perms
- **Messages 100 % personnalisables** — format MiniMessage, couleurs, hover, click
- **Warps cliquables** dans le chat avec `/warps`

---

## 📦 Installation

1. Télécharger `easytpa-1.0.0.jar` depuis les [Releases](https://github.com/crafteria-dev/EasyTPA/releases)
2. Placer le fichier dans le dossier `plugins/` de votre serveur Paper
3. Démarrer (ou redémarrer) le serveur
4. Modifier `plugins/EasyTPA/config.yml` et `messages.yml` selon vos besoins
5. Recharger avec `/tpreload` (inutile de redémarrer)

> **Requis :** Paper 26.1.2+ — Java 25+

---

## ⚙️ Configuration — `config.yml`

```yaml
# false → tout le monde peut utiliser les commandes joueur sans permission
# true  → vérifie les permissions (compatible LuckPerms et tout plugin Bukkit)
permissions: false

# Délai avant téléportation (secondes). 0 pour désactiver.
# Le joueur ne doit pas bouger, sinon la téléportation est annulée.
teleport-delay: 3

# Durée (secondes) avant expiration d'une demande TPA
tpa-timeout: 60

# Cooldown global entre deux téléportations (secondes). 0 pour désactiver.
# Contourne avec : teleport.cooldown.bypass
teleport-cooldown: 30

# /back peut ramener au lieu de mort si true
back-on-death: true

# Nombre maximum de homes par joueur
# Si permissions: true, extensible via teleport.home.multiple.<nombre>
max-homes: 1

# Nombre maximum de warps par joueur
max-warps: 5

# Rayon maximum (blocs) pour /rtp
rtp-radius: 5000

# Rayon minimum (blocs) pour /rtp (évite le spawn)
rtp-min-radius: 100

# Tentatives maximum pour trouver une position sûre pour /rtp
rtp-max-attempts: 20

# Préfixe des messages (format MiniMessage)
messages-prefix: "<dark_gray>[<aqua>EasyTPA<dark_gray>]<reset> "
```

---

## 📋 Commandes

| Commande | Description | Permission requise |
|----------|-------------|-------------------|
| `/tpa <joueur>` | Demander à se téléporter à un joueur | `teleport.tpa` |
| `/tpahere <joueur>` | Demander à un joueur de venir à vous | `teleport.tpahere` |
| `/tpaccept` | Accepter une demande TPA | `teleport.tpaccept` |
| `/tpdeny` | Refuser une demande TPA | `teleport.tpdeny` |
| `/tpcancel` | Annuler votre demande en attente | `teleport.tpcancel` |
| `/tptoggle` | Activer/désactiver la réception de demandes | `teleport.tptoggle` |
| `/home [nom]` | Se téléporter à un home | `teleport.home` |
| `/sethome [nom]` | Définir un home à votre position | `teleport.sethome` |
| `/delhome [nom]` | Supprimer un home | `teleport.delhome` |
| `/warp <nom>` | Se téléporter à un warp | `teleport.warp` |
| `/setwarp <nom>` | Créer/modifier un warp | `teleport.setwarp` |
| `/delwarp <nom>` | Supprimer un warp | Créateur du warp ou admin |
| `/warps` | Lister les warps (cliquables) | `teleport.warps` |
| `/spawn` | Se téléporter au spawn | `teleport.spawn` |
| `/setspawn` | Définir le spawn | `teleport.admin` |
| `/back` | Retourner à la position précédente | `teleport.back` |
| `/rtp` ou `/wild` | Téléportation aléatoire sûre | `teleport.rtp` |
| `/tpreload` | Recharger la configuration | `teleport.admin` |

> Si `permissions: false` dans `config.yml`, toutes les commandes joueur sont accessibles sans permission. Les commandes admin nécessitent toujours OP ou `teleport.admin`.

---

## 🔒 Permissions

| Permission | Description | Défaut |
|-----------|-------------|--------|
| `teleport.tpa` | Utiliser /tpa | `true` |
| `teleport.tpahere` | Utiliser /tpahere | `true` |
| `teleport.tpaccept` | Utiliser /tpaccept | `true` |
| `teleport.tpdeny` | Utiliser /tpdeny | `true` |
| `teleport.tpcancel` | Utiliser /tpcancel | `true` |
| `teleport.tptoggle` | Utiliser /tptoggle | `true` |
| `teleport.home` | Utiliser /home | `true` |
| `teleport.sethome` | Utiliser /sethome | `true` |
| `teleport.delhome` | Utiliser /delhome | `true` |
| `teleport.warp` | Utiliser /warp | `true` |
| `teleport.warps` | Utiliser /warps | `true` |
| `teleport.setwarp` | Créer/modifier un warp | `op` |
| `teleport.delwarp` | Supprimer n'importe quel warp | `op` |
| `teleport.spawn` | Utiliser /spawn | `true` |
| `teleport.back` | Utiliser /back | `true` |
| `teleport.rtp` | Utiliser /rtp | `true` |
| `teleport.cooldown.bypass` | Ignorer le cooldown de téléportation | `op` |
| `teleport.home.multiple.<n>` | Avoir jusqu'à `n` homes (ex: `.5`, `.10`) | — |
| `teleport.admin` | Toutes les permissions admin | `op` |

---

## 💬 Messages — `messages.yml`

Tous les messages utilisent le format **[MiniMessage](https://docs.advntr.dev/minimessage)** d'Adventure. Vous pouvez utiliser des couleurs, effets, liens cliquables, etc.

**Placeholders disponibles :**

| Placeholder | Description |
|------------|-------------|
| `<player>` | Nom d'un joueur |
| `<home>` | Nom d'un home |
| `<warp>` | Nom d'un warp |
| `<delay>` | Délai en secondes |
| `<max>` | Limite maximale |
| `<remaining>` | Secondes restantes (cooldown) |
| `<usage>` | Syntaxe de la commande |

**Exemples de personnalisation MiniMessage :**
```yaml
# Couleurs
teleport-success: "<green>Téléportation effectuée !"

# Couleur dégradée
tpa-sent: "<gradient:#55ff55:#00aa00>Demande envoyée à <player></gradient>"

# Texte en gras avec emoji
teleport-pending: "<gold><bold>⏳ Téléportation dans <delay>s... Ne bougez pas !</bold>"

# Lien cliquable (déjà utilisé pour les boutons TPA)
tpa-received-buttons: "<green><click:run_command:/tpaccept>[Accepter]</click>"
```

---

## 🏗️ Compilation depuis les sources

**Prérequis :** Java 25, Maven 3.8+

```bash
git clone https://github.com/crafteria-dev/EasyTPA.git
cd EasyTPA
mvn package
```

Le JAR compilé se trouve dans `target/easytpa-1.0.0.jar`.

---

## 📁 Structure du projet

```
src/main/java/fr/easytpa/
├── EasyTPA.java                    # Classe principale, initialisation
├── commands/                       # Un fichier par commande (17 commandes)
│   ├── TpaCommand.java
│   ├── TpaHereCommand.java
│   ├── TpAcceptCommand.java
│   ├── TpDenyCommand.java
│   ├── TpCancelCommand.java
│   ├── TpToggleCommand.java
│   ├── HomeCommand.java
│   ├── SetHomeCommand.java
│   ├── DelHomeCommand.java
│   ├── WarpCommand.java
│   ├── SetWarpCommand.java
│   ├── DelWarpCommand.java
│   ├── WarpsCommand.java
│   ├── SpawnCommand.java
│   ├── SetSpawnCommand.java
│   ├── BackCommand.java
│   ├── RtpCommand.java
│   └── TpReloadCommand.java
├── managers/                       # Logique métier et persistance
│   ├── ConfigManager.java          # Lecture de config.yml
│   ├── MessageManager.java         # MiniMessage + préfixe
│   ├── TpaManager.java             # Demandes TPA, timeout, toggle
│   ├── TeleportDelayManager.java   # Délai, anti-mouvement, cooldown
│   ├── CooldownManager.java        # Cooldown global par joueur
│   ├── HomeManager.java            # Homes (homes.yml)
│   ├── WarpManager.java            # Warps (warps.yml)
│   ├── SpawnManager.java           # Spawn (spawn.yml)
│   └── BackManager.java            # Position précédente (mémoire)
├── listeners/
│   └── TeleportListener.java       # PlayerMove, PlayerDeath, PlayerQuit
└── utils/
    ├── LocationUtils.java          # Sérialisation YAML, vérification sécurité
    └── PermissionUtils.java        # Vérification permissions joueur/admin

src/main/resources/
├── plugin.yml                      # Déclaration commandes et permissions
├── config.yml                      # Configuration principale
└── messages.yml                    # Messages personnalisables
```

---

## 🔧 Fonctionnement interne

### Flux d'une téléportation
1. La commande (ex: `/home spawn`) appelle `TeleportDelayManager.scheduleTeleport()`
2. Le cooldown est vérifié — si actif, message et abandon
3. Le délai anti-mouvement démarre (`teleport-delay` secondes)
4. Si le joueur bouge → annulation (`PlayerMoveEvent`)
5. Sinon → `executeTeleport()` : sauvegarde la position pour `/back`, appel async `player.teleportAsync()`
6. En cas de succès → le cooldown est mis à jour, le callback envoie le message de succès

### Système de warps
- Chaque warp enregistre l'UUID du créateur (`warps.yml`)
- Un joueur ne peut supprimer ou écraser que ses propres warps
- Les admins (`teleport.admin` ou OP) peuvent gérer tous les warps
- La limite `max-warps` est par joueur (pas globale)

### Téléportation aléatoire (/rtp)
- Génère des coordonnées dans un anneau entre `rtp-min-radius` et `rtp-radius`
- Utilise `world.getChunkAtAsync()` pour charger les chunks sans bloquer le serveur
- Vérifie la sécurité de la position (2 blocs d'air, sol solide, pas de magma/feu de camp)
- Réessaie jusqu'à `rtp-max-attempts` fois si la position n'est pas sûre

---

## 📜 Licence

Ce projet est sous licence **Fair Core License (FCL)**.

- ✅ Utilisation **gratuite** pour usage personnel et serveurs non commerciaux
- ✅ Modification et redistribution du code source **avec attribution**
- ❌ Utilisation **commerciale** (génération de revenus) **sans autorisation** interdite

Pour toute demande commerciale, contactez **ZO3N** sur GitHub.

---

<div align="center">
  Fait avec ❤️ par <strong>ZO3N</strong><br>
  <a href="https://github.com/Crafteria-dev/EasyTPA">github.com/crafteria-dev/EasyTPA</a>
</div>
