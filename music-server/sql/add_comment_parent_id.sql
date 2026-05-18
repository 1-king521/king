-- 评论楼中楼：根评论 parent_id 为 NULL，回复指向被回复评论的 id
ALTER TABLE `comment` ADD COLUMN `parent_id` INT NULL DEFAULT NULL COMMENT '被回复的评论id' AFTER `like_count`;
