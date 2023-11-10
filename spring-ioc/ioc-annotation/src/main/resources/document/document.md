## 注解配置

​		基本Bean注解，主要是使用注解的方式替代原有xml的  标签及其标签属性的配置

### 实列化 Bean

**@Component**

​		使用@Component 注解替代 <bean> 标签，被该注解标识的类，会在指定扫描范围内被Spring加载并实例化。

​		可以通过 @Component 注解的 value 属性指定当前 Bean 实例的 beanName ，也可以省略不写，不写的情况下为当前类名首字母小写

```java
//获取方式：applicationContext.getBean("userDao");
@Component("userDao")
public class UserDaoImpl implements UserDao {
}
//获取方式：applicationContext.getBean("userDaoImpl");
@Component
public class UserDaoImpl implements UserDao {
}

```

​		为了每层Bean标识的注解语义化更加明确，@Component又衍生出如下三个注解：

```
@Repository ：在Dao层类上使用
@Service ：在Service层类上使用
@Controller ： 在Web层类上使用
```

配置包扫描

​		使用注解对需要被Spring实例化的Bean进行标注，但是需要告诉Spring去哪找这些Bean，要配置组件扫描路径

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/xmlSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="
        http://www.springframework.org/schema/beans 
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context 
        http://www.springframework.org/schema/context/spring-context.xsd
">
    <!-- 告知Spring框架去itheima包及其子包下去扫描使用了注解的类 -->
    <context:component-scan base-package="com.liuhao"/>
</beans>

```

​		@Component 就单纯一个 value 属性，Spring 通过注解方式去配置的之前标签中的那些属性

```xml
<bean id="" name="" class="" scope="" lazy-init="" init-method="" destroy-method="" 
abstract="" autowire="" factory-bean="" factory-method=""></bean>
```

​		对应注解：

```xml
<bean scope=""> = @Scope 
	在类上或使用了@Bean标注的方法上，标注Bean的作用范围，取值为singleton或prototype
    
<bean lazy-init=""> = @Lazy 
    在类上或使用了@Bean标注的方法上，标注Bean是否延迟加载，取值为true和false
    
<bean init-method=""> = @PostConstruct
    在方法上使用，标注Bean的实例化后执行的方法
    
<bean destroy-method=""> = @PreDestroy
    在方法上使用，标注Bean的销毁前执行方法

```

### bean 的属性注入

​		Bean依赖注入的注解，主要是使用注解的方式替代xml的  标签完成属性的注入操作

Spring主要提供如下注解，用于在Bean内部进行属性注入的：

```
@Value ：使用在字段或方法上，用于注入普通数据
@Autowired ：使用在字段或方法上，用于根据类型（byType）注入引用数据
@Qualifier ：使用在字段或方法上，结合@Autowired，根据名称注入
@Resource ：使用在字段或方法上，根据类型或名称进行注入
```

**@Value**

​		注入普通数据类型属性

```java
@Value("haohao")
private String username;

@Value("haohao")
public void setUsername(String username){
	System.out.println(username);
}

```

​		注入properties文件中的属性

```java
@Value("${jdbc.username}")
private String username;

@Value("${jdbc.username}")
public void setUsername(String username){
	System.out.println(username);
}

```

​		加载properties文件

```xml
<context:property-placeholder location="classpath:jdbc.properties"/>
```

**@Autowired**

```java
//使用在属性上直接注入
@Autowired
private UserDao userDao;
//使用在方法上直接注入
@Autowired
public void setUserDao(UserDao userDao){
	System.out.println(userDao);
}
```

​		当容器中同一类型的Bean实例有多个时，会尝试自动根据名字进行匹配：

```java
//匹配当前Bean
@Repository("userDao")
public class UserDaoImpl implements UserDao{}
@Repository("userDao2")
public class UserDaoImpl2 implements UserDao{}

```

​		当容器中同一类型的Bean实例有多个时，且名字与被注入Bean名称不匹配时会报错

​		@Qualifier配合@Autowired可以完成根据名称注入Bean实例，使用@Qualifier指定名称

```java
@Autowired
@Qualifier("userDao2")
private UserDao userDao;

@Autowired
@Qualifier("userDao2")
public void setUserDao(UserDao userDao){
	System.out.println(userDao);
}

```

**@Resource**

​		既可以根据类型注入，也可以根据名称注入，无参就是根据类型注入，有参数就是根据名称注入

```java
@Resource
private UserDao userDao;

