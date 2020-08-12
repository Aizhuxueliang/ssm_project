
# Vue SSM搭建一个简单的Demo前后端分离含增删改查(CRUD)、分页、批量功能
### 前言
最近想复习一下SSM，所以就搭建了这个小DEMO，轻车熟路，个人认为在只用到SSM框架的前提下，这样做是最简洁的搭建方式了。写这篇博客也是复习的一部分，也想收获些意料之外的XXXX。
### 用到的技术和开发工具
**前端**
- 开发工具：WebStorm
- 开发框架：vue + axios + elementUI
- 包管理工具: npm
- 打包工具：webpack

**后端**
- 开发工具：IDEA
- 开发框架：springMVC + spring + mybatis
- 打包工具：maven
- 数据库： MySQL
### 需求及功能
- 增删改查（CRUD）
- 分页
- 批量删除
- 前后端分离

### 后台开发环境搭建
File->New->Project…->maven->.......百度
### 后台代码
1. ssm_project/src/main/resources/applicationContext.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:aop="http://www.springframework.org/schema/aop"
        xmlns:tx="http://www.springframework.org/schema/tx"
        xmlns:p="http://www.springframework.org/schema/p"
        xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
    http://www.springframework.org/schema/context
    http://www.springframework.org/schema/context/spring-context-4.3.xsd
    http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop-4.3.xsd
    http://www.springframework.org/schema/tx
    http://www.springframework.org/schema/tx/spring-tx-4.3.xsd">

    <!-- @Component, @Repository, @Service, @Controller, @Autowired, @Resources -->
    <!-- 用注解进行开发 -->
    <context:annotation-config></context:annotation-config>
    <!-- 注解扫描包 -->
    <context:component-scan base-package="com.example">
        <!-- 这里不加载Controller的bean -->
        <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>

    <!-- 1. 数据源 -->
    <!-- 读取db.properties文件. 读取到数据库信息 -->
    <context:property-placeholder location="classpath:db.properties"/>
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="${jdbc.driver}"></property>
        <property name="url" value="${jdbc.url}"></property>
        <property name="username" value="${jdbc.username}"></property>
        <property name="password" value="${jdbc.password}"></property>
    </bean>

    <!-- 2.  创建sqlSessionFactory ==> mybatis核心配置文件的读取 -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"></property>
        <property name="configLocation" value="classpath:mybatis-config.xml"></property>
        <property name="mapperLocations" value="classpath:mapper/*.xml"/>
    </bean>

    <!-- 3.扫描mybatis的mapper接口路径 -->
    <!-- 这个bean可以把我们的mapper接口直接扫描到. 直接把接口扫描完. 注册到spring的bean中 -->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
        <!-- 会在提供的base包下寻找interface .根据interface的名字. 将首字母小写生成这个接口所对应的bean -->
        <property name="basePackage" value="com.example.mapper"></property>
    </bean>

    <!-- 4.事务处理 -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <tx:advice id="txManager" transaction-manager="transactionManager">
        <tx:attributes>
            <tx:method name="get*" read-only="true"/>
            <tx:method name="query*" read-only="true"/>
            <tx:method name="search*" read-only="true"/>
            <tx:method name="select*" read-only="true"/>
            <tx:method name="find*" read-only="true"/>
            <tx:method name="*"  isolation="DEFAULT" propagation="REQUIRED"/>
        </tx:attributes>
    </tx:advice>

    <aop:config>
        <aop:pointcut expression="execution(* com.example.service.*.*.*(..))" id="cut"/>
        <aop:advisor advice-ref="txManager" pointcut-ref="cut"/>
    </aop:config>

    <!-- 用注解处理事务
    <tx:annotation-driven transaction-manager="transactionManager"/>
 -->
</beans>
```
2. ssm_project/src/main/resources/applicationContext-mvc.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:mvc="http://www.springframework.org/schema/mvc"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:p="http://www.springframework.org/schema/p"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
    http://www.springframework.org/schema/mvc
    http://www.springframework.org/schema/mvc/spring-mvc-4.3.xsd
    http://www.springframework.org/schema/context
    http://www.springframework.org/schema/context/spring-context-4.3.xsd">

    <!-- 开启注解. 扫描 -->
    <context:annotation-config></context:annotation-config>
    <context:component-scan base-package="com.example.controller"></context:component-scan>


    <!-- 过滤掉js, jpg, png, css, 静态文件 -->
    <mvc:default-servlet-handler/>

    <!-- 开启mvc -->
    <mvc:annotation-driven />


    <!-- 地址解析器  -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/views/"></property>
        <property name="suffix" value=".jsp"></property>
    </bean>


</beans>
```
3. ssm_project/src/main/resources/mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration><!-- mybatis的核心配置  -->
    <!-- mybatis的运行配置 -->
    <settings>
        <!-- 开启二级缓存 -->
        <setting name="cacheEnabled" value="true"/>
        <setting name="lazyLoadingEnabled" value="true"/>
        <!-- 打印查询语句 -->
        <setting name="logImpl" value="STDOUT_LOGGING"/>
        <!-- 如果这个属性是true,那么你的类中, 任何一个方法被执行.都要去加载属性,
            这个时候懒加载是没有效果的.
         -->
        <setting name="aggressiveLazyLoading" value="false"/>
    </settings>

