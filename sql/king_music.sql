/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306密码123456
 Source Server Type    : MySQL
 Source Server Version : 80040
 Source Host           : localhost:3306
 Source Schema         : music

 Target Server Type    : MySQL
 Target Server Version : 80040
 File Encoding         : 65001

 Date: 18/05/2026 13:18:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name_UNIQUE`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (5, 'admin', '123456');

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `pic` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (1, '/img/swiper/1.jpg', 'https://github.com/1-king521/king', '');
INSERT INTO `banner` VALUES (2, '/img/swiper/17790023127872.png', 'https://github.com/1-king521/king', '');
INSERT INTO `banner` VALUES (3, '/img/swiper/17790023208883.png', 'https://github.com/1-king521/king', '');
INSERT INTO `banner` VALUES (4, '/img/swiper/17790023312324.png', 'https://github.com/1-king521/king', '');
INSERT INTO `banner` VALUES (5, '/img/swiper/17790023488025.png', 'https://github.com/1-king521/king', '');
INSERT INTO `banner` VALUES (6, '/img/swiper/17790023613966.png', 'https://github.com/1-king521/king', '');
INSERT INTO `banner` VALUES (7, '/img/swiper/17790023729407.png', 'https://github.com/1-king521/king', '');
INSERT INTO `banner` VALUES (8, '/img/swiper/17790023811828.png', 'https://github.com/1-king521/king', '');

-- ----------------------------
-- Table structure for collect
-- ----------------------------
DROP TABLE IF EXISTS `collect`;
CREATE TABLE `collect`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int UNSIGNED NOT NULL,
  `type` tinyint NOT NULL,
  `song_id` int UNSIGNED NULL DEFAULT NULL,
  `song_sheet_id` int UNSIGNED NULL DEFAULT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 124 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collect
-- ----------------------------
INSERT INTO `collect` VALUES (4, 94, 0, 23, NULL, '2019-01-07 16:41:44');
INSERT INTO `collect` VALUES (10, 94, 0, 3, NULL, '2019-01-07 16:58:59');
INSERT INTO `collect` VALUES (16, 94, 0, 24, NULL, '2019-01-07 17:34:07');
INSERT INTO `collect` VALUES (21, 5, 0, 24, NULL, '2019-01-08 15:18:33');
INSERT INTO `collect` VALUES (24, 5, 0, 8, NULL, '2019-01-08 16:07:57');
INSERT INTO `collect` VALUES (43, 5, 0, 7, NULL, '2019-04-26 01:06:20');
INSERT INTO `collect` VALUES (45, 26, 0, 44, NULL, '2020-03-21 22:26:37');
INSERT INTO `collect` VALUES (46, 26, 0, 36, NULL, '2020-03-21 22:28:24');
INSERT INTO `collect` VALUES (47, 26, 0, 69, NULL, '2020-03-22 01:56:54');
INSERT INTO `collect` VALUES (48, 26, 0, 45, NULL, '2020-03-22 02:08:36');
INSERT INTO `collect` VALUES (50, 26, 0, 100, NULL, '2020-03-22 03:41:14');
INSERT INTO `collect` VALUES (52, 12, 0, 99, NULL, '2020-04-05 21:19:06');
INSERT INTO `collect` VALUES (54, 12, 0, 9, NULL, '2020-04-26 21:47:45');
INSERT INTO `collect` VALUES (55, 12, 0, 11, NULL, '2020-04-26 21:49:33');
INSERT INTO `collect` VALUES (57, 12, 0, 10, NULL, '2020-05-01 17:08:54');
INSERT INTO `collect` VALUES (58, 12, 0, 110, NULL, '2020-05-02 16:30:26');
INSERT INTO `collect` VALUES (62, 1, 0, 7, NULL, '2022-01-21 17:33:01');
INSERT INTO `collect` VALUES (63, 1, 0, 11, NULL, '2022-01-21 17:33:47');
INSERT INTO `collect` VALUES (64, 1, 0, 5, NULL, '2022-01-21 17:34:12');
INSERT INTO `collect` VALUES (65, 1, 0, 22, NULL, '2022-01-25 16:56:55');
INSERT INTO `collect` VALUES (98, 1, 0, 28, NULL, '2022-04-21 21:37:06');
INSERT INTO `collect` VALUES (105, 61, 1, NULL, 1, '2026-04-30 11:45:41');
INSERT INTO `collect` VALUES (106, 61, 0, 21, NULL, '2026-04-30 12:26:26');
INSERT INTO `collect` VALUES (110, 61, 0, 24, NULL, '2026-04-30 13:11:19');
INSERT INTO `collect` VALUES (111, 61, 1, NULL, 2, '2026-04-30 13:12:19');
INSERT INTO `collect` VALUES (112, 61, 0, 22, NULL, '2026-04-30 13:49:47');
INSERT INTO `collect` VALUES (116, 65, 0, 121, NULL, '2026-05-16 16:03:19');
INSERT INTO `collect` VALUES (117, 65, 0, 122, NULL, '2026-05-16 16:03:20');
INSERT INTO `collect` VALUES (121, 1, 0, 122, NULL, '2026-05-16 16:19:43');
INSERT INTO `collect` VALUES (122, 65, 1, NULL, 1, '2026-05-16 16:28:39');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int UNSIGNED NOT NULL,
  `song_id` int UNSIGNED NULL DEFAULT NULL,
  `song_sheet_id` int UNSIGNED NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `type` tinyint NOT NULL,
  `like_count` int UNSIGNED NOT NULL DEFAULT 0,
  `parent_id` int NULL DEFAULT NULL COMMENT '被回复的评论id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 84 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (3, 1, 0, 1, '里面乱乱糟糟\n我们别再闹了\n这个冬天已然很冷了\n我们靠在一起。好吗', '2019-01-06 16:12:13', 1, 8, NULL);
INSERT INTO `comment` VALUES (5, 1, 21, NULL, '允儿牵动我的心!!!', '2019-01-06 18:12:53', 0, 0, NULL);
INSERT INTO `comment` VALUES (9, 1, 22, NULL, '林允儿这个人，饭她真的很骄傲。韩国人说汉语总会带着地域性极强的泡菜味，可是林允儿真的很用心在把准每一个汉字，从咬字到发音，再加上轻柔干净的嗓音加持，将柔美与舒缓表达到极致，将歌里想诉说的那种感情娓娓道来。', '2019-01-06 19:36:01', 0, 0, NULL);
INSERT INTO `comment` VALUES (10, 1, 21, NULL, '像我们之间一段长久未诉的告白，被你这样娓娓道来，你问我爱你有多深，我爱你有几分，我的情不移我的爱不变，月亮代表我的心。', '2019-01-06 19:44:37', 0, 4, NULL);
INSERT INTO `comment` VALUES (11, 1, 21, NULL, '当听这首歌曲的时候，看看天上的月亮。美爆了！', '2019-01-06 19:45:51', 0, 2, NULL);
INSERT INTO `comment` VALUES (12, 1, 23, NULL, '太尼马好听了！堂堂正正的林歌手！！', '2019-01-06 19:48:25', 0, 0, NULL);
INSERT INTO `comment` VALUES (13, 1, 23, NULL, '林允儿啊，真的唱的很标准，很动人，我的同学都没想到是林允儿唱的，呜呜呜，爱死你了林允儿', '2019-01-06 19:54:01', 0, 0, NULL);
INSERT INTO `comment` VALUES (14, 1, 22, NULL, '真的好棒，我只听她这个版本的', '2019-01-06 19:55:43', 0, 0, NULL);
INSERT INTO `comment` VALUES (16, 1, 5, NULL, '好听啊', '2019-01-06 19:56:52', 0, 0, NULL);
INSERT INTO `comment` VALUES (17, 1, 22, NULL, '我的允宝啊，努力演戏想让我们看到一样的你，努力学中文唱给我们听越来越爱你了', '2019-01-06 19:58:53', 0, 0, NULL);
INSERT INTO `comment` VALUES (18, 1, 22, NULL, '好听啊', '2019-01-06 20:01:46', 0, 0, NULL);
INSERT INTO `comment` VALUES (19, 1, 23, NULL, '好听啊', '2019-01-06 20:03:59', 0, 0, NULL);
INSERT INTO `comment` VALUES (20, 1, 21, NULL, '好听啊', '2019-01-06 20:04:22', 0, 0, NULL);
INSERT INTO `comment` VALUES (23, 1, NULL, 5, '赞！！', '2019-01-08 01:05:27', 1, 2, NULL);
INSERT INTO `comment` VALUES (24, 5, NULL, 1, '超喜欢！', '2019-01-08 21:46:29', 1, 3, NULL);
INSERT INTO `comment` VALUES (25, 5, NULL, 5, '大爱我林！', '2019-01-08 21:47:45', 1, 1, NULL);
INSERT INTO `comment` VALUES (26, 5, NULL, 2, 'nice', '2019-01-08 22:11:23', 1, 1, NULL);
INSERT INTO `comment` VALUES (27, 1, NULL, 0, '很有感觉', '2019-01-08 22:32:51', 1, 2, NULL);
INSERT INTO `comment` VALUES (28, 5, 26, NULL, '好听', '2019-01-08 22:42:07', 0, 0, NULL);
INSERT INTO `comment` VALUES (29, 5, 21, NULL, 'nice!', '2019-01-08 22:57:08', 0, 0, NULL);
INSERT INTO `comment` VALUES (30, 5, 15, NULL, '好听！', '2019-01-08 23:03:43', 0, 0, NULL);
INSERT INTO `comment` VALUES (31, 1, 13, NULL, 'rrrr', '2019-01-15 16:28:03', 0, 0, NULL);
INSERT INTO `comment` VALUES (32, 1, 19, NULL, '赞', '2019-03-07 16:34:12', 0, 0, NULL);
INSERT INTO `comment` VALUES (33, 1, 6, NULL, '赞', '2019-03-12 09:06:21', 0, 0, NULL);
INSERT INTO `comment` VALUES (34, 1, NULL, 1, 'hao', '2019-03-16 21:07:01', 1, 3, NULL);
INSERT INTO `comment` VALUES (35, 1, NULL, 38, 'hao', '2019-03-24 01:39:06', 1, 0, NULL);
INSERT INTO `comment` VALUES (36, 1, NULL, 0, '妙！', '2019-03-24 01:48:56', 1, 1, NULL);
INSERT INTO `comment` VALUES (37, 1, NULL, 80, '好听', '2019-03-24 01:51:02', 1, 0, NULL);
INSERT INTO `comment` VALUES (38, 1, NULL, 80, '好听！！！', '2019-03-24 01:52:20', 1, 0, NULL);
INSERT INTO `comment` VALUES (39, 1, NULL, 80, '好听', '2019-03-24 01:53:06', 1, 0, NULL);
INSERT INTO `comment` VALUES (40, 1, NULL, 80, 'good', '2019-03-24 01:53:45', 1, 0, NULL);
INSERT INTO `comment` VALUES (41, 1, NULL, 80, 'nice', '2019-03-24 01:55:04', 1, 0, NULL);
INSERT INTO `comment` VALUES (42, 1, NULL, 80, 'nice', '2019-03-24 01:57:02', 1, 0, NULL);
INSERT INTO `comment` VALUES (43, 1, NULL, 82, 'success', '2019-03-24 01:57:40', 1, 0, NULL);
INSERT INTO `comment` VALUES (45, 1, NULL, 1, '啦啦啦(*≧∀≦)ﾉ', '2019-04-25 21:24:43', 1, 0, NULL);
INSERT INTO `comment` VALUES (47, 1, NULL, 1, '222', '2019-04-26 01:01:27', 1, 0, NULL);
INSERT INTO `comment` VALUES (48, 5, NULL, 10, '我喜欢你', '2019-04-26 01:03:12', 1, 0, NULL);
INSERT INTO `comment` VALUES (49, 1, NULL, 0, '', '2019-05-23 21:35:47', 1, 0, NULL);
INSERT INTO `comment` VALUES (50, 1, NULL, 51, '好听', '2019-05-23 21:38:04', 1, 0, NULL);
INSERT INTO `comment` VALUES (51, 1, NULL, 5, '好听', '2019-05-23 21:39:55', 1, 0, NULL);
INSERT INTO `comment` VALUES (52, 1, NULL, 5, '好听', '2019-05-23 21:40:25', 1, 0, NULL);
INSERT INTO `comment` VALUES (53, 1, 107, NULL, 'I love you！！！', '2019-06-03 02:16:23', 0, 0, NULL);
INSERT INTO `comment` VALUES (54, 1, 95, NULL, '好听', '2020-03-14 16:14:53', 0, 0, NULL);
INSERT INTO `comment` VALUES (55, 1, 28, NULL, '?', '2020-03-14 16:19:11', 0, 0, NULL);
INSERT INTO `comment` VALUES (56, 26, 69, NULL, 'good!', '2020-03-22 02:19:03', 0, 0, NULL);
INSERT INTO `comment` VALUES (57, 26, 10, NULL, 'good', '2020-03-22 03:40:10', 0, 5, NULL);
INSERT INTO `comment` VALUES (58, 1, NULL, 3, '1111111', '2022-02-28 01:14:56', 1, 0, NULL);
INSERT INTO `comment` VALUES (59, 1, 28, NULL, '11111', '2022-03-05 16:54:31', 0, 0, NULL);
INSERT INTO `comment` VALUES (60, 1, NULL, 15, '111', '2022-04-17 13:28:08', 1, 0, NULL);
INSERT INTO `comment` VALUES (61, 1, NULL, 15, '222', '2022-04-17 13:28:17', 1, 0, NULL);
INSERT INTO `comment` VALUES (62, 1, NULL, 15, '33', '2022-04-17 13:30:19', 1, 0, NULL);
INSERT INTO `comment` VALUES (63, 1, NULL, 15, '里面乱乱糟糟 我们别再闹了 这个冬天已然很冷了 我们靠在一起。好吗.里面乱乱糟糟 我们别再闹了 这个冬天已然很冷了 我们靠在一起。好吗.里面乱乱糟糟 我们别再闹了 这个冬天已然很冷了 我们靠在一起。好吗.里面乱乱糟糟 我们别再闹了 这个冬天已然很冷了 我们靠在一起。好吗', '2022-04-17 22:57:06', 1, 0, NULL);
INSERT INTO `comment` VALUES (64, 1, NULL, 1, '456', '2022-04-21 21:41:43', 1, 1, NULL);
INSERT INTO `comment` VALUES (68, 59, NULL, 1, '345', '2022-04-22 00:57:07', 1, 0, NULL);
INSERT INTO `comment` VALUES (69, 61, NULL, 1, 'yin', '2022-10-26 22:01:49', 1, 0, NULL);
INSERT INTO `comment` VALUES (70, 61, 9, NULL, '123', '2023-01-16 00:36:21', 0, 0, NULL);
INSERT INTO `comment` VALUES (71, 61, 9, NULL, '1111', '2023-01-16 00:36:49', 0, 0, NULL);
INSERT INTO `comment` VALUES (73, 61, 21, NULL, 'good\n', '2026-04-30 11:48:06', 0, 0, NULL);
INSERT INTO `comment` VALUES (81, 65, 120, NULL, '很好', '2026-05-16 11:07:41', 0, 1, NULL);
INSERT INTO `comment` VALUES (82, 65, 122, NULL, '真好', '2026-05-16 17:34:00', 0, 3, NULL);
INSERT INTO `comment` VALUES (83, 1, 122, NULL, '确实\n', '2026-05-16 17:39:47', 0, 1, 82);

-- ----------------------------
-- Table structure for singer
-- ----------------------------
DROP TABLE IF EXISTS `singer`;
CREATE TABLE `singer`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `sex` tinyint NULL DEFAULT NULL,
  `pic` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `birth` datetime NULL DEFAULT NULL,
  `location` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `introduction` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of singer
-- ----------------------------
INSERT INTO `singer` VALUES (1, '张杰', 1, '/img/singerPic/zhangjie.jpg', '1982-12-20 00:00:00', '中国四川', '华语歌坛新生代领军人物，偶像与实力兼具的超人气天王。2004年出道至今，已发行9张高品质唱片，唱片销量称冠内地群雄。2008年以来举办过9场爆满的个人演唱会，在各大权威音乐奖项中先后21次获得“最受欢迎男歌手”称号，2012年度中国TOP排行榜内地最佳男歌手，2010年在韩国M-net亚洲音乐大赏(MAMA)上获得“亚洲之星”（Best Asian Artist）大奖，影响力触及海外。');
INSERT INTO `singer` VALUES (2, '周杰伦', 1, '/img/singerPic/zhoujielun.jpg', '1979-01-08 17:29:15', '中国台湾', '著名歌手，音乐人，词曲创作人，编曲及制作人，MV及电影导演。新世纪华语歌坛领军人物，中国风歌曲始祖，被时代周刊誉为“亚洲猫王”，是2000年后亚洲流行乐坛最具革命性与指标性的创作歌手，亚洲销量超过3100万张，有“亚洲流行天王”之称，开启华语乐坛“R&B时代”与“流行乐中国风”的先河，周杰伦的出现打破了亚洲流行乐坛长年停滞不前的局面，为亚洲流行乐坛翻开了新的一页，是华语乐坛真正把R&B提升到主流高度的人物，引领华语乐坛革命整十年，改写了华语乐坛的流行方向。');
INSERT INTO `singer` VALUES (4, '陈奕迅', 1, '/img/singerPic/chenyixun.jpg', '1974-07-27 00:00:00', '中国香港', '著名实力派粤语流行音乐歌手、演员，香港演艺人协会副会长之一，曾被美国《时代杂志》形容为影响香港乐坛风格的人物，同时也是继许冠杰、张学友之后第三个获得“歌神”称号的香港男歌手。同时他也是继张学友后另一个在台湾获得成功的香港歌手，在2003年他成为了第二个拿到台湾金曲奖“最佳国语男演唱人”的香港歌手。陈奕迅曾被《时代》杂志形容为影响香港乐坛风格的人物。2010年，陈奕迅入选全球华人音乐殿堂华语金曲奖“30年经典评选”，成为1990年代出道歌手唯一代表。');
INSERT INTO `singer` VALUES (5, 'G.E.M.邓紫棋', 0, '/img/singerPic/dengziqi.jpg', '1974-07-27 00:00:00', '中国上海', '成长于一个音乐世家，其母亲为上海音乐学院声乐系毕业生，外婆教唱歌，舅父拉小提琴，外公在乐团吹色士风。在家人的熏陶下，自小便热爱音乐，喜爱唱歌，与音乐结下不解之缘。G.E.M.在5岁的时候已经开始尝试作曲及填词，13岁完成了8级钢琴。G.E.M.在小学时期就读位于太子道西的中华基督教会协和小学上午校，为2003年的毕业生，同时亦为校内诗歌班成员。其英文名G.E.M.是Get Everybody Moving的缩写，象徵著她希望透过音乐让大家动起来的梦想。');
INSERT INTO `singer` VALUES (9, '林俊杰', 1, '/user01/singer/img/1778820846355linjunjie.jpg', '1981-03-27 00:00:00', '新加坡', '著名男歌手，作曲人、作词人、音乐制作人，偶像与实力兼具。林俊杰出生于新加坡的一个音乐世家。在父母的引导下，4岁就开始学习古典钢琴，不善言辞的他由此发现了另一种与人沟通的语言。小时候的林俊杰把哥哥林俊峰当作偶像，跟随哥哥的步伐做任何事，直到接触流行音乐后，便爱上创作这一条路。2003年以专辑《乐行者》出道，2004年一曲《江南》红遍两岸三地，凭借其健康的形象，迷人的声线，出众的唱功，卓越的才华，迅速成为华语流行乐坛的领军人物之一，迄今为止共创作数百首音乐作品，唱片销量在全亚洲逾1000万张。');
INSERT INTO `singer` VALUES (10, '王力宏', 1, '/img/singerPic/wanglihong.jpg', '1976-05-17 14:00:30', '美国', '中国著名流行歌手、词曲创作音乐人；亚洲华语流行乐坛最具代表性的创作、偶像、实力派音乐偶像巨星。1995年发行首张专辑《情敌贝多芬》在台湾出道，亦是金曲奖中最年轻的封王者，其唱片总销量在全亚洲已超过2500万张。从改编歌曲《龙的传人》融合西方电子节奏和东方旋律的华人中式嘻哈风，再到为华语流行乐注入新元素，进一步将其推向全世界。超高唱片销量便可以证明力宏的影响力毋庸置疑的强。况且身为金曲奖常客，3次接受CNN电视台访问。首创Chinked-out曲风，将中国风推向世界。');
INSERT INTO `singer` VALUES (12, '李荣浩', 1, '/img/singerPic/lironghao.jpg', '1985-07-11 21:52:23', '中国安徽', '李荣浩，1985年7月11日生于蚌埠，中国流行音乐制作人、歌手、吉他手。曾为众多艺人创作歌曲以及担任制作人，也曾为多部电影与多款电子游戏制作音乐。2013年9月17日发行个人首张原创专辑《模特》，凭借这张专辑入围第25届金曲奖最佳国语男歌手奖、最佳新人奖、最佳专辑制作人、最佳国语专辑、最佳作词奖等五项大奖提名，成为最大黑马，实现了从制作人到歌手的华丽转身。');
INSERT INTO `singer` VALUES (13, '田馥甄', 0, '/img/singerPic/tianfuzhen.jpg', '1983-03-30 21:55:44', '中国台湾', '艺名Hebe，台湾知名女艺人，女子演唱团体S.H.E组合成员，出生于台湾新竹县新丰乡，于2000年参加“宇宙2000实力美少女争霸战”选秀活动，并于同年与宇宙唱片（华研唱片前身）签约培训，接着在隔年与Selina、Ella组成S.H.E，并于2001年9月11日发行S.H.E首张专辑《女生宿舍》正式出道。2010年下半年，S.H.E正式迈向“单飞不解散”阶段，接着同年9月3日，使用本名“田馥甄”推出首张个人专辑《To Hebe》');
INSERT INTO `singer` VALUES (14, '许嵩', 1, '/img/singerPic/xusong.jpg', '1986-05-14 21:58:45', '中国安徽', '著名作词人、作曲人、唱片制作人、歌手。内地独立音乐之标杆人物，有音乐鬼才之称。2009年独立出版首张词曲全创作专辑《自定义》，2010年独立出版第二张词曲全创作专辑《寻雾启示》，两度掀起讨论热潮，一跃成为内地互联网讨论度最高的独立音乐人。2011年加盟海蝶音乐，推出第三张词曲全创作专辑《苏格拉没有底》，发行首月即在大陆地区摘下唱片销量榜冠军，轰动全国媒体，并拉开全国巡回签售及歌迷见面会。');
INSERT INTO `singer` VALUES (15, '张国荣', 1, '/img/singerPic/zhangguorong.jpg', '1956-09-12 22:02:38', '中国香港', '张国荣是一位在全球华人社会和亚洲地区具有影响力的著名歌手、演员和音乐人；大中华区乐坛和影坛巨星；演艺圈多栖发展最成功的代表之一。张国荣是香港乐坛的殿堂级歌手之一，曾获得香港乐坛最高荣誉金针奖；他是第一位享誉韩国乐坛的华人歌手，亦是华语唱片在韩国销量纪录保持者。他通晓词曲创作，曾担任过MTV导演、唱片监制、电影编剧、电影监制。他于1991年当选香港电影金像奖影帝。。。');
INSERT INTO `singer` VALUES (16, '杨宗纬', 1, '/img/singerPic/yangzongwei.jpg', '1978-04-04 22:04:47', '中国台湾', '大学时期参加台湾歌唱选秀节目《超级星光大道》获选为第一届“人气王”。比赛的时候，杨宗纬歌声阳刚而细腻，富含感情，辨识度高，感染力强，以演唱抒情歌曲见长，选曲跨越性别界线，无论是男女歌手的抒情歌曲，经过他重新诠释之后，常常都可以得到不输原唱或甚至超越原唱的评价。出道后屡创多项记录，包括发行首张专辑，便以新人之姿登上台北小巨蛋举办个人演唱会。。。');
INSERT INTO `singer` VALUES (17, '朴树', 1, '/img/singerPic/pushu.jpg', '1973-11-08 22:07:08', '中国江苏', '中国大陆歌手，音乐人。1996年10月正式成为“麦田音乐”签约歌手，为简略笔划而定艺名朴树。首支单曲《火车开往冬天》，96年底推出后成绩斐然。99年1月，推出首张个人专辑《我去两千年》。99年12月与华纳唱片正式签订唱片合约，成为其亚太区旗下的第一位内地歌手，其首张专辑《我去两千年》将由华纳重新混录和拍摄最新单曲录影带后，于2000年上半年在海外隆重上市。代表作品：《那些花儿》，《白桦林》，《生如夏花》。主要成就：中歌榜最佳新人奖，最受欢迎男歌手，年度最佳制作人奖。');
INSERT INTO `singer` VALUES (18, '李克勤', 1, '/img/singerPic/likeqin.jpg', '1967-12-06 22:10:04', '中国香港', '中国香港很有影响力的粤语流行曲歌手与演员。1985年凭《雾之恋》夺得“十九区业余歌唱大赛”冠军而晋身乐坛。曾于2002、2003和2005年度《十大劲歌金曲颁奖典礼》中三度夺得「最受欢迎男歌手」，于2003年度《叱吒乐坛流行榜颁奖典礼》上获得「叱吒乐坛我最喜爱的男歌手」，并于《第二十七届十大中文金曲颁奖典礼》(2004年度)上夺得「最优秀流行男歌手大奖」，2010年度音乐先锋榜颁奖礼 ── 20家电台联颁亚太歌手奖。');
INSERT INTO `singer` VALUES (19, '张碧晨', 0, '/img/singerPic/zhangbichen.jpg', '1989-09-10 22:15:16', '中国天津', '1989年9月10日出生于中国天津，中国女歌手。 2013年，张碧晨以韩国女子组合“Sunny days ”出道，并获得“K-POP ”世界庆典“最优秀奖”。2014年7月，张碧晨参加第三季《中国好声音》，以一首《她说》进入那英组，并于10月7日以一首《时间去哪儿了》荣获第三季《中国好声音》全国总冠军，成为《中国好声音》首位女冠军。');
INSERT INTO `singer` VALUES (23, '毛不易', 1, '/img/singerPic/maobuyi.jpg', '1994-10-01 18:59:43', '中国黑龙江', '原名王维家，1994年10月1日出生于黑龙江省齐齐哈尔市泰来县，中国内地流行乐男歌手，毕业于杭州师范大学护理专业。');
INSERT INTO `singer` VALUES (24, '胡歌', 1, '/img/singerPic/huge.jpg', '1982-09-02 17:52:02', '中国上海', '中国著名男演员、歌手，有“古装王子”的美称。2005年毕业于上海戏剧学院01级表演系本科，在电视连续剧《仙剑奇侠传》中成功塑造了豪爽深情的“李逍遥”而成名，他还为此剧唱插曲《六月的雨》《逍遥叹》等。胡歌主演过多部广为人知的影视剧，多部电视剧打破电视台收视记录。其人擅长摄影，文采飞扬，志做导演，频唱影视剧歌曲。2013年，主演的话剧《如梦之梦》和《永远的尹雪艳》登上舞台。');
INSERT INTO `singer` VALUES (25, '陈势安', 1, '/img/singerPic/chengshian.jpg', '1984-06-04 17:57:54', '马来西亚', '陈势安（1984年6月4日－）为马来西亚籍的西马男歌手，出生于槟城州威省大山脚，出道前是个化妆师。2011年发行个人第三张专辑《再爱一遍，天后陈势安》。');
INSERT INTO `singer` VALUES (26, '王菲', 0, '/img/singerPic/wangfei.jpg', '1969-08-08 17:58:31', '中国北京', '中国著名女歌手、演员。是继邓丽君后大中华地区成就最高、影响力最大的华语女歌手。以其极具辨识度的天籁空灵般嗓音，在华语歌坛创造了属于她自己的时代。她是首位登上美国《时代周刊》封面并接受CNN专访的华语歌手。她是身价最高，演唱会上座率最高，演唱会票房累计最高的华语女歌手。王菲北京出生。1987年放弃厦门大学生物系的录取跟随父母移居香港，并拜师戴思聪学习声乐，1989年签约新艺宝唱片公司并发行了第一张个人专辑，从此正式步入乐坛，曾使用艺名王靖雯。');
INSERT INTO `singer` VALUES (30, '五月天', 2, '/user01/singer/img/1778821601187wuyuetian.jpeg', '1997-03-29 00:00:00', '中国台湾', '五月天（Mayday），中国台湾摇滚乐团，由温尚翊（怪兽）、陈信宏（阿信）、石锦航（石头）、蔡升晏（玛莎）、刘谚明（冠佑）五位成员组成。\n乐团前身为“So Band”乐团，在1997年3月29日更名为“五月天”。');
INSERT INTO `singer` VALUES (31, 'Beyond', 2, '/user01/singer/img/1779034506914Beyond.png', '1983-01-01 00:00:00', '中国香港', '中国香港摇滚乐队，由黄家驹、黄贯中、黄家强、叶世荣组成。');

-- ----------------------------
-- Table structure for singer_style
-- ----------------------------
DROP TABLE IF EXISTS `singer_style`;
CREATE TABLE `singer_style`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `type` tinyint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_singer_style_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of singer_style
-- ----------------------------
INSERT INTO `singer_style` VALUES (1, '男歌手', 1);
INSERT INTO `singer_style` VALUES (2, '女歌手', 0);
INSERT INTO `singer_style` VALUES (3, '组合歌手', 2);

-- ----------------------------
-- Table structure for song
-- ----------------------------
DROP TABLE IF EXISTS `song`;
CREATE TABLE `song`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `singer_id` int UNSIGNED NOT NULL,
  `name` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `introduction` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL COMMENT '发行时间',
  `update_time` datetime NOT NULL,
  `pic` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `lyric` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `duration` int NULL DEFAULT 0 COMMENT '歌曲时长(秒)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 180 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of song
-- ----------------------------
INSERT INTO `song` VALUES (120, 5, 'G.E.M.邓紫棋-', '', '2026-05-13 13:39:41', '2026-05-16 16:17:17', '/img/songPic/tubiao.jpg', '[00:27.46] 愛像一陣風 吹完它就走 這樣的節奏 誰都無可奈何\n[00:40.85] 沒有妳以後 我靈魂失控 黑雲在降落 我被它拖著走\n[00:53.51] 靜靜悄悄默默離開 陷入了危險邊緣 Baby 我的世界已狂風暴雨\n[01:07.56] 愛情來的太快 就像龍捲風 離不開暴風圈 來不及逃\n[01:13.51] 我不能再想 我不能再想 我不 我不 我不能\n[01:20.89] 愛情走的太快 就像龍捲風 不能承受 我已無處可躲\n[01:27.17] 我不要再想 我不要再想 我不 我不 我不要再想你\n[01:34.98] 不知不覺 你已經離開我 不知不覺 我跟了這節奏\n[01:40.68] 後知後覺 又過了一個秋 後知後覺 我該好好生活\n[01:47.13] 靜靜悄悄默默離開 陷入了危險邊緣 Baby 我的世界已狂風暴雨\n[02:00.42] 愛情來的太快 就像龍捲風 離不開暴風圈 來不及逃\n[02:07.27] 我不能再想 我不能再想 我不 我不 我不能\n[02:14.27] 愛情走的太快 就像龍捲風 不能承受 我已無處可躲\n[02:20.54] 我不要再想 我不要再想 我不 我不 我不要再想你\n[02:54.37] 愛情來的太快 就像龍捲風 離不開暴風圈 來不及逃\n[03:00.31] 我不能再想 我不能再想 我不 我不 我不能\n[03:07.66] 愛情走的太快 就像龍捲風 不能承受 我已無處可躲\n[03:13.79] 我不要再想 我不要再想 我不 我不 我不要再想你\n[03:21.66] 不知不覺 你已經離開我 不知不覺 我跟了這節奏\n[03:27.15] 後知後覺 又過了一個秋 後知後覺 我該好好生活\n[03:34.14] 不知不覺 你已經離開我 不知不覺 我跟了這節奏\n[03:40.65] 後知後覺 又過了一個秋 後知後覺 我該好好生活\n[03:47.10] 不知不覺 你已經離開我 不知不覺 我跟了這節奏\n[03:54.29] 後知後覺 後知後覺\n[04:05.45]', '/user01/G.E.M.邓紫棋-龙卷风.mp3', 249);
INSERT INTO `song` VALUES (121, 5, 'G.E.M.邓紫棋-', '', '2026-05-13 13:39:59', '2026-05-16 16:16:54', '/img/songPic/tubiao.jpg', '[00:27.46] 愛像一陣風 吹完它就走 這樣的節奏 誰都無可奈何\n[00:40.85] 沒有妳以後 我靈魂失控 黑雲在降落 我被它拖著走\n[00:53.51] 靜靜悄悄默默離開 陷入了危險邊緣 Baby 我的世界已狂風暴雨\n[01:07.56] 愛情來的太快 就像龍捲風 離不開暴風圈 來不及逃\n[01:13.51] 我不能再想 我不能再想 我不 我不 我不能\n[01:20.89] 愛情走的太快 就像龍捲風 不能承受 我已無處可躲\n[01:27.17] 我不要再想 我不要再想 我不 我不 我不要再想你\n[01:34.98] 不知不覺 你已經離開我 不知不覺 我跟了這節奏\n[01:40.68] 後知後覺 又過了一個秋 後知後覺 我該好好生活\n[01:47.13] 靜靜悄悄默默離開 陷入了危險邊緣 Baby 我的世界已狂風暴雨\n[02:00.42] 愛情來的太快 就像龍捲風 離不開暴風圈 來不及逃\n[02:07.27] 我不能再想 我不能再想 我不 我不 我不能\n[02:14.27] 愛情走的太快 就像龍捲風 不能承受 我已無處可躲\n[02:20.54] 我不要再想 我不要再想 我不 我不 我不要再想你\n[02:54.37] 愛情來的太快 就像龍捲風 離不開暴風圈 來不及逃\n[03:00.31] 我不能再想 我不能再想 我不 我不 我不能\n[03:07.66] 愛情走的太快 就像龍捲風 不能承受 我已無處可躲\n[03:13.79] 我不要再想 我不要再想 我不 我不 我不要再想你\n[03:21.66] 不知不覺 你已經離開我 不知不覺 我跟了這節奏\n[03:27.15] 後知後覺 又過了一個秋 後知後覺 我該好好生活\n[03:34.14] 不知不覺 你已經離開我 不知不覺 我跟了這節奏\n[03:40.65] 後知後覺 又過了一個秋 後知後覺 我該好好生活\n[03:47.10] 不知不覺 你已經離開我 不知不覺 我跟了這節奏\n[03:54.29] 後知後覺 後知後覺\n[04:05.45]', '/user01/G.E.M.邓紫棋-泡沫.mp3', 259);
INSERT INTO `song` VALUES (122, 5, 'G.E.M.邓紫棋-夜空中最亮的星', '', '2026-05-13 13:54:17', '2026-05-16 16:15:50', '/img/songPic/tubiao.jpg', '[00:08.80] 夜空中最亮的星 能否聽清\n[00:17.03] 那仰望的人 心底的孤獨和嘆息\n[00:25.61] OH 夜空中最亮的星 能否記起\n[00:34.22] 曾與我同行 消失在風裡的身影\n[00:42.28] 我祈禱\n[00:44.00] 擁有一顆透明的心靈 和會流淚的眼睛\n[00:52.45] 給我再去相信的勇氣 越過謊言去擁抱你\n[00:59.95] 每當我 找不到存在的意義\n[01:04.45] 每當我 迷失在黑夜裡\n[01:08.95] OH 夜空中最亮的星 請照亮我前行\n[01:18.78] 夜空中最亮的星 是否知道\n[01:30.92] 曾與我同行的身影 如今在哪裡\n[01:38.72] OH 夜空中最亮的星 是否在意\n[01:48.03] 是太陽先升起 還是意外先來臨\n[01:56.38] 我祈禱\n[01:58.03] 擁有一顆透明的心靈 和會流淚的眼睛\n[02:06.40] 給我再去相信的勇氣 越過謊言去擁抱你\n[02:14.13] 每當我 找不到存在的意義\n[02:18.73] 每當我 迷失在黑夜裡\n[02:23.48] OH 夜空中最亮的星 請照亮我前行\n[02:32.97] OH 夜空中最亮的星 請照亮我前行\n[02:40.65] OH 夜空中最亮的星 請照亮我前行\n[02:50.39] OH 夜空中最亮的星 請照亮我前行\n[02:58.23] OH 夜空中最亮的星 請照亮我前行\n[03:08.23] \n[03:11.67] 我不願忘記你的眼睛\n[03:16.56] 給我再去相信的勇氣 去擁抱你\n[03:25.45] 我找不到存在的意義\n[03:29.33] 我迷失在黑夜裡\n[03:34.55] 夜空中最亮的星 請照亮我前行\n[03:45.84]', '/user01/G.E.M.邓紫棋-夜空中最亮的星.mp3', 232);
INSERT INTO `song` VALUES (123, 1, '张杰-', '', '2026-05-15 12:49:04', '2026-05-15 12:49:04', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778820543820_张杰-剑心.mp3', 251);
INSERT INTO `song` VALUES (124, 1, '张杰-', '', '2026-05-15 12:49:25', '2026-05-15 12:49:25', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778820564789_张杰-逆态度.mp3', 234);
INSERT INTO `song` VALUES (125, 1, '张杰-', '', '2026-05-15 12:49:46', '2026-05-15 12:49:46', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778820586047_张杰-逆战.mp3', 231);
INSERT INTO `song` VALUES (126, 1, '张杰-', '', '2026-05-15 12:49:56', '2026-05-15 12:49:56', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778820596198_张杰-如果爱.mp3', 262);
INSERT INTO `song` VALUES (127, 1, '张杰-', '', '2026-05-15 12:50:10', '2026-05-15 12:50:10', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778820610124_张杰-天下.mp3', 219);
INSERT INTO `song` VALUES (128, 2, '周杰伦-', '', '2026-05-15 12:50:51', '2026-05-15 12:50:51', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778820650655_周杰伦-稻香.mp3', 223);
INSERT INTO `song` VALUES (129, 2, '周杰伦-', '', '2026-05-15 12:50:59', '2026-05-15 12:50:59', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778820658962_周杰伦-听妈妈的话.mp3', 263);
INSERT INTO `song` VALUES (130, 2, '周杰伦-', '', '2026-05-15 12:51:08', '2026-05-15 12:51:08', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778820668189_周杰伦-烟花易冷.mp3', 263);
INSERT INTO `song` VALUES (131, 2, '周杰伦-', '', '2026-05-15 12:51:16', '2026-05-15 12:51:16', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778820676092_周杰伦-夜曲.mp3', 229);
INSERT INTO `song` VALUES (132, 2, '周杰伦-', '', '2026-05-15 12:51:25', '2026-05-15 12:51:25', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778820684387_周杰伦-晴天.mp3', 270);
INSERT INTO `song` VALUES (133, 4, '陈奕迅-', '', '2026-05-15 12:52:11', '2026-05-15 12:52:11', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778820730743_陈奕迅-不要说话.mp3', 285);
INSERT INTO `song` VALUES (134, 4, '陈奕迅-', '', '2026-05-15 12:52:25', '2026-05-15 12:52:25', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778820744581_陈奕迅-红玫瑰.mp3', 242);
INSERT INTO `song` VALUES (135, 4, '陈奕迅-', '', '2026-05-15 12:52:34', '2026-05-15 12:52:34', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778820753890_陈奕迅-陪你度过漫长岁月.mp3', 0);
INSERT INTO `song` VALUES (136, 4, '陈奕迅-', '', '2026-05-15 12:52:47', '2026-05-15 12:52:47', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778820766449_陈奕迅 + 王菲-因为爱情.mp3', 217);
INSERT INTO `song` VALUES (138, 9, '林俊杰-关键词', '', '2026-05-15 12:54:37', '2026-05-17 16:39:19', '/img/songPic/tubiao.jpg', '[00:00.00]作词 : 林怡凤\n[00:01.00]作曲 : 林俊杰\n[00:02.00]编曲 : 吴庆隆\n[00:03.00]制作人 : 林俊杰\n[00:14.12]好好爱自己 就有人会爱你\n[00:17.43]这乐观的说词\n[00:21.05]幸福的样子 我感觉好真实\n[00:24.31]找不到形容词\n[00:27.72]沉默在掩饰 快泛滥的激情\n[00:31.43]只剩下语助词\n[00:35.05]有一种踏实 当你口中喊我名字\n[00:40.49]落叶的位置 谱出一首诗\n[00:47.67]时间在消逝 我们的故事开始\n[00:54.34]这是第一次\n[00:58.13]让我见识爱情 可以慷慨又自私\n[01:04.67]你是我的关键词\n[01:10.22]\n[01:21.26]我不太确定 爱最好的方式\n[01:24.45]是动词或名词\n[01:28.09]很想告诉你 最赤裸的感情\n[01:31.46]却又忘词\n[01:35.35]聚散总有时 而哭笑也有时\n[01:38.49]我不怕潜台词\n[01:41.55]有一种踏实 是你心中有我名字\n[01:47.63]落叶的位置 谱出一首诗\n[01:54.41]时间在消逝 我们的故事开始\n[02:01.62]这是第一次\n[02:05.35]让我见识爱情 可以慷慨又自私\n[02:11.86]你是我的关键词\n[02:17.66]你藏在歌词\n[02:20.94]代表的意思\n[02:24.50]是专有名词\n[02:30.18]落叶的位置\n[02:33.55]谱出一首诗\n[02:37.05]我们的故事\n[02:40.51]才正要开始\n[02:44.35]这是第一次\n[02:47.33]爱一个人爱得如此慷慨又自私\n[02:54.33]你是我的关键词\n[03:03.12]配唱制作 : 林俊杰\n[03:04.12]制作协力 : 周信廷\n[03:05.12]制作协调 : Gary Leo\n[03:06.12]弦乐指挥 : Adrian Chiang\n[03:07.12]第一小提琴 : Liuyi Rerallick/Micheal Kayan/Askar Salimdjanov/Oleksandr Korniev/Li Ruoyao/Hsieh Yu Ling/Ping Hsiang Chang/Shi Xiaoxuan/Loh Wei Ken/Tsai I-Yun\n[03:08.12]第二小提琴 : Victor Martin/Cao Can/Qian Hui Ho/Kim Kyu Ri/Lim Hao Wei/Placida Ho/Aziel Verner/Sayfiddinov Mukhriddin\n[03:09.12]中提琴 : Sunatilla Saydikarimov/Mukhammadyor Tulaganov/Lin I-Chien/Wei Jun Ting/Lin Hung-Yu/Martin Peh\n[03:10.12]大提琴 : Xu Xuena/Alexander Williams/Jamshid Saydikarimov/Shin Minji/Chen Pin Jyun/Trinh Ha Linh\n[03:11.12]低音大提琴 : Zhang Jianze/Sanche Jagatheesan/Sun Chenguang\n[03:12.12]木吉他 : Jamie Wilson/Joshua Tan\n[03:13.12]低音吉他 : Shaun Seow\n[03:14.12]鼓 : Lerod Cailao\n[03:15.12]和声编写 : 简爱 Christine Chien\n[03:16.12]和声 : 简爱 Christine Chien/Carrie Yeo/陈迪雅/洪俊扬\n[03:17.12]录音环境 : 新加坡滨海艺术中心音乐厅/The JFJ Lab（台北）\n[03:18.12]录音师 : 林俊杰/周信廷/Leonard Fong\n[03:19.12]前台及舞台音响 : Frank Lee\n[03:20.12]录音软件操作 : Edwin Wijaya\n[03:21.12]混音室 : mixHaus (Encino, CA)\n[03:22.12]混音师 : Richard Furch\n[03:23.12]OP : 龙言展工作室 Heartbeats Music Production\n[03:24.12]SP : 大潮音乐经纪有限公司\n[03:25.12]OP : JFJ Productions Corp\n[03:26.12]SP : Universal Music Publishing Ltd Taiwan\n[03:27.12]ISRC TW-A53-15-76504\n[03:27.12]', '/user01/1778820876613_林俊杰-关键词.mp3', 212);
INSERT INTO `song` VALUES (140, 9, '林俊杰-林俊杰-黑暗骑士', '', '2026-05-15 12:58:21', '2026-05-15 15:59:13', '/img/songPic/tubiao.jpg', '[00:17.57] 黑暗裡誰還不睡 黑色的心情和斗篷假面\n[00:25.67] 黑夜的黑不是最黑 而在於貪婪找不到底線\n[00:33.94] 腳下是卑微的街 我孤獨站在城市天際線\n[00:42.47] 別問我惡類或善類 我只是渴望飛的哺乳類\n[00:49.07] 善惡的分界 不是對立面\n[00:57.18] 而是每個人 那最後純潔的防線 都逃不過考驗\n[01:07.03] 有沒有一種考驗 有沒有一次淬煉\n[01:11.33] 拯救了世界就像 英雄 電影 情節\n[01:15.48] 有沒有一種信念 有沒有一句誓言\n[01:21.46] 呼喚黎明的出現 yeah yeah yeah yeah\n[01:29.82] 呼喚黎明的出現 yeah yeah yeah yeah\n[01:38.47] 呼喚黎明的出現\n[01:42.49] \n[01:49.12] 為什麼抓光了賊 多年來更沒目擊過搶匪\n[01:57.46] 而貧窮還是像潮水 淹沒了人們生存的尊嚴\n[02:05.90] 文明最顛峰某天 人們和蝙蝠卻住回洞穴\n[02:14.47] 那罪行再也看不見 都躲在法律和交易後面\n[02:20.83] 善惡的分界 不怕難分辨\n[02:29.36] 只怕每個人 都關上雙耳和雙眼 都害怕去改變\n[02:38.76] 有沒有一種改變 有沒有一次壯烈\n[02:43.01] 結局的完美就像 英雄 電影 情節\n[02:47.39] 有沒有一種信念 有沒有一句誓言\n[02:53.33] 呼喚黎明的出現 yeah yeah yeah yeah\n[03:01.52] 呼喚黎明的出現 yeah yeah yeah yeah\n[03:09.97] 呼喚黎明的出現 呼喚黎明的出現\n[03:21.02] 越來越毒的雨水 越來越多的災變\n[03:24.63] 越來越遠的從前 英雄 電影 情節\n[03:28.94] 律師和小丑勾結 民代和財團簽約\n[03:35.23] 善良和罪惡妥協\n[03:37.39] 越來越大的企業 越來越小的公園\n[03:41.49] 越來越深的幻滅 英雄 電影 情節\n[03:45.69] 面具下的人是誰 或者說不管是誰\n[03:51.76] 都無法全身而退 yeah yeah yeah yeah\n[03:59.96] 都無法全身而退 yeah yeah yeah yeah\n[04:08.43] 都無法全身而退\n[04:12.50] \n[04:27.54] 當我們都走上街 當我們懷抱信念\n[04:31.44] 當我們親身扮演 英雄 電影 情節\n[04:35.73] 你就是一種信念 你就是一句誓言\n[04:42.07] 世界正等你出現 yeah yeah yeah yeah\n[04:50.38] 世界正等你出現 yeah yeah yeah yeah\n[04:58.70] Oh yeah yeah\n[05:01.23]', '/user01/1778821100576_林俊杰-黑暗骑士.mp3', 304);
INSERT INTO `song` VALUES (141, 9, '林俊杰-林俊杰-江南', '', '2026-05-15 12:58:21', '2026-05-15 12:58:21', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821100998_林俊杰-江南.mp3', 268);
INSERT INTO `song` VALUES (142, 9, '林俊杰-林俊杰-期待爱', '', '2026-05-15 12:58:22', '2026-05-16 16:28:53', '/img/songPic/tubiao.jpg', '[00:27.89] My life 我一直在等待 空蕩的口袋 想在裡面放 一份愛\n[00:39.67] Why 總是被打敗 真的好無奈 其實我 實實在在 不管帥不帥 (帥不帥)\n[00:53.69] 想要找回來\n[00:56.44] 自己的節拍\n[00:59.10] 所以這一次\n[01:02.11] 我要勇敢 大聲說出來\n[01:07.29] 期待 期待你發現我的愛 無所不在 我自然而然的關懷\n[01:17.91] 你的存在\n[01:20.72] 心靈感應的方向\n[01:23.20] 我一眼就看出來 是因為愛\n[01:30.03] 我猜 你早已發現我的愛\n[01:34.95] 繞幾個彎 靠越近越明白\n[01:40.50] 不要走開\n[01:43.22] 幸福的開始 就是放手去愛\n[01:52.18] \n[02:12.52] 想要找回來 (想要找回來)\n[02:15.45] 自己的節拍 (自己的節拍)\n[02:18.22] 所以這一次\n[02:21.16] 我要勇敢 大聲說出來\n[02:26.40] 期待 期待你發現我的愛 無所不在 我自然而然的關懷\n[02:36.99] 你的存在\n[02:39.75] 心靈感應的方向\n[02:42.18] 我一眼就看出來 是因為愛\n[02:48.89] 我猜 你早已發現我的愛\n[02:53.85] 繞幾個彎 靠越近越明白\n[02:59.54] 不要走開\n[03:02.34] 幸福的開始\n[03:04.04] 就是放手去愛\n[03:07.88] \n[03:17.24] 期待 期待你發現我的愛 無所不在 我自然而然的關懷\n[03:27.98] 你的存在\n[03:30.74] 心靈感應的方向\n[03:32.91] 我一眼就看出來 我爱你们\n[03:39.87] 我猜 你早已發現我的愛\n[03:44.82] 繞幾個彎 靠越近越明白\n[03:50.52] 不要走開\n[03:53.14] 幸福的開始\n[03:54.76] 就是放手去愛\n[04:01.77] 幸福的開始 就是放手去愛\n[04:16.49]', '/user01/1778821101668_林俊杰-期待爱.mp3', 234);
INSERT INTO `song` VALUES (143, 9, '林俊杰-林俊杰-学不会', '', '2026-05-15 12:58:22', '2026-05-15 12:58:22', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821101993_林俊杰-学不会.mp3', 230);
INSERT INTO `song` VALUES (144, 9, '林俊杰-林俊杰-一千年以后', '', '2026-05-15 12:58:23', '2026-05-15 12:58:23', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821102631_林俊杰-一千年以后.mp3', 228);
INSERT INTO `song` VALUES (145, 9, '林俊杰-林俊杰-醉赤壁', '', '2026-05-15 12:58:23', '2026-05-15 12:58:23', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821102963_林俊杰-醉赤壁.mp3', 282);
INSERT INTO `song` VALUES (146, 10, '王力宏-王力宏-大城小爱', '', '2026-05-15 12:59:10', '2026-05-15 12:59:10', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821149529_王力宏-大城小爱.mp3', 225);
INSERT INTO `song` VALUES (147, 10, '王力宏-王力宏-唯一', '', '2026-05-15 12:59:10', '2026-05-15 12:59:10', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821149859_王力宏-唯一.mp3', 262);
INSERT INTO `song` VALUES (148, 10, '王力宏-王力宏-需要人陪', '', '2026-05-15 12:59:11', '2026-05-15 12:59:11', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821150535_王力宏-需要人陪.mp3', 251);
INSERT INTO `song` VALUES (149, 12, '李荣浩-李荣浩-李白', '', '2026-05-15 12:59:44', '2026-05-15 12:59:44', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821184111_李荣浩-李白.mp3', 253);
INSERT INTO `song` VALUES (150, 12, '李荣浩-李荣浩-年少有为', '', '2026-05-15 12:59:45', '2026-05-15 12:59:45', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821184760_李荣浩-年少有为.mp3', 279);
INSERT INTO `song` VALUES (151, 12, '李荣浩-李荣浩-年少有为qqq', '', '2026-05-15 12:59:45', '2026-05-15 12:59:45', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821185025_李荣浩-年少有为qqq.mp3', 279);
INSERT INTO `song` VALUES (152, 12, '李荣浩-李荣浩-作曲家', '', '2026-05-15 12:59:45', '2026-05-15 12:59:45', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821185273_李荣浩-作曲家.mp3', 231);
INSERT INTO `song` VALUES (153, 13, '田馥甄-田馥甄-魔鬼中的天使', '', '2026-05-15 13:00:08', '2026-05-15 13:00:08', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821208205_田馥甄-魔鬼中的天使.mp3', 238);
INSERT INTO `song` VALUES (154, 13, '田馥甄-田馥甄-你就不要想起我', '', '2026-05-15 13:00:09', '2026-05-15 13:00:09', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821208774_田馥甄-你就不要想起我.mp3', 282);
INSERT INTO `song` VALUES (155, 13, '田馥甄-田馥甄-小幸运', '', '2026-05-15 13:00:09', '2026-05-15 13:00:09', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821209160_田馥甄-小幸运.mp3', 264);
INSERT INTO `song` VALUES (156, 14, '许嵩-许嵩-我乐意', '', '2026-05-15 13:00:51', '2026-05-15 13:00:51', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821251108_许嵩-我乐意.mp3', 242);
INSERT INTO `song` VALUES (157, 15, '张国荣-张国荣-当爱已成往事', '', '2026-05-15 13:01:19', '2026-05-15 13:01:19', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821278595_张国荣-当爱已成往事.mp3', 302);
INSERT INTO `song` VALUES (158, 15, '张国荣-张国荣-倩女幽魂', '', '2026-05-15 13:01:19', '2026-05-15 13:01:19', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821279260_张国荣-倩女幽魂.m4a', 218);
INSERT INTO `song` VALUES (159, 15, '张国荣-张国荣-想你', '', '2026-05-15 13:01:20', '2026-05-15 13:01:20', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821279536_张国荣-想你.m4a', 295);
INSERT INTO `song` VALUES (160, 15, '张国荣-张国荣-最冷一天', '', '2026-05-15 13:01:20', '2026-05-15 13:01:20', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821279832_张国荣-最冷一天.mp3', 258);
INSERT INTO `song` VALUES (161, 16, '杨宗纬-杨宗纬-多余', '', '2026-05-15 13:02:36', '2026-05-15 13:02:36', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821355807_杨宗纬-多余.mp3', 210);
INSERT INTO `song` VALUES (162, 16, '杨宗纬-杨宗纬-空白格', '', '2026-05-15 13:02:37', '2026-05-15 13:02:37', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821356408_杨宗纬-空白格.m4a', 218);
INSERT INTO `song` VALUES (163, 17, '朴树-朴树-白桦林', '', '2026-05-15 13:03:34', '2026-05-15 13:03:34', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821413735_朴树-白桦林.mp3', 229);
INSERT INTO `song` VALUES (164, 17, '朴树-朴树-平凡之路', '', '2026-05-15 13:03:34', '2026-05-15 13:03:34', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821414207_朴树-平凡之路.mp3', 302);
INSERT INTO `song` VALUES (165, 18, '李克勤-李克勤-K歌之王', '', '2026-05-15 13:03:54', '2026-05-15 13:03:54', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821434104_李克勤-K歌之王.mp3', 264);
INSERT INTO `song` VALUES (166, 18, '李克勤-李克勤-红日', '', '2026-05-15 13:03:55', '2026-05-15 13:03:55', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821434522_李克勤-红日.mp3', 0);
INSERT INTO `song` VALUES (167, 18, '李克勤-李克勤-护花使者', '', '2026-05-15 13:03:55', '2026-05-15 13:03:55', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821435211_李克勤-护花使者.mp3', 198);
INSERT INTO `song` VALUES (168, 18, '李克勤-李克勤-月半小夜曲', '', '2026-05-15 13:03:56', '2026-05-15 13:03:56', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821435549_李克勤-月半小夜曲.mp3', 291);
INSERT INTO `song` VALUES (169, 19, '张碧晨-张碧晨-下一秒', '', '2026-05-15 13:04:22', '2026-05-15 13:04:22', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821461664_张碧晨-下一秒.mp3', 195);
INSERT INTO `song` VALUES (170, 19, '张碧晨-张碧晨-一吻之间', '', '2026-05-15 13:04:22', '2026-05-15 13:04:22', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821462010_张碧晨-一吻之间.mp3', 248);
INSERT INTO `song` VALUES (171, 23, '毛不易-毛不易-别再闹了', '', '2026-05-15 13:04:43', '2026-05-15 13:04:43', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821482691_毛不易-别再闹了.mp3', 207);
INSERT INTO `song` VALUES (172, 23, '毛不易-毛不易-那时的我们', '', '2026-05-15 13:04:43', '2026-05-15 13:04:43', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821483015_毛不易-那时的我们.mp3', 207);
INSERT INTO `song` VALUES (173, 23, '毛不易-毛不易-无问', '', '2026-05-15 13:04:44', '2026-05-15 13:04:44', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821483661_毛不易-无问.mp3', 259);
INSERT INTO `song` VALUES (174, 30, '五月天-五月天-倔强', '', '2026-05-15 13:06:02', '2026-05-15 13:06:02', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821561868_五月天-倔强.mp3', 262);
INSERT INTO `song` VALUES (175, 30, '五月天-五月天-如果我们不曾相遇', '', '2026-05-15 13:06:03', '2026-05-15 13:06:03', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821562551_五月天-如果我们不曾相遇.mp3', 202);
INSERT INTO `song` VALUES (176, 30, '五月天-五月天-孙悟空', '', '2026-05-15 13:06:03', '2026-05-15 13:06:03', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821562881_五月天-孙悟空.mp3', 252);
INSERT INTO `song` VALUES (177, 31, 'Beyond-Beyond-光辉岁月', '', '2026-05-15 13:06:19', '2026-05-15 13:06:19', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821578251_Beyond-光辉岁月.mp3', 300);
INSERT INTO `song` VALUES (178, 31, 'Beyond-Beyond-无悔这一生', '', '2026-05-15 13:06:19', '2026-05-15 13:06:19', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821578987_Beyond-无悔这一生.mp3', 0);
INSERT INTO `song` VALUES (179, 31, 'Beyond-Beyond-真的爱你', '', '2026-05-15 13:06:20', '2026-05-15 13:06:20', '/img/songPic/tubiao.jpg', '[00:00:00]暂无歌词', '/user01/1778821579418_Beyond-真的爱你.mp3', 0);

-- ----------------------------
-- Table structure for song_sheet
-- ----------------------------
DROP TABLE IF EXISTS `song_sheet`;
CREATE TABLE `song_sheet`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `pic` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `introduction` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `style` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '无',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 87 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of song_sheet
-- ----------------------------
INSERT INTO `song_sheet` VALUES (1, '抖音热歌', '/user01/songlist/17790015404141.png', '开心一点', '华语');
INSERT INTO `song_sheet` VALUES (2, '白月光', '/user01/songlist/17790015480412.png', '致我们逝去的青春', '华语');
INSERT INTO `song_sheet` VALUES (3, '独自emo', '/user01/songlist/17790015530843.png', '别再半夜偷偷哭\n', '华语');
INSERT INTO `song_sheet` VALUES (4, '来回循环', '/user01/songlist/17790015577704.png', '说好不哭哦\n', '华语');
INSERT INTO `song_sheet` VALUES (5, '陪伴一些人的歌', '/user01/songlist/17790015628665.png', '那些喜欢到会循环播放的歌\n\n感谢收听', '华语');
INSERT INTO `song_sheet` VALUES (6, '叽叽叽叽', '/user01/songlist/17790015747356.png', '细品', '粤语');
INSERT INTO `song_sheet` VALUES (7, '又是谁的青春呢', '/user01/songlist/17790015834887.png', '不解释', '粤语');
INSERT INTO `song_sheet` VALUES (8, '经典永不过时', '/user01/songlist/17790018103378.png', '歌单', '粤语');
INSERT INTO `song_sheet` VALUES (9, '经典粤语', '/user01/songlist/17790018179619.png', '不赖', '粤语');
INSERT INTO `song_sheet` VALUES (10, '人生有几个十年', '/user01/songlist/177900182554710.png', '愿归来仍是少年', '粤语');
INSERT INTO `song_sheet` VALUES (11, '旋律之上', '/user01/songlist/177900201967911.png', '听不懂也听', '欧美');
INSERT INTO `song_sheet` VALUES (12, '少年英雄梦', '/user01/songlist/177900202675212.png', '有一天。。。', '欧美');
INSERT INTO `song_sheet` VALUES (13, '早点睡', '/user01/songlist/177900203557213.png', '别熬夜', '欧美');
INSERT INTO `song_sheet` VALUES (14, '我怀念的~', '/user01/songlist/177900204166914.png', '抬头看天空', '欧美');
INSERT INTO `song_sheet` VALUES (15, '闭上眼睛', '/user01/songlist/177900204616515.png', '认真倾听', '欧美');
INSERT INTO `song_sheet` VALUES (16, '擦浪嘿', '/user01/songlist/177900205585416.png', '', '日韩');
INSERT INTO `song_sheet` VALUES (17, '哈尼呀谁有', '/user01/songlist/177900206147017.png', '谁懂', '日韩');
INSERT INTO `song_sheet` VALUES (18, '以后会懂的', '/user01/songlist/177900206531518.png', '欢迎来评论', '日韩');
INSERT INTO `song_sheet` VALUES (19, '耳朵痒痒的', '/user01/songlist/177900207065419.png', '', '日韩');
INSERT INTO `song_sheet` VALUES (20, '以后在想吧', '/user01/songlist/177900207611220.png', '很懒', '日韩');
INSERT INTO `song_sheet` VALUES (21, '念念不忘', '/user01/songlist/177900208336621.png', '必有回响', '轻音乐');
INSERT INTO `song_sheet` VALUES (22, '向云端', '/user01/songlist/177900209043322.png', '别低头', '轻音乐');
INSERT INTO `song_sheet` VALUES (23, '多出去走走', '/user01/songlist/177900209713123.png', '看世界', '轻音乐');
INSERT INTO `song_sheet` VALUES (24, '看到图像你会想起谁', '/user01/songlist/177900210445424.png', '男生白月光', '轻音乐');
INSERT INTO `song_sheet` VALUES (25, '做你想做的', '/user01/songlist/177900211137725.png', 'just do it', '轻音乐');
INSERT INTO `song_sheet` VALUES (26, '超强律动', '/user01/songlist/177900211954326.png', '燃起来了', 'BGM');
INSERT INTO `song_sheet` VALUES (27, '继续律动', '/user01/songlist/177900212394027.png', '动起来', 'BGM');
INSERT INTO `song_sheet` VALUES (28, '超燃！', '/user01/songlist/177900212925128.png', '本尊！', 'BGM');
INSERT INTO `song_sheet` VALUES (29, 'FPS的神', '/user01/songlist/177900213375429.png', '感受它 不要去操纵它\n', 'BGM');
INSERT INTO `song_sheet` VALUES (30, '别叫妈妈好嘛', '/user01/songlist/177900214133030.png', '哇学弟', 'BGM');
INSERT INTO `song_sheet` VALUES (31, '弹着吉他 唱着歌', '/user01/songlist/177900214888931.png', '哭了', '乐器');
INSERT INTO `song_sheet` VALUES (32, '民谣', '/user01/songlist/177900215479832.png', '感受', '乐器');
INSERT INTO `song_sheet` VALUES (33, '嗨起来', '/user01/songlist/177900215906333.png', '丫够燥de', '乐器');
INSERT INTO `song_sheet` VALUES (34, '先听', '/user01/songlist/177900216417834.png', '', '乐器');
INSERT INTO `song_sheet` VALUES (35, '继续听', '/user01/songlist/177900216966635.png', '', '乐器');
INSERT INTO `song_sheet` VALUES (36, '夏日 微风 她', '/user01/songlist/177900217866936.png', '可以吗', '流行');
INSERT INTO `song_sheet` VALUES (37, '又到了听冬眠的季节了', '/user01/songlist/177900218265637.png', '别感冒', '流行');
INSERT INTO `song_sheet` VALUES (38, '别伤心', '/user01/songlist/177900218677838.png', '佛系一点', '流行');
INSERT INTO `song_sheet` VALUES (39, '会想起她嘛', '/user01/songlist/177900219159139.png', 'i miss you', '流行');
INSERT INTO `song_sheet` VALUES (40, '有点意思', '/user01/songlist/177900219804640.png', '有趣', '流行');

-- ----------------------------
-- Table structure for song_sheet_song
-- ----------------------------
DROP TABLE IF EXISTS `song_sheet_song`;
CREATE TABLE `song_sheet_song`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `song_id` int UNSIGNED NOT NULL,
  `song_sheet_id` int UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 220 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of song_sheet_song
-- ----------------------------
INSERT INTO `song_sheet_song` VALUES (1, 36, 1);
INSERT INTO `song_sheet_song` VALUES (4, 7, 2);
INSERT INTO `song_sheet_song` VALUES (5, 11, 2);
INSERT INTO `song_sheet_song` VALUES (6, 38, 6);
INSERT INTO `song_sheet_song` VALUES (7, 39, 6);
INSERT INTO `song_sheet_song` VALUES (8, 44, 1);
INSERT INTO `song_sheet_song` VALUES (9, 22, 2);
INSERT INTO `song_sheet_song` VALUES (10, 22, 12);
INSERT INTO `song_sheet_song` VALUES (11, 38, 5);
INSERT INTO `song_sheet_song` VALUES (12, 39, 5);
INSERT INTO `song_sheet_song` VALUES (13, 38, 5);
INSERT INTO `song_sheet_song` VALUES (14, 39, 5);
INSERT INTO `song_sheet_song` VALUES (15, 45, 4);
INSERT INTO `song_sheet_song` VALUES (16, 45, 12);
INSERT INTO `song_sheet_song` VALUES (17, 10, 13);
INSERT INTO `song_sheet_song` VALUES (18, 10, 2);
INSERT INTO `song_sheet_song` VALUES (19, 28, 3);
INSERT INTO `song_sheet_song` VALUES (20, 10, 3);
INSERT INTO `song_sheet_song` VALUES (21, 30, 10);
INSERT INTO `song_sheet_song` VALUES (22, 31, 10);
INSERT INTO `song_sheet_song` VALUES (23, 82, 6);
INSERT INTO `song_sheet_song` VALUES (24, 83, 6);
INSERT INTO `song_sheet_song` VALUES (25, 84, 6);
INSERT INTO `song_sheet_song` VALUES (26, 85, 6);
INSERT INTO `song_sheet_song` VALUES (27, 99, 7);
INSERT INTO `song_sheet_song` VALUES (28, 100, 8);
INSERT INTO `song_sheet_song` VALUES (29, 78, 9);
INSERT INTO `song_sheet_song` VALUES (30, 79, 9);
INSERT INTO `song_sheet_song` VALUES (31, 80, 9);
INSERT INTO `song_sheet_song` VALUES (32, 86, 7);
INSERT INTO `song_sheet_song` VALUES (33, 87, 7);
INSERT INTO `song_sheet_song` VALUES (34, 88, 8);
INSERT INTO `song_sheet_song` VALUES (35, 100, 7);
INSERT INTO `song_sheet_song` VALUES (36, 82, 11);
INSERT INTO `song_sheet_song` VALUES (37, 65, 11);
INSERT INTO `song_sheet_song` VALUES (38, 50, 11);
INSERT INTO `song_sheet_song` VALUES (39, 67, 14);
INSERT INTO `song_sheet_song` VALUES (40, 78, 14);
INSERT INTO `song_sheet_song` VALUES (41, 26, 14);
INSERT INTO `song_sheet_song` VALUES (42, 4, 15);
INSERT INTO `song_sheet_song` VALUES (43, 7, 15);
INSERT INTO `song_sheet_song` VALUES (44, 21, 15);
INSERT INTO `song_sheet_song` VALUES (45, 24, 16);
INSERT INTO `song_sheet_song` VALUES (46, 40, 16);
INSERT INTO `song_sheet_song` VALUES (47, 50, 16);
INSERT INTO `song_sheet_song` VALUES (48, 70, 16);
INSERT INTO `song_sheet_song` VALUES (49, 72, 17);
INSERT INTO `song_sheet_song` VALUES (50, 73, 17);
INSERT INTO `song_sheet_song` VALUES (51, 51, 18);
INSERT INTO `song_sheet_song` VALUES (52, 52, 18);
INSERT INTO `song_sheet_song` VALUES (53, 65, 18);
INSERT INTO `song_sheet_song` VALUES (54, 67, 18);
INSERT INTO `song_sheet_song` VALUES (55, 2, 19);
INSERT INTO `song_sheet_song` VALUES (56, 7, 19);
INSERT INTO `song_sheet_song` VALUES (57, 55, 19);
INSERT INTO `song_sheet_song` VALUES (58, 53, 19);
INSERT INTO `song_sheet_song` VALUES (59, 54, 19);
INSERT INTO `song_sheet_song` VALUES (60, 4, 20);
INSERT INTO `song_sheet_song` VALUES (61, 7, 20);
INSERT INTO `song_sheet_song` VALUES (62, 11, 20);
INSERT INTO `song_sheet_song` VALUES (63, 26, 20);
INSERT INTO `song_sheet_song` VALUES (64, 99, 21);
INSERT INTO `song_sheet_song` VALUES (65, 100, 21);
INSERT INTO `song_sheet_song` VALUES (66, 86, 21);
INSERT INTO `song_sheet_song` VALUES (67, 91, 22);
INSERT INTO `song_sheet_song` VALUES (68, 94, 22);
INSERT INTO `song_sheet_song` VALUES (69, 77, 22);
INSERT INTO `song_sheet_song` VALUES (70, 68, 22);
INSERT INTO `song_sheet_song` VALUES (71, 50, 22);
INSERT INTO `song_sheet_song` VALUES (72, 76, 17);
INSERT INTO `song_sheet_song` VALUES (73, 93, 15);
INSERT INTO `song_sheet_song` VALUES (74, 92, 15);
INSERT INTO `song_sheet_song` VALUES (75, 78, 72);
INSERT INTO `song_sheet_song` VALUES (76, 79, 72);
INSERT INTO `song_sheet_song` VALUES (77, 80, 72);
INSERT INTO `song_sheet_song` VALUES (78, 64, 71);
INSERT INTO `song_sheet_song` VALUES (79, 65, 71);
INSERT INTO `song_sheet_song` VALUES (80, 50, 71);
INSERT INTO `song_sheet_song` VALUES (81, 51, 71);
INSERT INTO `song_sheet_song` VALUES (82, 51, 70);
INSERT INTO `song_sheet_song` VALUES (83, 50, 70);
INSERT INTO `song_sheet_song` VALUES (84, 64, 62);
INSERT INTO `song_sheet_song` VALUES (85, 65, 62);
INSERT INTO `song_sheet_song` VALUES (86, 66, 62);
INSERT INTO `song_sheet_song` VALUES (87, 67, 62);
INSERT INTO `song_sheet_song` VALUES (88, 25, 63);
INSERT INTO `song_sheet_song` VALUES (89, 26, 63);
INSERT INTO `song_sheet_song` VALUES (90, 79, 63);
INSERT INTO `song_sheet_song` VALUES (91, 65, 64);
INSERT INTO `song_sheet_song` VALUES (92, 64, 64);
INSERT INTO `song_sheet_song` VALUES (93, 80, 64);
INSERT INTO `song_sheet_song` VALUES (94, 25, 65);
INSERT INTO `song_sheet_song` VALUES (95, 64, 65);
INSERT INTO `song_sheet_song` VALUES (96, 67, 67);
INSERT INTO `song_sheet_song` VALUES (97, 64, 67);
INSERT INTO `song_sheet_song` VALUES (98, 25, 67);
INSERT INTO `song_sheet_song` VALUES (99, 25, 69);
INSERT INTO `song_sheet_song` VALUES (100, 24, 69);
INSERT INTO `song_sheet_song` VALUES (101, 25, 69);
INSERT INTO `song_sheet_song` VALUES (102, 26, 69);
INSERT INTO `song_sheet_song` VALUES (103, 48, 69);
INSERT INTO `song_sheet_song` VALUES (104, 80, 68);
INSERT INTO `song_sheet_song` VALUES (105, 64, 68);
INSERT INTO `song_sheet_song` VALUES (106, 25, 68);
INSERT INTO `song_sheet_song` VALUES (107, 67, 66);
INSERT INTO `song_sheet_song` VALUES (108, 64, 66);
INSERT INTO `song_sheet_song` VALUES (109, 80, 66);
INSERT INTO `song_sheet_song` VALUES (110, 102, 23);
INSERT INTO `song_sheet_song` VALUES (112, 101, 25);
INSERT INTO `song_sheet_song` VALUES (113, 102, 30);
INSERT INTO `song_sheet_song` VALUES (114, 102, 32);
INSERT INTO `song_sheet_song` VALUES (115, 101, 34);
INSERT INTO `song_sheet_song` VALUES (116, 42, 36);
INSERT INTO `song_sheet_song` VALUES (117, 43, 36);
INSERT INTO `song_sheet_song` VALUES (118, 41, 36);
INSERT INTO `song_sheet_song` VALUES (119, 36, 38);
INSERT INTO `song_sheet_song` VALUES (120, 37, 38);
INSERT INTO `song_sheet_song` VALUES (121, 101, 38);
INSERT INTO `song_sheet_song` VALUES (122, 101, 37);
INSERT INTO `song_sheet_song` VALUES (123, 102, 39);
INSERT INTO `song_sheet_song` VALUES (124, 37, 40);
INSERT INTO `song_sheet_song` VALUES (125, 108, 40);
INSERT INTO `song_sheet_song` VALUES (126, 102, 40);
INSERT INTO `song_sheet_song` VALUES (127, 112, 41);
INSERT INTO `song_sheet_song` VALUES (128, 102, 41);
INSERT INTO `song_sheet_song` VALUES (129, 102, 42);
INSERT INTO `song_sheet_song` VALUES (130, 41, 24);
INSERT INTO `song_sheet_song` VALUES (131, 100, 23);
INSERT INTO `song_sheet_song` VALUES (132, 98, 47);
INSERT INTO `song_sheet_song` VALUES (133, 61, 47);
INSERT INTO `song_sheet_song` VALUES (134, 62, 47);
INSERT INTO `song_sheet_song` VALUES (135, 33, 49);
INSERT INTO `song_sheet_song` VALUES (136, 68, 49);
INSERT INTO `song_sheet_song` VALUES (137, 33, 49);
INSERT INTO `song_sheet_song` VALUES (138, 23, 49);
INSERT INTO `song_sheet_song` VALUES (139, 33, 50);
INSERT INTO `song_sheet_song` VALUES (140, 21, 50);
INSERT INTO `song_sheet_song` VALUES (141, 61, 52);
INSERT INTO `song_sheet_song` VALUES (142, 62, 52);
INSERT INTO `song_sheet_song` VALUES (143, 21, 60);
INSERT INTO `song_sheet_song` VALUES (144, 22, 60);
INSERT INTO `song_sheet_song` VALUES (145, 23, 60);
INSERT INTO `song_sheet_song` VALUES (146, 63, 58);
INSERT INTO `song_sheet_song` VALUES (147, 98, 58);
INSERT INTO `song_sheet_song` VALUES (148, 63, 53);
INSERT INTO `song_sheet_song` VALUES (149, 30, 54);
INSERT INTO `song_sheet_song` VALUES (150, 61, 56);
INSERT INTO `song_sheet_song` VALUES (151, 63, 56);
INSERT INTO `song_sheet_song` VALUES (152, 98, 57);
INSERT INTO `song_sheet_song` VALUES (153, 32, 54);
INSERT INTO `song_sheet_song` VALUES (154, 22, 57);
INSERT INTO `song_sheet_song` VALUES (155, 98, 59);
INSERT INTO `song_sheet_song` VALUES (156, 63, 59);
INSERT INTO `song_sheet_song` VALUES (157, 62, 61);
INSERT INTO `song_sheet_song` VALUES (158, 22, 61);
INSERT INTO `song_sheet_song` VALUES (159, 68, 51);
INSERT INTO `song_sheet_song` VALUES (160, 35, 51);
INSERT INTO `song_sheet_song` VALUES (161, 32, 51);
INSERT INTO `song_sheet_song` VALUES (162, 33, 61);
INSERT INTO `song_sheet_song` VALUES (163, 86, 43);
INSERT INTO `song_sheet_song` VALUES (164, 100, 44);
INSERT INTO `song_sheet_song` VALUES (165, 87, 45);
INSERT INTO `song_sheet_song` VALUES (166, 86, 45);
INSERT INTO `song_sheet_song` VALUES (167, 100, 44);
INSERT INTO `song_sheet_song` VALUES (168, 88, 46);
INSERT INTO `song_sheet_song` VALUES (169, 99, 73);
INSERT INTO `song_sheet_song` VALUES (170, 88, 74);
INSERT INTO `song_sheet_song` VALUES (171, 99, 74);
INSERT INTO `song_sheet_song` VALUES (172, 88, 73);
INSERT INTO `song_sheet_song` VALUES (173, 103, 78);
INSERT INTO `song_sheet_song` VALUES (174, 103, 84);
INSERT INTO `song_sheet_song` VALUES (175, 103, 75);
INSERT INTO `song_sheet_song` VALUES (176, 103, 76);
INSERT INTO `song_sheet_song` VALUES (177, 103, 77);
INSERT INTO `song_sheet_song` VALUES (178, 103, 79);
INSERT INTO `song_sheet_song` VALUES (179, 88, 80);
INSERT INTO `song_sheet_song` VALUES (180, 99, 80);
INSERT INTO `song_sheet_song` VALUES (181, 103, 80);
INSERT INTO `song_sheet_song` VALUES (182, 104, 80);
INSERT INTO `song_sheet_song` VALUES (183, 104, 81);
INSERT INTO `song_sheet_song` VALUES (184, 88, 82);
INSERT INTO `song_sheet_song` VALUES (185, 99, 82);
INSERT INTO `song_sheet_song` VALUES (186, 105, 83);
INSERT INTO `song_sheet_song` VALUES (187, 99, 48);
INSERT INTO `song_sheet_song` VALUES (188, 95, 26);
INSERT INTO `song_sheet_song` VALUES (189, 96, 27);
INSERT INTO `song_sheet_song` VALUES (190, 97, 26);
INSERT INTO `song_sheet_song` VALUES (191, 95, 28);
INSERT INTO `song_sheet_song` VALUES (192, 98, 29);
INSERT INTO `song_sheet_song` VALUES (193, 62, 29);
INSERT INTO `song_sheet_song` VALUES (194, 87, 31);
INSERT INTO `song_sheet_song` VALUES (195, 61, 31);
INSERT INTO `song_sheet_song` VALUES (196, 63, 31);
INSERT INTO `song_sheet_song` VALUES (197, 87, 55);
INSERT INTO `song_sheet_song` VALUES (198, 96, 55);
INSERT INTO `song_sheet_song` VALUES (199, 98, 33);
INSERT INTO `song_sheet_song` VALUES (200, 63, 33);
INSERT INTO `song_sheet_song` VALUES (201, 105, 83);
INSERT INTO `song_sheet_song` VALUES (202, 106, 83);
INSERT INTO `song_sheet_song` VALUES (203, 107, 53);
INSERT INTO `song_sheet_song` VALUES (204, 107, 60);
INSERT INTO `song_sheet_song` VALUES (205, 108, 8);
INSERT INTO `song_sheet_song` VALUES (206, 112, 24);
INSERT INTO `song_sheet_song` VALUES (207, 113, 40);
INSERT INTO `song_sheet_song` VALUES (208, 109, 8);
INSERT INTO `song_sheet_song` VALUES (209, 107, 23);
INSERT INTO `song_sheet_song` VALUES (212, 142, 2);
INSERT INTO `song_sheet_song` VALUES (215, 140, 2);
INSERT INTO `song_sheet_song` VALUES (219, 138, 2);

-- ----------------------------
-- Table structure for song_sheet_style
-- ----------------------------
DROP TABLE IF EXISTS `song_sheet_style`;
CREATE TABLE `song_sheet_style`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_song_sheet_style_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of song_sheet_style
-- ----------------------------
INSERT INTO `song_sheet_style` VALUES (6, 'BGM');
INSERT INTO `song_sheet_style` VALUES (7, '乐器');
INSERT INTO `song_sheet_style` VALUES (1, '华语');
INSERT INTO `song_sheet_style` VALUES (4, '日韩');
INSERT INTO `song_sheet_style` VALUES (3, '欧美');
INSERT INTO `song_sheet_style` VALUES (8, '流行');
INSERT INTO `song_sheet_style` VALUES (2, '粤语');
INSERT INTO `song_sheet_style` VALUES (5, '轻音乐');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `sex` tinyint NULL DEFAULT NULL,
  `phone_num` char(15) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `email` char(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `birth` datetime NULL DEFAULT NULL,
  `introduction` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `location` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username_UNIQUE`(`username` ASC) USING BTREE,
  UNIQUE INDEX `phone_num_UNIQUE`(`phone_num` ASC) USING BTREE,
  UNIQUE INDEX `email_UNIQUE`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 66 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (65, 'wyk', '123', 2, '12345678977', '3341317107@qq.com', '2026-05-13 00:00:00', '', '', '/img/avatorImages/17790125100141.png', '2026-05-13 03:52:30', '2026-05-17 18:08:30');