@Resource(name = "userDao2")
public void setUserDao(UserDao userDao){
	System.out.println(userDao);
}
```

### 配置非自定义 Bean

​		非自定义 Bean 不能像自定义 Bean 一样使用 @Component 进行管理，非自定义 Bean 要通过工厂的方式进行实例化，使用 @Bean 标注方法即可，@Bean 的属性为 beanName，如不指定为当前工厂方法名称，工厂方法所在类必须要被Spring管理

```java
//将方法返回值Bean实例以@Bean注解指定的名称存储到Spring容器中
@Bean("dataSource")
public DataSource dataSource(){
    DruidDataSource dataSource = new DruidDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://localhost:3306/mybatis");
    dataSource.setUsername("root");
    dataSource.setPassword("root");
    return dataSource;
}
```

​		@Bean 工厂方法需要参数的话，有如下几种注入方式：

```
1、使用 @Autowired 根据类型自动进行Bean的匹配，@Autowired可以省略 ；
2、使用 @Qualifier 根据名称进行Bean的匹配；
3、使用 @Value 根据名称进行普通数据类型匹配。
```

代码

```java
@Bean
@Autowired //根据类型匹配参数
public Object objectDemo01(UserDao userDao){
    System.out.println(userDao);
    return new Object();
}

@Bean
public Object objectDemo02(@Qualifier("userDao") UserDao userDao,@Value("${jdbc.username}") String 			username){
    System.out.println(userDao);
    System.out.println(username);
    return new Object();
}

```

### Bean 配置类的注解开发

​		@Component 等注解替代了<bean>标签，但是像 <import> 等非 标签怎样去使用注解替代呢？

```xml
<!-- 加载properties文件 -->
<context:property-placeholder location="classpath:jdbc.properties"/>
<!-- 组件扫描 -->
<context:component-scan base-package="com.liuhao"/>
<!-- 引入其他xml文件 -->
<import resource="classpath:beans.xml"/>
```

​		定义一个配置类替代原有的xml配置文件，标签以外的标签，一般都是在配置类上使用注解完成的

**@Configuration**

​		标识类为配置类，替代原有 xml 配置文件，该注解第一个作用是标识该类是一个配置类，第二个作用是具备@Component 作用

**@ComponentScan**

​		扫描配置，替代原有xml文件中的 <context:component-scan base-package="" />

```java
@Configuration
@ComponentScan({"com.liuhao.service","com.liuhao.dao"})
public class ApplicationContextConfig {}

```

base-package的配置方式：

```
1、指定一个或多个包名：扫描指定包及其子包下使用注解的类
2、不配置包名：扫描当前@componentScan注解配置类所在包及其子包下的类
```

**@PropertySource**

​		用于加载外部properties资源配置，替代原有xml中的 <context:property-placeholder location="" />

```java
@Configuration
@ComponentScan
@PropertySource({"classpath:jdbc.properties","classpath:xxx.properties"})
public class ApplicationContextConfig {}
```

**@Import**

​		用于加载其他配置类，替代原有xml中的 <import resource="classpath:beans.xml"/>  作用在主配置类上，把其他配置类也加载进来，并且这些配置类不用加 @Configuration，也不用扫包,加有Import的是主配置类

```java
@Configuration
@ComponentScan
@PropertySource("classpath:jdbc.properties")
@Import(OtherConfig.class)
public class ApplicationContextConfig {}
```

### 其他注解

**@Primary**

​		用于标注相同类型的Bean优先被使用权，@Primary 是Spring3.0引入的，与@Component和@Bean一起使用，标注该Bean的优先级更高，则在通过类型获取Bean或通过@Autowired根据类型进行注入时，会选用优先级更高的

```java
@Repository("userDao")
public class UserDaoImpl implements UserDao{}
@Repository("userDao2")
@Primary
public class UserDaoImpl2 implements UserDao{}
```

```java
@Bean
public UserDao userDao01(){return new UserDaoImpl();}
@Bean
@Primary
public UserDao userDao02(){return new UserDaoImpl2();}
```

**@Profile**

​		作用同于xml配置profile属性，是进行环境切换使用的，标注在类或方法上，标注当前产生的Bean从属于哪个环境，只有激活了当前环境，被标注的Bean才能被注册到Spring容器里，不指定环境的Bean，任何环境下都能注册到Spring容器里

```java
@Repository("userDao")
@Profile("test")
public class UserDaoImpl implements UserDao{}
@Repository("userDao2")
public class UserDaoImpl2 implements UserDao{}
```

​		可以使用以下两种方式指定被激活的环境：

```
1、使用命令行动态参数，虚拟机参数位置加载 -Dspring.profiles.active=test
2、使用代码的方式设置环境变量 System.setProperty("spring.profiles.active","test");
```

### 注解的解析原理

# 获取 bean

方法定义：

```java
// 根据beanName从容器中获取Bean实例，要求容器中Bean唯一，返回值为Object，需要强转
Object getBean(String beanName)

