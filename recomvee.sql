-- Version du serveur :  5.7.30
-- Version de PHP : 7.4.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Base de données : `recomvee`
--

-- --------------------------------------------------------

--
-- Structure de la table `followers`
--

CREATE TABLE `followers` (
  `id` int(8) NOT NULL,
  `id_user` int(8) NOT NULL,
  `id_follower` int(8) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='this table represent each user and his followers';

-- --------------------------------------------------------

--
-- Structure de la table `movie`
--

CREATE TABLE `movie` (
  `id` int(8) NOT NULL,
  `title` varchar(255) NOT NULL,
  `genres` varchar(255) NOT NULL,
  `overview` text NOT NULL,
  `production_companies` varchar(255) NOT NULL,
  `poster_path` varchar(255) NOT NULL,
  `imdb_id` varchar(255) NOT NULL,
  `popularity` varchar(8) NOT NULL,
  `release_date` varchar(12) NOT NULL,
  `data_source` varchar(8) NOT NULL DEFAULT 'kaggle'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='movie table that contains all movies informations';

-- --------------------------------------------------------

--
-- Structure de la table `rating`
--

CREATE TABLE `rating` (
  `rating_id` int(8) NOT NULL,
  `movie_id_freignkey` int(8) NOT NULL,
  `user_id_foreignkey` int(8) NOT NULL,
  `rating` int(8) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='table rating that contains all users ratings to each movie';

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE `user` (
  `id_user` int(8) NOT NULL,
  `name_user` varchar(25) NOT NULL,
  `password_user` varchar(25) NOT NULL,
  `mail_user` varchar(255) NOT NULL,
  `page_link` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='table user to store each user';

--
-- Déchargement des données de la table `user`
--

INSERT INTO `user` (`id_user`, `name_user`, `password_user`, `mail_user`, `page_link`) VALUES
(1, 'tarek', 'tarek2020', 'labaditarekk@mail.com', 'https://www.allocine.fr/membre-Z20080311183721197724977/'),
(2, 'tariko', 'tariko', 'labaditarekk@outlook.fr', 'https://www.allocine.fr/membre-Z20140719173612613475948/');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `followers`
--
ALTER TABLE `followers`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_user_ibfk_1` (`id_user`),
  ADD KEY `id_follower_ibfk_2` (`id_follower`);

--
-- Index pour la table `movie`
--
ALTER TABLE `movie`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `rating`
--
ALTER TABLE `rating`
  ADD PRIMARY KEY (`rating_id`),
  ADD KEY `id_movie_ibfk_1` (`movie_id_freignkey`),
  ADD KEY `id_user_ibfk_2` (`user_id_foreignkey`);

--
-- Index pour la table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `followers`
--
ALTER TABLE `followers`
  MODIFY `id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `movie`
--
ALTER TABLE `movie`
  MODIFY `id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `rating`
--
ALTER TABLE `rating`
  MODIFY `rating_id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int(8) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `followers`
--
ALTER TABLE `followers`
  ADD CONSTRAINT `id_follower_ibfk_2` FOREIGN KEY (`id_follower`) REFERENCES `user` (`id_user`),
  ADD CONSTRAINT `id_user_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`);

--
-- Contraintes pour la table `rating`
--
ALTER TABLE `rating`
  ADD CONSTRAINT `id_movie_ibfk_1` FOREIGN KEY (`movie_id_freignkey`) REFERENCES `movie` (`id`),
  ADD CONSTRAINT `id_user_ibfk_2` FOREIGN KEY (`user_id_foreignkey`) REFERENCES `user` (`id_user`);