</configuration>
```
4. ssm_project/src/main/resources/db.properties

```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/ssm_example?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true
jdbc.username=root
jdbc.password=123
```
5. ssm_project/src/main/resources/log4j.properties

```properties
# Global logging configuration
log4j.rootLogger=DEBUG,Console

# Console output...

log4j.appender.Console=org.apache.log4j.ConsoleAppender
log4j.appender.Console.layout=org.apache.log4j.PatternLayout
log4j.appender.Console.layout.ConversionPattern=%d [%t] %-5p [%c] - %m%n
log4j.logger.org.apache=INFO
```
6. ssm_project/src/main/webapp/WEB-INF/web.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="2.5"
         xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
    http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">

  <!-- 读取除了mvc外的配置文件 -->
  <context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:/applicationContext.xml</param-value>
  </context-param>

  <!-- 整个web容器的动向由这个监听器进行监听. 这个监听器可以监听项目的启动. 从而直接加载核心配置文件 -->
  <listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
  </listener>

  <filter>
    <filter-name>characterEncodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
      <param-name>encoding</param-name>
      <param-value>UTF-8</param-value>
    </init-param>
    <init-param>
      <param-name>forceEncoding</param-name>
      <param-value>true</param-value>
    </init-param>
  </filter>
  <filter-mapping>
    <filter-name>characterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>


  <servlet>
    <servlet-name>springMVC</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <!-- 给出spring的路径 -->
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>classpath:applicationContext-mvc.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup><!-- 当web容器加载的时候, 初始化spring -->
  </servlet>

  <servlet-mapping>
    <servlet-name>springMVC</servlet-name>
    <url-pattern>/</url-pattern><!-- 所有 -->
  </servlet-mapping>

  <listener>
    <listener-class>org.springframework.web.context.request.RequestContextListener</listener-class>
  </listener>

</web-app>
```
7. ssm_project/pom.xml

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>ssm_project</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>war</packaging>


  <dependencies>
    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
      <version>1.7.12</version>
    </dependency>
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-core</artifactId>
      <version>1.1.1</version>
      <scope>provided</scope>
    </dependency>
    <!--实现slf4j接口并整合-->
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.1.1</version>
      <scope>provided</scope>
    </dependency>

    <!--druid==>阿里巴巴数据库连接池-->
    <dependency>
      <groupId>com.alibaba</groupId>
      <artifactId>druid</artifactId>
      <version>1.0.25</version>
    </dependency>
    <!--2.dao框架:MyBatis依赖-->
    <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
      <version>3.3.0</version>
    </dependency>
    <!--mybatis自身实现的spring整合依赖-->
    <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis-spring</artifactId>
      <version>1.3.2</version>
    </dependency>

    <!--3.Servlet web相关依赖-->
    <dependency>
      <groupId>taglibs</groupId>
      <artifactId>standard</artifactId>
      <version>1.1.2</version>
    </dependency>
    <dependency>
      <groupId>jstl</groupId>
      <artifactId>jstl</artifactId>
      <version>1.2</version>

    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.5.4</version>
    </dependency>

    <dependency>
      <groupId>javax.servlet</groupId>
      <artifactId>javax.servlet-api</artifactId>
      <version>3.1.0</version>
      <scope>provided</scope>
    </dependency>

    <!--4:spring依赖-->
    <!--1)spring核心依赖-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-core</artifactId>
      <version>4.3.14.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-beans</artifactId>
      <version>4.3.14.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
      <version>4.3.14.RELEASE</version>
    </dependency>
    <!--2)spring dao层依赖-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-jdbc</artifactId>
      <version>4.3.14.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-tx</artifactId>
      <version>4.3.14.RELEASE</version>
    </dependency>
    <!--3)springweb相关依赖-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-web</artifactId>
      <version>4.3.14.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-webmvc</artifactId>
      <version>4.3.14.RELEASE</version>
    </dependency>
    <!--4)spring test相关依赖-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-test</artifactId>
      <version>4.3.14.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>5.1.38</version>
      <scope>runtime</scope>
    </dependency>

    <!--Aop要导入的包-->
    <dependency>
      <groupId>org.aspectj</groupId>
      <artifactId>aspectjweaver</artifactId>
      <version>1.8.9</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.json/json -->
    <dependency>
      <groupId>org.json</groupId>
      <artifactId>json</artifactId>
      <version>20170516</version>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
          <source>1.8</source>
          <target>1.8</target>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```
8. ssm_project/src/main/java/com/example/pojo/User.java

```java
package com.example.pojo;