// 根据Class类型从容器中获取Bean实例，要求容器中Bean类型唯一，返回值为Class类型实例，无需强转
T getBean(Class type)
 
//根据beanName从容器中获得Bean实例，返回值为Class类型实例，无需强转
T getBean(String beanName, Class type)
```

代码

```java
//根据beanName获取容器中的Bean实例，需要手动强转
UserService userService = (UserService) applicationContext.getBean("userService");
//根据Bean类型去容器中匹配对应的Bean实例，如存在多个匹配Bean则报错
UserService userService2 = applicationContext.getBean(UserService.class);
//根据beanName获取容器中的Bean实例，指定Bean的Type类型
UserService userService3 = applicationContext.getBean("userService", UserService.class);
```

## 

 BeanFactory 

导入 Spring maven 坐标

```xml
<!--Spring核心-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.3.7</version>
</dependency>

```

配置好 bean ，获取 bean

```java
//创建BeanFactory
DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
//创建读取器
XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
//加载配置文件
reader.loadBeanDefinitions("beans.xml");
//获取Bean实例对象
UserDao userService = (UserService) beanFactory.getBean("userService");
```

ApplicationContext

​		ApplicationContext 称为Spring容器，内部封装了BeanFactory，比BeanFactory功能更丰富更强大，使用
ApplicationContext 进行开发时，xml配置文件的名称习惯写成applicationContext.xml

```java
//创建ApplicationContext,加载配置文件，实例化容器
ApplicationContext applicationContext = new ClassPathxmlApplicationContext(“applicationContext.xml");
//根据beanName获得容器中的Bean实例
UserService userService = (UserService) applicationContext.getBean("userService");
System.out.println(userService);
```

​		BeanFactory与ApplicationContext的关系

```
1）BeanFactory是Spring的早期接口，称为Spring的Bean工厂，ApplicationContext是后期更高级接口，称之为
Spring 容器；

2）ApplicationContext在BeanFactory基础上对功能进行了扩展，例如：监听功能、国际化功能等。BeanFactory的
API更偏向底层，ApplicationContext的API大多数是对这些底层API的封装；

3）Bean创建的主要逻辑和功能都被封装在BeanFactory中，ApplicationContext不仅继承了BeanFactory，而且
ApplicationContext内部还维护着BeanFactory的引用，所以，ApplicationContext与BeanFactory既有继承关系，又
有融合关系。

4）Bean的初始化时机不同，原始BeanFactory是在首次调用getBean时才进行Bean的创建，而ApplicationContext则
是配置文件加载，容器一创建就将Bean都实例化并初始化好。
```

​		ApplicationContext除了继承了BeanFactory外，还继承了ApplicationEventPublisher（事件发布器）、
ResouresPatternResolver（资源解析器）、MessageSource（消息资源）等。但是ApplicationContext的核心功
能还是BeanFactory。

​		BeanFactory方式时，当调用getBean方法时才会把需要的Bean实例创建，即延迟加载；而ApplicationContext是加载配置文件，容器创建时就将所有的Bean实例都创建好了，存储到一个单例池中，当调
用getBean时直接从单例池中获取Bean实例返回



BeanFactory的继承体系

​		BeanFactory是核心接口，项目运行过程中肯定有具体实现参与，这个具体实现就是DefaultListableBeanFactory
，而ApplicationContext内部维护的Beanfactory的实现类也是它



ApplicationContext的继承体系

​		只在Spring基础环境下，即只导入spring-context坐标时，此时ApplicationContext的继承体系，只在Spring基础环境下，常用的三个ApplicationContext作用如下：

```
ClassPathXmlApplicationContext ：加载类路径下的xml配置的ApplicationContext

FileSystemXmlApplicationContext：加载磁盘路径下的xml配置的ApplicationContext

AnnotationConfigApplicationContext：加载注解配置类的ApplicationContext
```

​		在Spring的web环境下，常用的两个ApplicationContext作用如下：

```
XmlWebApplicationContext： web环境下，加载类路径下的xml配置的ApplicationContext
AnnotationConfigWebApplicationContext： web环境下，加载磁盘路径下的xml配置的ApplicationContext
```


