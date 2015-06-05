-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jun 05, 2015 at 08:35 PM
-- Server version: 5.5.43-MariaDB-1ubuntu0.14.04.2
-- PHP Version: 5.5.9-1ubuntu4.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `Ghostrunner`
--

-- --------------------------------------------------------

--
-- Table structure for table `ext_invitations`
--

CREATE TABLE IF NOT EXISTS `ext_invitations` (
  `ext_inv_id` int(11) NOT NULL AUTO_INCREMENT,
  `pin` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `host_id` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ext_inv_id`),
  KEY `cons9` (`host_id`),
  KEY `cons10` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `groups`
--

CREATE TABLE IF NOT EXISTS `groups` (
  `group_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf32 COLLATE utf32_unicode_ci NOT NULL,
  `distance` int(11) NOT NULL,
  `ref_weekday` int(11) NOT NULL COMMENT '0 sonntag - 6 samstag',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=37 ;

--
-- Dumping data for table `groups`
--

INSERT INTO `groups` (`group_id`, `name`, `distance`, `ref_weekday`) VALUES
(35, 'gruppe1', 10000, 1),
(36, 'gruppe2', 10000, 1);

-- --------------------------------------------------------

--
-- Table structure for table `group_run`
--

CREATE TABLE IF NOT EXISTS `group_run` (
  `group_run_id` int(11) NOT NULL AUTO_INCREMENT,
  `run_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  PRIMARY KEY (`group_run_id`),
  KEY `cons2` (`run_id`),
  KEY `cons3` (`group_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=16 ;

--
-- Dumping data for table `group_run`
--

INSERT INTO `group_run` (`group_run_id`, `run_id`, `group_id`) VALUES
(6, 14, 35),
(7, 15, 35),
(8, 16, 35),
(9, 17, 35),
(10, 18, 35),
(11, 20, 35),
(12, 21, 35),
(13, 27, 36),
(14, 27, 35),
(15, 28, 35);

-- --------------------------------------------------------

--
-- Table structure for table `int_invitations`
--

CREATE TABLE IF NOT EXISTS `int_invitations` (
  `int_inv_id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `host_id` int(11) NOT NULL,
  `invitee_id` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`int_inv_id`),
  KEY `cons6` (`host_id`),
  KEY `cons7` (`invitee_id`),
  KEY `cons8` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `runs`
--

CREATE TABLE IF NOT EXISTS `runs` (
  `run_id` int(11) NOT NULL AUTO_INCREMENT,
  `distance` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `duration` bigint(20) NOT NULL,
  `actual_distance` double NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`run_id`),
  KEY `cons1` (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=29 ;

--
-- Dumping data for table `runs`
--

INSERT INTO `runs` (`run_id`, `distance`, `user_id`, `duration`, `actual_distance`, `timestamp`) VALUES
(14, 2000, 49, 1188, 9876, '2015-05-26 12:22:22'),
(15, 2000, 49, 1188, 9876, '2015-05-21 00:00:00'),
(16, 2000, 49, 1188, 9876, '2015-05-25 00:00:00'),
(17, 2000, 49, 1188, 9876, '2015-05-26 00:00:01'),
(18, 2000, 49, 1188, 9876, '2015-05-26 01:00:00'),
(19, 2000, 49, 30102, 5.048816025257111, '1970-01-17 14:01:45'),
(20, 2000, 49, 30102, 5.048816025257111, '1970-01-17 14:01:45'),
(21, 2000, 49, 30102, 5.048816025257111, '2015-05-29 13:30:41'),
(27, 2000, 50, 10114, 4.972048759460449, '2015-06-01 21:06:34'),
(28, 2000, 50, 10114, 4.972048759460449, '2015-06-01 21:06:57');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `nick` varchar(20) CHARACTER SET utf16 COLLATE utf16_unicode_ci NOT NULL,
  `password` text CHARACTER SET utf16 COLLATE utf16_bin NOT NULL COMMENT 'Hash',
  `salt` text CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `email` varchar(80) CHARACTER SET armscii8 COLLATE armscii8_bin NOT NULL,
  `last_login` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `run_timestamp` timestamp NULL DEFAULT NULL,
  `auth_token` text,
  `token_expiry` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=51 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `nick`, `password`, `salt`, `email`, `last_login`, `run_timestamp`, `auth_token`, `token_expiry`) VALUES
(49, 'username', 'Sj\0��i%2H�N{C��', 'nh1u3acu2oh1h1jgv7je5k0lc6', 'a@aa.aa', '2015-05-27 08:02:14', NULL, 'dXNlcm5hbWUhISExNDMyOTcyOTU4OTIwISEhQeGaojbvv73vv71ABO+/ve+/ve+/ve+/vXbvv73vv70=', '2015-05-30 08:02:38'),
(50, 'bullshit', 'bull', 'shit', 'qwklej@asldjk.as', '2015-05-28 13:38:06', NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `user_group`
--

CREATE TABLE IF NOT EXISTS `user_group` (
  `user_group_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `is_admin` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_group_id`),
  KEY `cons4` (`user_id`),
  KEY `cons5` (`group_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=83 ;

--
-- Dumping data for table `user_group`
--

INSERT INTO `user_group` (`user_group_id`, `user_id`, `group_id`, `is_admin`) VALUES
(73, 50, 35, 0),
(74, 50, 36, 0),
(82, 49, 35, 0);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `ext_invitations`
--
ALTER TABLE `ext_invitations`
  ADD CONSTRAINT `cons10` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`),
  ADD CONSTRAINT `cons9` FOREIGN KEY (`host_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `group_run`
--
ALTER TABLE `group_run`
  ADD CONSTRAINT `cons2` FOREIGN KEY (`run_id`) REFERENCES `runs` (`run_id`),
  ADD CONSTRAINT `cons3` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`);

--
-- Constraints for table `int_invitations`
--
ALTER TABLE `int_invitations`
  ADD CONSTRAINT `cons6` FOREIGN KEY (`host_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `cons7` FOREIGN KEY (`invitee_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `cons8` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`);

--
-- Constraints for table `runs`
--
ALTER TABLE `runs`
  ADD CONSTRAINT `cons1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `user_group`
--
ALTER TABLE `user_group`
  ADD CONSTRAINT `cons4` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `cons5` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