public class User {
    private String userId;//用户id
    private String cardType;//证件类型
    private String cardNo;//证件号码
    private String userName;//用户姓名
    private String userSex;//用户性别
    private String userAge;//用户年龄
    private String userRole;//用户角色

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
```

9. ssm_project/src/main/resources/mapper/UserMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.example.mapper.UserMapper" >
    <resultMap id="BaseResultMap" type="com.example.pojo.User" >
        <id property="userId" column="user_id" jdbcType="VARCHAR" /><!--用户id-->
        <result property="cardType" column="card_type" jdbcType="VARCHAR" /><!--证件类型-->
        <result property="cardNo" column="card_no" jdbcType="VARCHAR" /><!--证件号码-->
        <result property="userName" column="user_name" jdbcType="VARCHAR" /><!--用户姓名-->
        <result property="userSex" column="user_sex" jdbcType="VARCHAR" /><!--用户性别-->
        <result property="userAge" column="user_age" jdbcType="VARCHAR" /><!--用户年龄-->
        <result property="userRole" column="user_role" jdbcType="VARCHAR" /><!--用户角色-->
    </resultMap>

    <sql id="Base_Column_List" >
        user_id, card_type, card_no, user_name, user_sex, user_age, user_role
    </sql>

    <!--分页查询用户-->
    <select id="queryUserPage" resultMap="BaseResultMap" parameterType="java.lang.Integer">
        select
            <include refid="Base_Column_List" />
        from user
            order by user_id desc
			limit #{startRows,jdbcType=INTEGER},5
    </select>

    <!--分页查询用户-->
    <select id="selectUserPage" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List" />
        from user
        <where>
            <if test="userName != null and userName !=''">
                and user_name like concat('%', #{userName}, '%')
            </if>
            <if test="userSex != null and userSex !=''">
                and user_sex = #{userSex}
            </if>
        </where>
        order by user_id desc
        limit #{startRows,jdbcType=INTEGER},5
    </select>

    <!--查询用户个数-->
    <select id="getRowCount" resultType="java.lang.Integer">
		select count(*) from user
        <where>
            <if test="userName != null and userName !=''">
                and user_name like concat('%', #{userName}, '%')
            </if>
            <if test="userSex != null and userSex !=''">
                and user_sex = #{userSex}
            </if>
        </where>
	</select>

    <!--添加用户信息-->
    <insert id="createUser" parameterType="com.example.pojo.User" >
        insert into User(<include refid="Base_Column_List" />)
		values(#{userId}, #{cardType}, #{cardNo}, #{userName}, #{userSex}, #{userAge}, #{userRole})
    </insert>

    <!--根据 user_id 删除用户-->
    <delete id="deleteUserById" parameterType="String" >
        delete from user
        where user_id = #{userId,jdbcType=VARCHAR}
    </delete>


    <!--根据 user_id 批量删除用户-->
    <delete id="deleteUserByIdList" parameterType="java.util.List">
        delete from user where user_id in <foreach collection="list" item="item" index="index" open="(" close=")" separator=",">#{item,jdbcType=VARCHAR}</foreach>
    </delete>

    <!--根据 user_id 更新用户信息-->
    <update id="updateUserById" parameterType="com.example.pojo.User" >
        update user
        <set >
            <if test="cardNo != null" >
                card_no = #{cardNo,jdbcType=VARCHAR},
            </if>
            <if test="cardType != null" >
                card_type = #{cardType,jdbcType=VARCHAR},
            </if>
            <if test="userName != null" >
                user_name = #{userName,jdbcType=VARCHAR},
            </if>
            <if test="userSex != null" >
                user_sex = #{userSex,jdbcType=VARCHAR},
            </if>
            <if test="userAge != null" >
                user_age = #{userAge,jdbcType=VARCHAR},
            </if>
            <if test="userRole != null" >
                user_role = #{userRole,jdbcType=VARCHAR}
            </if>
        </set>
        where 1 = 1
        and user_id = #{userId,jdbcType=VARCHAR}
    </update>

</mapper>
```
10. ssm_project/src/main/java/com/example/mapper/UserMapper.java

```java
package com.example.mapper;

import java.util.List;

import com.example.pojo.User;
import org.apache.ibatis.annotations.Param;


public interface UserMapper {

    /**
     * 分页查询 User
     * @param startRows 起始页
     * @return List<User>
     */
    List<User> queryUserPage(Integer startRows);

    /**
     * 分页查询 User 带条件
     * @param userName
     * @param userSex
     * @param startRows
     * @return
     */
    List<User> selectUserPage(@Param("userName")String userName, @Param("userSex")String userSex, @Param("startRows")Integer startRows);

    /**
     * 查询 User 个数
     * @param userName
     * @param userSex
     * @return
     */
    Integer getRowCount(@Param("userName")String userName, @Param("userSex")String userSex);

    /**
     * 添加 User
     * @param user
     * @return 返回码
     */
    Integer createUser(User user);

    /**
     * 根据 userId 删除用户
     * @return 返回码
     */
    Integer deleteUserById(String userId);

    /**
     * 根据 userId 批量删除用户
     * @param userIds
     * @return
     */
    Integer deleteUserByIdList(@Param("list") List userIds);

    /**
     * 根据 userId 更新用户
     * @return 返回码
     */
    Integer updateUserById(User user);

}
```
11. ssm_project/src/main/java/com/example/service/UserService.java

```java
package com.example.service;

import java.util.List;

import com.example.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserService {

    /**
     * 分页查询 User
     * @param startRows 起始页
     * @return List<User>
     */
    List<User> queryUserPage(Integer startRows);

    /**
     * 分页查询 User 带条件
     * @param userName
     * @param userSex
     * @param startRows
     * @return
     */
    List<User> selectUserPage(@Param("userName")String userName, @Param("userSex")String userSex, @Param("startRows")Integer startRows);

    /**
     * 查询 User 个数
     * @param userName
     * @param userSex
     * @return
     */
    Integer getRowCount(@Param("userName")String userName, @Param("userSex")String userSex);

    /**
     * 添加 User
     * @param user
     * @return 返回码
     */
    Integer createUser(User user);

    /**
     * 根据 userId 删除用户
     * @return 返回码
     */
    Integer deleteUserById(String userId);

    /**
     * 根据 userId 批量删除用户
     * @param userIds
     * @return
     */
    Integer deleteUserByIdList(@Param("list") List userIds);

    /**
     * 根据 userId 更新用户
     * @return 返回码
     */
    Integer updateUserById(User user);

}
```
12. ssm_project/src/main/java/com/example/service/impl/UserServiceImpl.java

```java
package com.example.service.impl;

import java.util.List;

import com.example.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mapper.UserMapper;
import com.example.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> queryUserPage(Integer startRows) {
        return userMapper.queryUserPage(startRows);
    }

    @Override
    public List<User> selectUserPage(String userName, String userSex, Integer startRows) {
        return userMapper.selectUserPage(userName, userSex, startRows);
    }

    @Override
    public Integer getRowCount(String userName, String userSex) {
        return userMapper.getRowCount(userName, userSex);
    }

    @Override
    public Integer createUser(User user) {
        return userMapper.createUser(user);
    }

    @Override
    public Integer deleteUserById(String userId) {
        return userMapper.deleteUserById(userId);
    }

    @Override
    public Integer deleteUserByIdList(@Param("list") List userIds) {
        return userMapper.deleteUserByIdList(userIds);
    }

    @Override
    public Integer updateUserById(User user) {
        return userMapper.updateUserById(user);
    }


}
```
13. ssm_project/src/main/java/com/example/controller/UserController.java

```java
package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.service.UserService;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/queryUserPage")
    @ResponseBody
    public List<User> queryUserPage(Integer page) {
        int pageNow = page == null ? 1 : page;
        int pageSize = 5;
        int startRows = pageSize*(pageNow-1);
        return userService.queryUserPage(startRows);
    }

    @RequestMapping("/selectUserPage")
    @ResponseBody
    public List<User> selectUserPage(String userName, String userSex, Integer page) {
        int pageNow = page == null ? 1 : page;
        int pageSize = 5;
        int startRows = pageSize*(pageNow-1);
        return userService.selectUserPage(userName, userSex, startRows);
    }

    @RequestMapping("/getRowCount")
    @ResponseBody
    public Integer getRowCount(String userName, String userSex) {
        return userService.getRowCount(userName, userSex);
    }

    @RequestMapping("/createUser")
    @ResponseBody
    public Integer createUser(User user) {
        Random random = new Random();
        Integer number = random.nextInt(9000) + 1000;
        user.setUserId(System.currentTimeMillis() + String.valueOf(number));
        return userService.createUser(user);
    }

    @RequestMapping("/deleteUserById")
    @ResponseBody
    public Integer deleteUserById(String userId) {
        return userService.deleteUserById(userId);
    }

    @RequestMapping(value = "/deleteUserByIdList")
    @ResponseBody
    public Integer deleteUserByIdList(String userIdList) {
        String userIdListSub = userIdList.substring(0, userIdList.length()-1);
//        String[] userIds = userIdList.split(",");

        List userIds = new ArrayList();
        for (String userIdStr: userIdListSub.split(",")){
            userIds.add(userIdStr.trim());
        }
        return userService.deleteUserByIdList(userIds);
    }

    @RequestMapping("/updateUserById")
    @ResponseBody
    public Integer updateUserById(User user) {
        return userService.updateUserById(user);
    }


}
```
14. SQL

```sql
-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `card_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `card_no` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_sex` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_age` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_role` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('15968162087363060', '身份证', '4264465547656467', '过突然', '男', '30', '办事人员和有关人员');
INSERT INTO `user` VALUES ('15968162346981977', '护照', '432532654364654', '规划图', '男', '29', '不便分类的其他从业人员');
INSERT INTO `user` VALUES ('15968162893439470', '身份证', '4354324534532', '具体办1', '男', '31', '农、林、牧、渔、水利业生产人员');
INSERT INTO `user` VALUES ('15968163245457143', '身份证', '43564546576687', '回各家', '男', '34', '未知');
INSERT INTO `user` VALUES ('15968163514764733', '军官证', '7657868', '缺口v4', '女', '23', '不便分类的其他从业人员');
INSERT INTO `user` VALUES ('15968165113694372', '台湾往来大陆通行证', '343214321412433214', '遗体ioy', '女', '48', '生产、运输设备操作人员及有关人员');
INSERT INTO `user` VALUES ('15968165371931786', '港澳居民通行证', '65765887989090909', '垂直发射的', '女', '35', '不便分类的其他从业人员');
INSERT INTO `user` VALUES ('15968941217553030', '身份证', '34354657665768768', '撒撒到', '男', '22', '军人');
INSERT INTO `user` VALUES ('15968943937844616', '身份证', '4454534436565756', '出手大', '女', '31', '不便分类的其他从业人员');
INSERT INTO `user` VALUES ('15968944123869023', '护照', '43225465457657', 'VCD法国', '女', '39', '农、林、牧、渔、水利业生产人员');
INSERT INTO `user` VALUES ('15968953962316864', '身份证', '342354325', '房东是个大帅哥', '女', '33', '商业、服务业人员');
INSERT INTO `user` VALUES ('15968954638794962', '身份证', '343343554654', '撒撒旦', '女', '29', '生产、运输设备操作人员及有关人员');

SET FOREIGN_KEY_CHECKS = 1;
```
**后台到这里就写完了，结构如下：**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200810225636464.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ppYW55dXd1eWk=,size_16,color_FFFFFF,t_70)

