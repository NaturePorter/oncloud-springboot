package cn.oncloud.aspect;

/**
 * @author 余弘洋
 */

public enum RetentionPolicy {
    /**
     *被编译器忽略
     */
    SOURCE,

    /**
     * 注解将会被保留在Class文件中，但在运行时并不会被VM保留。这是默认行为，所有没有      ＊ 用Retention注解的注解，都会采用这种策略。
     */
    CLASS,

    /**
     * 保留至运行时。所以我们可以通过反射去获取注解信息
     */
    RUNTIME

}
