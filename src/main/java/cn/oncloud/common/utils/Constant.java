package cn.oncloud.common.utils;

public class Constant {
    /**
     * 超级管理员
     */
    public static final int SUPER_ADMIN = 1;

    /**
     * 用户是否停用标志符
     */
    public static final String ISDELETE = "1";

    /**
     * 菜单类型
     */
    public enum MenuType {
        /**
         * 目录
         */
        CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