### 前台开发环境搭建
![SSM_list](https://img-blog.csdnimg.cn/20200810223437952.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ppYW55dXd1eWk=,size_16,color_FFFFFF,t_70)

### 前台代码

15. vue_project/src/App.vue

```html
<template>
    <el-row type="flex" justify="center">
        <el-col :xs="24" :sm="22" :md="20" :lg="20" :xl="18">
            <router-view/>
        </el-col>
    </el-row>
</template>

<script>
    export default {
        name: 'App'
    }
</script>

<style>
    #app {
        font-family: 'Avenir', Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
        margin-top: 60px;
    }

    .el-table th.gutter {
        display: table-cell !important;
    }
</style>
```
16. vue_project/src/main.js

```js
// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import App from './App';
import router from './router';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import qs from 'qs';
import axios from "axios";


Vue.config.productionTip = false;
Vue.use(ElementUI);
Vue.prototype.axios = axios;
Vue.prototype.qs = qs;

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
```
17. vue_project/src/components/UserHome.vue

```html
<template>
    <div>
        <el-form :inline="true" class="demo-form-inline">
            <el-form-item>
                <el-input
                        v-model="search1"
                        size="mini"
                        placeholder="输入姓名查询" v-on:input="handleSearch()">
                </el-input>
            </el-form-item>
            <el-form-item>
                <el-select size="mini" v-model="search2" v-on:change="handleSearch()">
                    <el-option label="请选择性别" value=""></el-option>
                    <el-option label="男" value="男"></el-option>
                    <el-option label="女" value="女"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button
                        class="el-icon-circle-plus-outline"
                        type="text"
                        @click="dialogAdd = true">添加
                </el-button>
            </el-form-item>
            <el-form-item>
                <el-button
                        class="el-icon-delete"
                        type="text"
                        @click="handleDeleteList()">删除
                </el-button>
            </el-form-item>
        </el-form>

        <el-table
                ref="multipleTable"
                :data="tableData"
                border
                highlight-current-row
                style="width: 100%"
                @selection-change="handleSelectionDelete">
            <el-table-column
                    type="selection"
                    width="55">
            </el-table-column>
            <el-table-column
                    label="用户编号">
                <template slot-scope="scope">
                    <span>{{ scope.row.userId }}</span>
                </template>
            </el-table-column>
            <el-table-column
                    label="证件类型">
                <template slot-scope="scope">
                    <span>{{ scope.row.cardType }}</span>
                </template>
            </el-table-column>
            <el-table-column
                    label="证件号码">
                <template slot-scope="scope">
                    <span>{{ scope.row.cardNo }}</span>
                </template>
            </el-table-column>
            <el-table-column
                    label="用户姓名">
                <template slot-scope="scope">
                    <el-popover trigger="hover" placement="right">
                        <p>证件类型: {{ scope.row.cardType }}</p>
                        <p>证件号码: {{ scope.row.cardNo }}</p>
                        <p>用户姓名：{{ scope.row.userName }}</p>
                        <p>用户性别: {{ scope.row.userSex }}</p>
                        <p>用户年龄: {{ scope.row.userAge }}</p>
                        <p>用户角色：{{ scope.row.userRole }}</p>
                        <div slot="reference" class="name-wrapper">
                            <el-button type="text">{{ scope.row.userName }}</el-button>
                        </div>
                    </el-popover>
                </template>
            </el-table-column>
            <el-table-column
                    label="用户性别">
                <template slot-scope="scope">
                    <span>{{ scope.row.userSex }}</span>
                </template>
            </el-table-column>
            <el-table-column
                    label="用户年龄">
                <template slot-scope="scope">
                    <span>{{ scope.row.userAge }}</span>
                </template>
            </el-table-column>
            <el-table-column
                    label="用户角色">
                <template slot-scope="scope">
                    <span>{{ scope.row.userRole }}</span>
                </template>
            </el-table-column>
            <el-table-column
                    label="操作">
                <template slot-scope="scope">
                    <el-button
                            type="text"
                            icon="el-icon-edit"
                            @click="handleEdit(scope.$index, scope.row)">编辑
                    </el-button>
                    <el-button
                            type="text"
                            icon="el-icon-delete"
                            @click="handleDelete(scope.$index, scope.row)">删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="80px" class="demo-ruleForm" size="medium">
            <el-dialog
                    title="添加"
                    :append-to-body='true'
                    :visible.sync="dialogAdd"
                    :before-close="handleClose">
                <el-form-item label="证件类型">
                    <el-select v-model="ruleForm.cardType" placeholder="请选择证件类型" prop="cardType">
                        <el-option label="身份证" value="身份证"></el-option>
                        <el-option label="军官证" value="军官证"></el-option>
                        <el-option label="护照" value="护照"></el-option>
                        <el-option label="港澳居民通行证" value="港澳居民通行证"></el-option>
                        <el-option label="台湾往来大陆通行证" value="台湾往来大陆通行证"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="证件号码">
                    <el-input v-model="ruleForm.cardNo"></el-input>
                </el-form-item>
                <el-form-item label="用户姓名">
                    <el-input v-model="ruleForm.userName"></el-input>
                </el-form-item>
                <el-form-item label="用户性别">
                    <el-radio-group v-model="ruleForm.userSex">
                        <el-radio label="男"></el-radio>
                        <el-radio label="女"></el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="用户年龄">
                    <el-slider v-model="ruleForm.userAge" show-input></el-slider>
                </el-form-item>
                <el-form-item label="用户角色">
                    <el-select v-model="ruleForm.userRole" placeholder="请选择用户角色" prop="userRole">
                        <el-option label="国家机关、党群组织、企业、事业单位负责人" value="国家机关、党群组织、企业、事业单位负责人"></el-option>
                        <el-option label="专业技术人员" value="专业技术人员"></el-option>
                        <el-option label="办事人员和有关人员" value="办事人员和有关人员"></el-option>
                        <el-option label="商业、服务业人员" value="商业、服务业人员"></el-option>
                        <el-option label="农、林、牧、渔、水利业生产人员" value="农、林、牧、渔、水利业生产人员"></el-option>
                        <el-option label="生产、运输设备操作人员及有关人员" value="生产、运输设备操作人员及有关人员"></el-option>
                        <el-option label="军人" value="军人"></el-option>
                        <el-option label="不便分类的其他从业人员" value="不便分类的其他从业人员"></el-option>
                        <el-option label="未知" value="未知"></el-option>
                    </el-select>
                </el-form-item>

                <span slot="footer" class="dialog-footer">
            <el-button @click="emptyUserData()" size="medium">取 消</el-button>
            <el-button @click="addUser('ruleForm')" type="primary" size="medium">确 定</el-button>
          </span>
            </el-dialog>
        </el-form>

        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="70px" class="demo-ruleForm" size="medium">
            <el-dialog
                    title="编辑"
                    :append-to-body='true'
                    :visible.sync="dialogUpdate"
                    :before-close="handleClose">
                <el-form-item label="证件类型">
                    <el-select v-model="ruleForm.cardType" placeholder="请选择证件类型" prop="cardType">
                        <el-option label="身份证" value="身份证"></el-option>
                        <el-option label="军官证" value="军官证"></el-option>
                        <el-option label="护照" value="护照"></el-option>
                        <el-option label="港澳居民通行证" value="港澳居民通行证"></el-option>
                        <el-option label="台湾往来大陆通行证" value="台湾往来大陆通行证"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="证件号码">
                    <el-input v-model="ruleForm.cardNo"></el-input>
                </el-form-item>
                <el-form-item label="用户姓名">
                    <el-input v-model="ruleForm.userName"></el-input>
                </el-form-item>
                <el-form-item label="用户性别">
                    <el-radio-group v-model="ruleForm.userSex">
                        <el-radio label="男"></el-radio>
                        <el-radio label="女"></el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="用户年龄">
                    <el-slider v-model="ruleForm.userAge" show-input></el-slider>
                </el-form-item>
                <el-form-item label="用户角色">
                    <el-select v-model="ruleForm.userRole" placeholder="请选择用户角色" prop="userRole">
                        <el-option label="国家机关、党群组织、企业、事业单位负责人" value="国家机关、党群组织、企业、事业单位负责人"></el-option>
                        <el-option label="专业技术人员" value="专业技术人员"></el-option>
                        <el-option label="办事人员和有关人员" value="办事人员和有关人员"></el-option>
                        <el-option label="商业、服务业人员" value="商业、服务业人员"></el-option>
                        <el-option label="农、林、牧、渔、水利业生产人员" value="农、林、牧、渔、水利业生产人员"></el-option>
                        <el-option label="生产、运输设备操作人员及有关人员" value="生产、运输设备操作人员及有关人员"></el-option>
                        <el-option label="军人" value="军人"></el-option>
                        <el-option label="不便分类的其他从业人员" value="不便分类的其他从业人员"></el-option>
                        <el-option label="未知" value="未知"></el-option>
                    </el-select>
                </el-form-item>

                <span slot="footer" class="dialog-footer">
            <el-button @click="emptyUserData()" size="medium">取 消</el-button>
            <el-button @click="updateUser()" type="primary" size="medium">确 定</el-button>
          </span>
            </el-dialog>
        </el-form>
        <br>

        <el-pagination
                background
                :disabled="disablePage"
                :current-page.sync="currentPage"
                small
                layout="prev, pager, next"
                :page-size="pageSize"
                :total="total"
                @current-change="handlePageChange">
        </el-pagination>
    </div>
</template>

<script>
    export default {
        data() {
            return {
                ruleForm: {
                    userId: null,//用户id
                    cardType: null,//证件类型
                    cardNo: null,//证件号码
                    userName: null,//用户姓名
                    userSex: null,//用户性别
                    userAge: 25,//用户年龄
                    userRole: null//用户角色
                },
                rules: {},
                tableData: [],
                search1: '',
                search2: '',
                dialogAdd: false,
                dialogUpdate: false,
                pageSize: 5,
                currentPage: 1,
                total: 0,
                disablePage: false,
                multipleSelection: []
            };
        },

        created() {
            let postData1 = this.qs.stringify({
                page: this.currentPage,
                userName: this.search1,
                userSex: this.search2
            });
            this.axios({
                method: 'post',
                url: '/ssm_project_war_exploded/user/selectUserPage',
                data: postData1
            }).then(response => {
                this.tableData = response.data;
            }).catch(error => {
                console.log(error);
            });

            let postData = this.qs.stringify({
                userName: this.search1,
                userSex: this.search2
            });
            this.axios({
                method: 'post',
                url: '/ssm_project_war_exploded/user/getRowCount',
                data: postData
            }).then(response => {
                this.total = response.data;
            }).catch(error => {
                console.log(error);
            });

        },

        methods: {
            /**
             * 分页
             */
            handlePageChange() {
                //console.log(`当前页: ${this.currentPage}`);
                let postData = this.qs.stringify({
                    page: this.currentPage,
                    userName: this.search1,
                    userSex: this.search2
                });
                this.axios({
                    method: 'post',
                    url: '/ssm_project_war_exploded/user/selectUserPage',
                    data: postData
                }).then(response => {
                    this.tableData = response.data;
                }).catch(error => {
                    console.log(error);
                });
            },

            /**
             * 添加用户
             */
            addUser() {
                if (this.ruleForm.cardType == null || this.ruleForm.cardNo == null || this.ruleForm.userName == null || this.ruleForm.userSex == null || this.ruleForm.userRole == null) {
                    this.$alert('用户信息不完整请检查', '温馨提示', {
                        confirmButtonText: '确定'
                    });
                    return;
                }
                let postData = this.qs.stringify({
                    cardType: this.ruleForm.cardType,//证件类型
                    cardNo: this.ruleForm.cardNo,//证件号码
                    userName: this.ruleForm.userName,//用户姓名
                    userSex: this.ruleForm.userSex,//用户性别
                    userAge: this.ruleForm.userAge,//用户年龄
                    userRole: this.ruleForm.userRole,//用户角色
                });
                this.axios({
                    method: 'post',
                    url: '/ssm_project_war_exploded/user/createUser',
                    data: postData
                }).then(response => {
                    this.handlePageChange();
                    this.getRowCount();
                    this.$message({
                        type: 'success',
                        message: '已添加!'
                    });
                    this.emptyUserData();
                    //console.log(response);
                }).catch(error => {
                    console.log(error);
                });
            },

            /**
             * 统计用户个数
             */
            getRowCount() {
                let postData = this.qs.stringify({
                    userName: this.search1,
                    userSex: this.search2
                });
                this.axios({
                    method: 'post',
                    url: '/ssm_project_war_exploded/user/getRowCount',
                    data: postData
                }).then(response => {
                    this.total = response.data;
                }).catch(error => {
                    console.log(error);
                });
            },

            handleSearch() {
                this.handlePageChange();
                this.getRowCount();
            },

            handleEdit(index, row) {
                this.dialogUpdate = true;
                row.userAge = Number(row.userAge);
                this.ruleForm = Object.assign({}, row, index); //这句是关键！！！
            },

            handleClose(done) {
                this.$confirm('确认关闭？')
                    .then(_ => {
                        done();
                        this.emptyUserData();
                    })
                    .catch(_ => {
                    });
            },

            /**
             * 清空绑定数据
             */
            emptyUserData() {
                this.dialogAdd = false;
                this.dialogUpdate = false;
                this.ruleForm = {
                    userId: null,//用户id
                    cardType: null,//证件类型
                    cardNo: null,//证件号码
                    userName: null,//用户姓名
                    userSex: null,//用户性别
                    userAge: 25,//用户年龄
                    userRole: null//用户角色
                };
            },

            /**
             * 根据 userId 删除用户
             * @param index
             * @param row
             */
            handleDelete(index, row) {
                //console.log(index, row);
                this.$confirm('删除操作, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let postData = this.qs.stringify({
                        userId: row.userId,
                    });
                    this.axios({
                        method: 'post',
                        url: '/ssm_project_war_exploded/user/deleteUserById',
                        data: postData
                    }).then(response => {
                        this.getRowCount();
                        if (this.total % 5 == 1 && this.currentPage >= 1) {
                            if (this.total / 5 < this.currentPage) {
                                this.currentPage = this.currentPage - 1;
                            }
                        }
                        this.handlePageChange();

                        this.$message({
                            type: 'success',
                            message: '删除成功!'
                        });
                        //console.log(response);
                    }).catch(error => {
                        console.log(error);
                    });

                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            },

            handleSelectionDelete(val) {
                this.multipleSelection = val;
            },

            /**
             * 根据 userId 批量删除用户
             */
            handleDeleteList() {
                let userIds = "";
                this.multipleSelection.forEach(item => {
                    userIds += item.userId + ',';
                })
                console.log(userIds);
                // let userIds= this.multipleSelection.map(item => item.userId).join()

                this.$confirm('删除操作, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let postData = this.qs.stringify({
                        userIdList: userIds
                    });
                    console.log(postData);
                    this.axios({
                        method: 'post',
                        url: '/ssm_project_war_exploded/user/deleteUserByIdList',
                        data: postData
                    }).then(response => {
                        this.getRowCount();
                        if (this.total % 5 == 1 && this.currentPage >= 1) {
                            if (this.total / 5 < this.currentPage) {
                                this.currentPage = this.currentPage - 1;
                            }
                        }
                        this.handlePageChange();

                        this.$message({
                            type: 'success',
                            message: '删除成功!'
                        });
                        //console.log(response);
                    }).catch(error => {
                        console.log(error);
                    });

                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            },


            updateUser() {
                if (this.ruleForm.cardType == null || this.ruleForm.cardNo == null || this.ruleForm.userName == null || this.ruleForm.userSex == null || this.ruleForm.userRole == null) {
                    this.$alert('用户信息不完整请检查', '温馨提示', {
                        confirmButtonText: '确定'
                    });
                    return;
                }
                let postData = this.qs.stringify({
                    userId: this.ruleForm.userId,//用户 Id
                    cardType: this.ruleForm.cardType,//证件类型
                    cardNo: this.ruleForm.cardNo,//证件号码
                    userName: this.ruleForm.userName,//用户姓名
                    userSex: this.ruleForm.userSex,//用户性别
                    userAge: this.ruleForm.userAge,//用户年龄
                    userRole: this.ruleForm.userRole,//用户角色
                });
                this.axios({
                    method: 'post',
                    url: '/ssm_project_war_exploded/user/updateUserById',
                    data: postData
                }).then(response => {
                    this.handlePageChange();
                    this.getRowCount();
                    this.$message({
                        type: 'success',
                        message: '已编辑!'
                    });
                    this.emptyUserData();
                    //console.log(response);
                }).catch(error => {
                    console.log(error);
                });
            }
        },

    }
</script>
<style scoped>

</style>
```
18. vue_project/src/router/index.js

```js
import Vue from 'vue';
import Router from 'vue-router';
import HelloWorld from '@/components/HelloWorld'
import UserHome from '@/components/UserHome'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/HelloWorld',
      name: 'HelloWorld',
      component: HelloWorld
    },
    {
      path: '/',
      name: 'UserHome',
      component: UserHome
    }
  ]
})
```
**前台结构如下：**
![前台结构](https://img-blog.csdnimg.cn/20200810225426694.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ppYW55dXd1eWk=,size_16,color_FFFFFF,t_70)

### 前后台整合
vue_project/config/index.js

```js
dev: {

    // Paths
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
    proxyTable: {
      '/': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/': ''
        }
      },
      '/ws/*': {
        target: 'ws://127.0.0.1:8080',
        ws: true
      }
    },



    // Various Dev Server settings
    host: 'localhost', // can be overwritten by process.env.HOST
    port: 8082, // can be overwritten by process.env.PORT, if port is in use, a free one will be determined
    autoOpenBrowser: false,
    errorOverlay: true,
    notifyOnErrors: true,
    poll: false, // https://webpack.js.org/configuration/dev-server/#devserver-watchoptions-

    // Use Eslint Loader?
    // If true, your code will be linted during bundling and
    // linting errors and warnings will be shown in the console.
    useEslint: true,
    // If true, eslint errors and warnings will also be shown in the error overlay
    // in the browser.
    showEslintErrorsInOverlay: false,

    /**
     * Source Maps
     */

    // https://webpack.js.org/configuration/devtool/#development
    devtool: 'cheap-module-eval-source-map',

    // If you have problems debugging vue-files in devtools,
    // set this to false - it *may* help
    // https://vue-loader.vuejs.org/en/options.html#cachebusting
    cacheBusting: true,

    cssSourceMap: true
  },
```
### 实现效果
**视频**
[video(video-Wq1iRAuM-1597070693731)(type-bilibili)(url-https://player.bilibili.com/player.html?aid=499139068)(image-https://ss.csdn.net/p?http://i1.hdslb.com/bfs/archive/2de6a570a4bd4399af72292c671a454784aa0c35.jpg)(title-Vue SSM搭建一个简单的Demo含增删改查(CRUD)、分页、批量功能)]
**图片**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200810224334554.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ppYW55dXd1eWk=,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200810224334582.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ppYW55dXd1eWk=,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200810224334558.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ppYW55dXd1eWk=,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200810224334563.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ppYW55dXd1eWk=,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200810224334553.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ppYW55dXd1eWk=,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200810224334550.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ppYW55dXd1eWk=,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200810224334550.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ppYW55dXd1eWk=,size_16,color_FFFFFF,t_70)

### 值得扩展的功能或方向
1. 用户登录拦截功能：前端需要封装一下axios，后端需引入shiro；
2. 增加动态菜单，根据用户角色；
3. excel文件批量导入导出用户数据功能：还没想好用啥插件；
4. 引入富文本编辑器：主要为了再建一张表让业务复杂些；
5. 去大厂的开放平台找点牛逼的API引到project里；
### 结语
有问题请留言

CSDN：
博客园：[https://blog.csdn.net/jianyuwuyi/article/details/107924066](https://blog.csdn.net/jianyuwuyi/article/details/107924066)
GitHub：[https://github.com/Aizhuxueliang/ssm_project](https://github.com/Aizhuxueliang/ssm_project)

