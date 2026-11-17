package cn.oncloud.common.utils;

public class Constant {

    /**
     * 超级管理员用户ID
     */
    public static final long SUPER_ADMIN = 1L;

    /**
     * 角色名称
     */
    public static final String ROLE_SUPER_ADMIN = "超级管理员";
    public static final String ROLE_ADMIN = "管理员";
    public static final String ROLE_USER = "用户";

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

        private final int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