-- ----------------------------
-- Table structure for user_play_history
-- ----------------------------
DROP TABLE IF EXISTS `user_play_history`;
CREATE TABLE `user_play_history`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int UNSIGNED NOT NULL,
  `song_id` int UNSIGNED NOT NULL,
  `play_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `song_id`(`song_id` ASC) USING BTREE,
  CONSTRAINT `user_play_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_play_history_ibfk_2` FOREIGN KEY (`song_id`) REFERENCES `song` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_play_history
-- ----------------------------
INSERT INTO `user_play_history` VALUES (1, 65, 121, '2026-05-16 17:32:42');
INSERT INTO `user_play_history` VALUES (2, 65, 122, '2026-05-16 17:32:55');
INSERT INTO `user_play_history` VALUES (3, 65, 120, '2026-05-16 20:10:08');
INSERT INTO `user_play_history` VALUES (4, 65, 121, '2026-05-17 16:22:20');
INSERT INTO `user_play_history` VALUES (5, 65, 122, '2026-05-17 16:22:21');
INSERT INTO `user_play_history` VALUES (6, 65, 121, '2026-05-17 16:22:26');
INSERT INTO `user_play_history` VALUES (7, 65, 121, '2026-05-17 16:39:04');
INSERT INTO `user_play_history` VALUES (8, 65, 120, '2026-05-17 16:39:06');
INSERT INTO `user_play_history` VALUES (9, 65, 138, '2026-05-17 16:39:12');
INSERT INTO `user_play_history` VALUES (10, 65, 120, '2026-05-17 16:42:46');
INSERT INTO `user_play_history` VALUES (11, 65, 121, '2026-05-17 16:42:52');
INSERT INTO `user_play_history` VALUES (12, 65, 121, '2026-05-17 16:42:52');
INSERT INTO `user_play_history` VALUES (13, 65, 120, '2026-05-18 00:03:52');

-- ----------------------------
-- Table structure for user_support
-- ----------------------------
DROP TABLE IF EXISTS `user_support`;
CREATE TABLE `user_support`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `comment_id` int NOT NULL,
  `user_id` varchar(45) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_support
-- ----------------------------
INSERT INTO `user_support` VALUES (3, 72, '61');
INSERT INTO `user_support` VALUES (5, 64, '61');
INSERT INTO `user_support` VALUES (11, 74, '65');
INSERT INTO `user_support` VALUES (17, 81, '65');
INSERT INTO `user_support` VALUES (19, 83, '1');
INSERT INTO `user_support` VALUES (21, 82, '1');
INSERT INTO `user_support` VALUES (22, 82, '65');
INSERT INTO `user_support` VALUES (23, 82, '2');

SET FOREIGN_KEY_CHECKS = 1;
