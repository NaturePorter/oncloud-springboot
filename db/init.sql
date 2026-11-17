-- ---------------------------------------------------------------------------
-- ONCLOUD 文档管理系统 初始化脚本（仅用于全新数据库）
-- 使用方法：
--   mysql -uroot -p < db/init.sql
-- 说明：
--   1. 原有用户密码为明文，登录成功后会自动升级为 BCrypt 密文；
--   2. 角色/菜单关系已补齐，不同角色登录后看到的导航不同；
--   3. 默认账号：admin / admin（超级管理员）。
-- ---------------------------------------------------------------------------

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `dataoncloud` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `dataoncloud`;

DROP TABLE IF EXISTS `oc_document`;
CREATE TABLE `oc_document` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `docname` varchar(255) DEFAULT NULL COMMENT '文档名称',
  `realname` varchar(255) DEFAULT NULL COMMENT '文档上传真实名称',
  `perfix` varchar(255) DEFAULT NULL COMMENT '文档格式',
  `docpath` varchar(255) DEFAULT NULL COMMENT '文档路径',
  `docsize` bigint(20) DEFAULT NULL COMMENT '文档大小',
  `doclabel` varchar(255) DEFAULT NULL COMMENT '文档标签',
  `docdescribe` varchar(255) DEFAULT NULL COMMENT '文档描述',
  `downloadfrequency` bigint(20) DEFAULT NULL COMMENT '文档被下载次数',
  `uploadtime` date DEFAULT NULL COMMENT '文档上传时间',
  `uid` bigint(11) DEFAULT NULL COMMENT '文档所有者id',
  `isshared` varchar(255) DEFAULT NULL COMMENT '是否共享',
  `isdelete` varchar(255) DEFAULT NULL COMMENT '是否被删除',
  `isaudit` varchar(255) DEFAULT NULL COMMENT '是否通过审核',
  PRIMARY KEY (`id`),
  KEY `idx_document_uid` (`uid`),
  KEY `idx_document_state` (`isdelete`, `isshared`, `isaudit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `oc_menu`;
CREATE TABLE `oc_menu` (
  `menu_id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `perms` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `order_num` int(11) DEFAULT NULL,
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `oc_role`;
CREATE TABLE `oc_role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `role_name` varchar(255) DEFAULT NULL COMMENT '角色名称',
  `remark` varchar(255) DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL COMMENT '创建此角色的用户Id',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `oc_role_menu`;
CREATE TABLE `oc_role_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL,
  `menu_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_role_menu_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `oc_user`;
CREATE TABLE `oc_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '用户密码',
  `mobile` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL COMMENT '用户角色',
  `create_time` datetime DEFAULT NULL,
  `state` tinyint(4) DEFAULT NULL COMMENT '状态：1启用 0停用',
  `isdelete` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `oc_user_role`;
CREATE TABLE `oc_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_role_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `oc_user_token`;
CREATE TABLE `oc_user_token` (
  `user_id` int(11) NOT NULL,
  `token` varchar(255) DEFAULT NULL,
  `expirer_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 菜单
-- ----------------------------
INSERT INTO `oc_menu` (`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES
(1, 0, '首页', NULL, NULL, '0', 'system', 0),
(2, 1, '管理员列表', 'authc_list', NULL, '1', 'admin', 1),
(5, 0, '文件管理', NULL, NULL, '0', 'system', 0),
(6, 5, '文件列表', 'doc_list', NULL, '1', NULL, 1),
(7, 5, '文件上传', 'doc_upload', NULL, '1', NULL, 2),
(8, 1, '审核', 'audit', 'oc:doc:audit', '1', NULL, 4),
(9, 5, '文件大厅', 'doc_hall', NULL, '1', NULL, 3);

-- ----------------------------
-- 角色
-- ----------------------------
INSERT INTO `oc_role` (`role_id`, `role_name`, `remark`, `create_user_id`, `create_time`) VALUES
(1, '超级管理员', '拥有系统全部权限', 1, NOW()),
(2, '管理员', '可以审核文档、管理用户', 1, NOW()),
(3, '用户', '只能管理自己的文档', 1, NOW());

-- ----------------------------
-- 角色-菜单
-- ----------------------------
INSERT INTO `oc_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1), (1, 2), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9),
(2, 1), (2, 2), (2, 5), (2, 6), (2, 7), (2, 8), (2, 9),
(3, 1), (3, 5), (3, 6), (3, 7), (3, 9);

-- ----------------------------
-- 用户（密码为明文，首次登录后自动升级为 BCrypt）
-- ----------------------------
INSERT INTO `oc_user` (`id`, `username`, `password`, `mobile`, `email`, `role`, `create_time`, `state`, `isdelete`) VALUES
(1, 'admin', 'admin', NULL, NULL, '超级管理员', NOW(), 1, '0'),
(2, 'liming', '123456', NULL, NULL, '管理员', NOW(), 1, '0'),
(3, 'zhangsan', '123456', NULL, NULL, '管理员', NOW(), 1, '0'),
(4, 'wangwu', '321654', NULL, NULL, '管理员', NOW(), 1, '0'),
(5, 'lisi', '456789', NULL, NULL, '用户', NOW(), 1, '0'),
(9, 'yuyuyuyu', '123456', NULL, NULL, '用户', NOW(), 1, '0'),
(10, 'hahaha', '456789', NULL, NULL, '用户', NOW(), 1, '0');

-- ----------------------------
-- 用户-角色
-- ----------------------------
INSERT INTO `oc_user_role` (`user_id`, `role_id`) VALUES
(1, 1), (2, 2), (3, 2), (4, 2), (5, 3), (9, 3), (10, 3);
