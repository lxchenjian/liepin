/*
 Navicat Premium Data Transfer

 Source Server         : 慕聘网 DEV
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : 192.168.1.121:3306
 Source Schema         : payment-center

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 08/04/2023 10:09:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for merchant_orders
-- ----------------------------
DROP TABLE IF EXISTS `merchant_orders`;
CREATE TABLE `merchant_orders` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `merchant_order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商户订单号',
  `merchant_user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商户方的发起用户的用户主键id',
  `merchant_company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商户方的发起用户所在的企业主键id',
  `amount` int NOT NULL COMMENT '实际支付总金额（包含商户所支付的订单费邮费总额）',
  `pay_method` int NOT NULL COMMENT '支付方式',
  `pay_status` int NOT NULL COMMENT '支付状态 10：未支付 20：已支付 30：支付失败 40：已退款',
  `come_from` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '从哪一端来的，比如从慕聘网这门实战过来的，注明是哪个项目的',
  `return_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付成功后的通知地址，这个是开发者那一端的，不是第三方支付通知的地址',
  `is_delete` int NOT NULL COMMENT '逻辑删除状态;1: 删除 0:未删除',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商户订单表，用于在支付中心存储查询，并且校验订单的支付状态';

-- ----------------------------
-- Table structure for user_passport
-- ----------------------------
DROP TABLE IF EXISTS `user_passport`;
CREATE TABLE `user_passport` (
  `id` int NOT NULL,
  `imooc_user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户在慕课网的用户id',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '由风间影月老师分配给用户的密码，这个密码存入数据库需要加密',
  `end_date` date NOT NULL COMMENT '用户访问有效期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
