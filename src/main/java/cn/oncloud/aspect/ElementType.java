package cn.oncloud.aspect;

/**
 * @author 余弘洋
 */

public enum ElementType {
    /** 类, 接口 (包括注释类型), 或 枚举 声明 */
    TYPE,

    /** 字段声明（包括枚举常量） */
    FIELD,

    /** 方法声明(Method declaration) */
    METHOD,

    /** 正式的参数声明 */
    PARAMETER,

    /** 构造函数声明 */
    CONSTRUCTOR,

    /** 局部变量声明 */
    LOCAL_VARIABLE,

    /** 注释类型声明 */
    ANNOTATION_TYPE,

    /** 包声明 */
    PACKAGE,

    /**
     * 类型参数声明
     *
     * @since 1.8
     */
    TYPE_PARAMETER,

    /**
     * 使用的类型
     *
     * @since 1.8
     */
    TYPE_USE
}
