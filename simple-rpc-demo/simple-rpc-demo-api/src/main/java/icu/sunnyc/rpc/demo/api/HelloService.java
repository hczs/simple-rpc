package icu.sunnyc.rpc.demo.api;

/**
 * 示例接口
 * @author: houcheng
 * @date: 2022/5/31 16:26:10
 */
public interface HelloService {

    /**
     * 示例方法
     * @param name 字符串参数
     * @return String
     */
    String hello(String name);

    /**
     * 返回这个学生一年后的样子
     * @param student 学生对象
     * @return 一年后的学生对象
     */
    Student oneYearLater(Student student);

}
