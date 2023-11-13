# 配置 bean

## xml 配置

### bean 标签属性

```xml
<bean id="TestBean">
	描述：bean的id，可以根据配置的这个值来获取这个bean，如果不配置id，则Spring会把当前Bean实例的全限定名作为beanName
	
<bean class="con.liuhao.service.impl.TestServiceImpl">
	描述：全限定名配置
	
<bean name="aa,bb,cc">
	描述：通过name设置Bean的别名，通过别名也能直接获取到Bean实例，可以设置多个别名
    
<bean scope="">
	描述：通过name设置Bean的别名，通过别名也能直接获取到Bean实例，可以设置多个别名
	值：单纯的Spring环境Bean的作用范围有两个
    	singleton：单例，默认值，Spring容器创建的时候，就会进行Bean的实例化，并存储到容器内部的单例池中每次getBean时都是从单例池中获取相同的Bean实例
    	prototype：原型，Spring容器初始化时不会创建Bean实例，当调用getBean时才会实例化Bean，每次getBean都会创建一个新的Bean实例

<bean lazy-init="">：
	描述：Bean的实例化时机，是否延迟加载。BeanFactory作为容器时无效
    值：发
    	true：延迟加载，也就是当Spring容器创建的时候，不会立即创建Bean实例，等待用到时在创建Bean实例并存储到`单例池中去，后续在使用该Bean直接从单例池获取即可，本质上该Bean还是单例的

<bean init-method="">
	描述：Bean实例化后自动执行的初始化方法，method指定方法名，还可以通过 实现 InitializingBean 接口做初始化
	
<bean destory-method="">
	描述：Bean实例销毁前的方法，method指定方法名
	
<bean autowire="">
	描述：设置自动注入模式，常用的有按照类型byType，按照名字byName
	
<bean facotr-bean="" factory-method="">
	描述：指定哪个工厂Bean的哪个方法完成Bean的创建
```

### 实例化 Bean

三种方式：

- 无参构造
- 静态工厂
- 实例工厂创建
- 实现FactoryBean规范延迟实例化Bean

#### 无参构造

需要有无参构造

```java
public class TestServiceImpl {
    public TestServiceImpl() {
    }
}
```

```xml
<bean id="testService" class="con.liuhao.service.impl.TestServiceImpl"></bean> 
```

​		此时存储到Spring容器（singleObjects单例池）中的Bean的beanName是testService，值是TestServiceImpl对象

#### 静态工厂

​		如果一个Bean不能通过new直接实例化，而是通过工厂类的某个静态方法创建的，需要把<bean>标签的class属性配置为工厂类：

```java
public class TestServiceImpl {
    public TestServiceImpl() {
    }
}

public class TestServiceFactoryBean {
    public static TestService getTestService(){
        return new TestServiceImpl();
    }
}
```

```xml
<bean id="TestService" class="com.liuhao.beanFactory.TestServiceFactoryBean" factory-method="createInstance" factory-method="getTestService">
</bean>
```

#### 实例工厂

​		如果一个Bean不能通过new直接实例化，而是通过工厂类的某个实例方法创建的，需要先配置工厂的bean标签然后在需要创建的对象的bean标签的factory-bean属性配置为工厂类对象，factory-method属性配置为产生实例的方法。

```java
public class TestServiceImpl {
    public TestServiceImpl() {
    }
}

public class TestServiceFactoryBean {
    public TestService getTestService(){
        return new TestServiceImpl();
    }
}
```

```xml
<bean id="testServiceFactory" class="com.liuhao.beanFactory.TestServiceFactoryBean"></bean>
<bean id="testService" factory-bean="testServiceFactory" factory-method="getTestService"></bean>
```

#### 实现 FactoryBean 

FactoryBean 接口规范

```java
public interface FactoryBean<T> {
	String OBJECT_TYPE_ATTRIBUTE = “factoryBeanObjectType”;
	T getObject() throws Exception; //获得实例对象方法
	Class<?> getObjectType(); //获得实例对象类型方法
	default boolean isSingleton() {
		return true;
	}
}
```

代码

```java
public class TestServiceFactoryBean implements FactoryBean<TestService> {
    
    public TestService getObject() throws Exception {
        return new TestServiceImpl();
    }

    public Class<?> getObjectType() {
        return TestService.class;
    }
}

```

配置：配置FacotryBean交由Spring管理即可

```xml
<bean id="testService" class="com.liuhao.beanFactory.TestServiceFactoryBean"></bean>
```

​		TestServiceFactoryBean 会被实例化了，并存储到了单例池 singletonObjects 中，但是 getObject() 方法尚未被执行，TestServiceImpl 也没被实例化，当首次用到 TestServiceImpl 时，才调用 getObject() ，此工厂方式产生的Bean实例不会存储到单例池 singletonObjects 中，会存储到 factoryBeanObjectCache 缓存池中，并且后期每次使用到testService都从该缓存池中返回的是同一个userDao实例。

### bean 的属性注入

两种方式：

- 构造方法
- set 方法

注入的数据类型：

```
普通数据类型，例如：String、int、boolean等，通过value属性指定。
引用数据类型，例如：其他bean，如DataSource等，通过ref属性指定。
集合数据类型，例如：List、Map、Properties等
```

#### 有参构造方法

标签

```xml
<constructor-arg />：
	描述：签不仅仅是为构造方法传递参数，只要是为了实例化对象而传递的参数都可以通过这个标签完成，例如上面通过静态工厂方法实例化Bean所传递的参数也是要通过此标签进行传递的

<constructor-arg name="name" ref="userDao"/>
	ref：注入其他bean
<constructor-arg name="name" value="haohao"/>
	value：注入基本数据类型和String
```

代码

```java
public class TestServiceImpl implements TestService {
    
    private String username;

    public TestServiceImpl(String username) {
        this.username = username;
    }
}
```

配置：注入基本数据类型和String

```xml
<bean id="TestService" class="con.liuhao.service.impl.TestServiceImpl">
	<constructor-arg name="username" value="张三" />
</bean> 
```

#### set 方法

标签

```xml
<property name="xxx">
    描述：需要在类里有 setXxx 命名的方法，如 name="name" 那么就必须有 setName(String name) 方法 
    
<property name="username" ref="xxxBean"/>
    ref：注入其他bean
<property name="username" value="张三"/>
    value：注入基本数据类型和String
```

代码

```java
public class Son{
    
}

public class TestServiceImpl implements TestService {
    
    private String username;
    private List<String> friends;
    private List<Son> sons1;
    private List<Son> sons2;
    private Map<String,String> sons3;
    private Map<String,Son> sons4;
    private Properties properties;

    public void setUsername(String username){
        this.username = username;
    }
    
    public void setFriends(List<String> friends){
        this.friends = friends;
    }
    
    public void setSons1(List<String> sons1){
        this.sons1 = sons1;
    }
    
    public void setSons2(List<String> sons2){
        this.sons2 = sons2;
    }
    
    public void setSons3(Map<String,String> sons3){
        this.sons3 = sons3;
    }
    
    public void setSons4(Map<String,Son> sons4){
        this.sons4 = sons4;
    }
    
    public void setProperties(Properties properties){
        this.properties = properties;
    }
}
```

配置

```xml
<bean id="son" class="xxx.xx..Son"></bean>

<bean id="TestService" class="con.liuhao.service.impl.TestServiceImpl">
    <property name="username" value="张三"/>
    <property name="friends">
        <list>
            <value>李四</value>
            <value>王五</value>
        </list>
    </property>
    <property name="sons1">
        <list>
            <bean class="xxx.xx..Son"></bean>
        </list>
    </property>
    <property name="sons2">
        <list>
            <ref bean="son"/>
        </list>
    </property>
    <property name="sons3">
        <map>
            <entry key="大儿子" value="大儿"/>
        </map>
    </property>
    <property name="sons4">
        <map>
            <entry key="大儿子" value-ref="son"/>
        </map>
    </property>
    <property name="properties">
        <prop key="xxx">XXX</prop>
    </property>
</bean> 
```

自动配置

​		如果被注入的属性类型是Bean引用的话，那么可以在 标签中使用 autowire 属性去配置自动注入方式，属
性值有两个：

```
byName：通过属性名自动装配，即去匹配 setXxx 与 id="xxx"（name="xxx"）是否一致；

byName：通过属性名自动装配，即去匹配 setXxx 与 id="xxx"（name="xxx"）是否一致；
```

### 配置非自定义 bean

​		配置第三方jar包中的类的 Bean，这些Bean要想让Spring进行管理，也需要对其进行配置。

配置非自定义的Bean需要考虑如下两个问题：

```
1、被配置的Bean的实例化方式是什么？无参构造、有参构造、静态工厂方式还是实例工厂方式；
2、被配置的Bean是否需要注入必要属性。
```

**案列：配置 Druid 数据源交由Spring管理**

导入 maven 坐标

```xml
<!-- mysql驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.49</version>
</dependency>
<!-- druid数据源 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.23</version>
</dependency>
```

配置 bean

```xml
<!--配置 DruidDataSource数据源-->
<bean class="com.alibaba.druid.pool.DruidDataSource">
<!--配置必要属性-->
    <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
    <property name="url" value="jdbc://localhost:3306/test"/>
    <property name="username" value="root"/>
    <property name="password" value="root"/>
</bean>
```

**案列：配置 Connection**

​		Connection 的产生是通过DriverManager的静态方法getConnection获取的，所以我们要用静态工厂方式配置

配置 Bean

```xml
<bean class="java.lang.Class" factory-method="forName">
	<constructor-arg name="className" value="com.mysql.jdbc.Driver"/>
</bean>
<bean id="connection" class="java.sql.DriverManager" factory-method="getConnection" 
scope="prototype">
    <constructor-arg name="url" value="jdbc:mysql://localhost:3306/test"/>
    <constructor-arg name="user" value="root"/>
    <constructor-arg name="password" value="root"/>
</bean>
```

**案列：配置日期对象**

代码：产生一个指定日期格式的对象

```java
String currentTimeStr = "2023-08-27 07:20:00";
SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Date date = simpleDateFormat.parse(currentTimeStr);
```

配置：可以看成是实例工厂方式，使用Spring配置方式产生Date实例

```xml
<bean id="simpleDateFormat" class="java.text.SimpleDateFormat">
	<constructor-arg name="pattern" value="yyyy-MM-dd HH:mm:ss"/>
</bean>
<bean id="date" factory-bean="simpleDateFormat" factory-method="parse">
	<constructor-arg name="source" value="2023-08-27 07:20:00"/>
</bean>
```

**案列：配置 MyBatis 的 SqlSessionFactory**

导入 maven 坐标

```xml
<!--mybatis框架-->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.5</version>
</dependency>
<!-- mysql驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.49</version>
</dependency>
```

代码：MyBatis原始获得SqlSessionFactory的方式

```java
//加载mybatis核心配置文件，使用Spring静态工厂方式
InputStream in = Resources.getResourceAsStream(“mybatis-conifg.xml”);
//创建SqlSessionFactoryBuilder对象，使用Spring无参构造方式
SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
//调用SqlSessionFactoryBuilder的build方法，使用Spring实例工厂方式
SqlSessionFactory sqlSessionFactory = builder.build(in);
```

配置：

```xml
<!--静态工厂方式产生Bean实例-->
<bean id="inputStream" class="org.apache.ibatis.io.Resources" factory-method="getResourceAsStream">
	<constructor-arg name=“resource” value=“mybatis-config.xml/>
</bean>
<!--无参构造方式产生Bean实例-->
<bean id="sqlSessionFactoryBuilder" class="org.apache.ibatis.session.SqlSessionFactoryBuilder"/>
<!--实例工厂方式产生Bean实例-->
<bean id="sqlSessionFactory" factory-bean="sqlSessionFactoryBuilder" factory-method="build">
	<constructor-arg name="inputStream" ref="inputStream"/>
</bean>
```

###  Bean  实例化实现

​		Spring容器在进行初始化时，会将xml配置的 <bean> 的信息封装成一个BeanDefinition对象，所有的BeanDefinition存储到一个名为beanDefinitionMap的Map集合中去，Spring框架在对该Map进行遍历，使用反射创建Bean实例对象，创建好的Bean对象存储在一个名为singletonObjects的Map集合中，当调用getBean方法时则最终从该Map集合中取出Bean实例对象返回

​	**Bean 实例化的基本流程**

```
1、加载xml配置文件，解析获取配置中的每个<bean>的信息，封装成一个个的BeanDefinition对象;
2、将BeanDefinition存储在一个名为beanDefinitionMap的Map<String,BeanDefinition>中;
3、ApplicationContext底层遍历beanDefinitionMap，创建Bean实例对象;
4、创建好的Bean实例对象，被存储到一个名为singletonObjects的Map<String,Object>中;
5、当执行applicationContext.getBean(beanName)时，从singletonObjects去匹配Bean实例返回
```

​	**BeanDefinition 对象**

配置：Bean信息定义对象-BeanDefinition

```xml
<bean id="" class="" name="" lazyinit="" scope="" init-method="" destroy-method="" factory-bean="" 
	factory-method="" abstract="" depends-on="" parent="">
    <property name="" ref=""/>
    <property name="" ref=""/>
    <property name="" value=""/>
</bean>
```

代码：信息封装

```java
public interface BeanDefinition {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";
    
    void setBeanClassName(@Nullable String var1);
    String getBeanClassName();
    
    void setScope(@Nullable String var1);
    String getScope();
    
    void setLazyInit(boolean var1);
    boolean isLazyInit();
    
    void setFactoryBeanName(@Nullable String var1);
    String getFactoryBeanName();
    
    void setFactoryMethodName(@Nullable String var1);
    String getFactoryMethodName();
    
    void setInitMethodName(@Nullable String var1);
    String getInitMethodName();
    //..... 省略部分属性和方法
}	
```

​		DefaultListableBeanFactory 对象内部维护着一个Map用于存储封装好的BeanDefinitionMap

```
public class DefaultListableBeanFactory extends ... implements ... {
    // 存储<bean>标签对应的BeanDefinition对象
    // key:是Bean的beanName，value:是Bean定义对象BeanDefinition
    private final Map<String, BeanDefinition> beanDefinitionMap;
}
```

​		Spring 框架会取出 beanDefinitionMap 中的每个 BeanDefinition 信息，反射构造方法或调用指定的工厂方法生成Bean 例对象，所以**只要将 BeanDefinition 注册到 beanDefinitionMap 这个Map中，Spring 就会进行对应的 Bean 的实例化操作。**

​		Bean 实例及单例池 singletonObjects， beanDefinitionMap 中的 BeanDefinition 会被转化成对应的Bean实例对象，存储到单例池 singletonObjects 中去，在 DefaultListableBeanFactory 的上四级父类 DefaultSingletonBeanRegistry 中，维护着 singletonObjects，源码如下：

```java
public class DefaultSingletonBeanRegistry extends ... implements ... {
//存储Bean实例的单例池
////key:是Bean的beanName，value:是Bean的实例对象
private final Map<String, Object> singletonObjects = new ConcurrentHashMap(256);
}

```

#### Spring 的后置处理器

​		Spring 的后处理器是 Spring 对外开发的重要扩展点，允许我们介入到Bean的整个实例化流程中来，以达到动态注册
BeanDefinition，动态修改BeanDefinition，以及动态修改Bean的作用。Spring主要有两种后处理器：

```
1、 BeanFactoryPostProcessor：Bean工厂后处理器，在BeanDefinitionMap填充完毕，Bean实例化之前执行；
2、 BeanPostProcessor：Bean后处理器，一般在Bean实例化之后，填充到单例池singletonObjects之前执行。
```

##### Bean 工厂后处理器

​		BeanFactoryPostProcessor，是一个接口规范，实现了该接口的类只要交由Spring容器管理的话，那么Spring就会回调该接口的方法，用于对BeanDefinition注册和修改的功能。

​		BeanFactoryPostProcessor 定义：

```java
public interface BeanFactoryPostProcessor {
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);
}
```

​		编写BeanFactoryPostProcessor：

```java
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) 
throws BeansException{
		System.out.println("MyBeanFactoryPostProcessor执行了...");
	}
}
```

​		配置BeanFactoryPostProcessor：

```xml
<bean class="com.itheima.processor.MyBeanFactoryPostProcessor"/>
```

​		postProcessBeanFactory 参数本质就是 DefaultListableBeanFactory，拿到BeanFactory的引用，自然就可以
对beanDefinitionMap中的BeanDefinition进行操作了 ，例如对UserDaoImpl的BeanDefinition进行修改操作

```java
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) 
throws BeansException {
         //获得UserDao定义对象
		BeanDefinition userDaoBD = beanFactory.getBeanDefinition(“userDao”);
         //修改class
		userDaoBD.setBeanClassName("com.itheima.dao.impl.UserDaoImpl2"); 
		//userDaoBD.setInitMethodName(methodName); //修改初始化方法
		//userDaoBD.setLazyInit(true); //修改是否懒加载
		//... 省略其他的设置方式 ...
	}
}

```

​		上面已经对指定的BeanDefinition进行了修改操作，下面对BeanDefiition进行注册操作

```java
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //强转成子类DefaultListableBeanFactory
        if(configurableListableBeanFactory instanceof DefaultListableBeanFactory){
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) 
            configurableListableBeanFactory;
            BeanDefinition beanDefinition = new RootBeanDefinition();
            beanDefinition.setBeanClassName("com.itheima.dao.UserDaoImpl2");
            //进行注册操作
            beanFactory.registerBeanDefinition("userDao2",beanDefinition);
		}	
	}
}

```

​		Spring 提供了一个BeanFactoryPostProcessor的子接口BeanDefinitionRegistryPostProcessor专门用于注册
BeanDefinition操作

```java
public class MyBeanFactoryPostProcessor2 implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory 
configurableListableBeanFactory) throws BeansException {}
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) 
throws BeansException {
        BeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClassName("com.itheima.dao.UserDaoImpl2");
        beanDefinitionRegistry.registerBeanDefinition("userDao2",beanDefinition);
	}
}

```

##### Bean 后处理器

​		Bean 被实例化后，到最终缓存到名为 singletonObjects 单例池之前，中间会经过 Bean 的初始化过程，例如：属性的填充、初始方法init的执行等，其中有一个对外进行扩展的点 BeanPostProcessor，我们称为Bean后处理。跟上面的
Bean 工厂后处理器相似，它也是一个接口，实现了该接口并被容器管理的 BeanPostProcessor，会在流程节点上被
Spring自动调用。

​		BeanPostProcessor 接口定义：

```java
public interface BeanPostProcessor {
    @Nullable
    //在属性注入完毕，init初始化方法执行之前被回调
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws 
BeansException {
		return bean;
	}
    
    @Nullable
    //在初始化方法执行之后，被添加到单例池singletonObjects之前被回调
    default Object postProcessAfterInitialization(Object bean, String beanName) throws 
BeansException {
		return bean;
	}
}

```

​		自定义MyBeanPostProcessor，完成快速入门测试：

```java
public class MyBeanPostProcessor implements BeanPostProcessor {
    /* 参数： bean是当前被实例化的Bean，beanName是当前Bean实例在容器中的名称
    返回值：当前Bean实例对象 */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException{
		System.out.println("BeanPostProcessor的before方法...");
		return bean;
	}
    /* 参数： bean是当前被实例化的Bean，beanName是当前Bean实例在容器中的名称
    返回值：当前Bean实例对象 */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException{
    	System.out.println("BeanPostProcessor的after方法...");
    	return bean;
    }
}

```

​	配置MyBeanPostProcessor：

```xml
<bean class="com.itheima.processors.MyBeanPostProcessor"></bean>
```

### Bean 生命周期

​		Spring Bean 的生命周期是从 Bean 实例化之后，即通过反射创建出对象之后，到Bean成为一个完整对象，最终存储
到单例池中，这个过程被称为Spring Bean的生命周期。Spring Bean的生命周期大体上分为三个阶段：

```
1、Bean的实例化阶段：Spring框架会取出BeanDefinition的信息进行判断当前Bean的范围是否是singleton的，是否不是延迟加载的，是否不是FactoryBean等，最终将一个普通的singleton的Bean通过反射进行实例化；

2、Bean的初始化阶段：Bean创建之后还仅仅是个"半成品"，还需要对Bean实例的属性进行填充、执行一些Aware接口方法、执行BeanPostProcessor方法、执行InitializingBean接口的初始化方法、执行自定义初始化init方法等。该阶段是Spring最具技术含量和复杂度的阶段，Aop增强功能，后面要学习的Spring的注解功能等、spring高频面试题Bean的循环引用问题都是在这个阶段体现的；

3、Bean的完成阶段：经过初始化阶段，Bean就成为了一个完整的Spring Bean，被存储到单例池singletonObjects中去了，即完成了Spring Bean的整个生命周期。
```

​		Spring Bean的初始化过程涉及如下几个过程：

```
1、Bean实例的属性填充
2、Aware接口属性注入
3、BeanPostProcessor的before()方法回调
4、InitializingBean接口的初始化方法回调
5、自定义初始化方法init回调
6、BeanPostProcessor的after()方法回调
```

​		Bean实例属性填充：BeanDefinition 中有对当前Bean实体的注入信息通过属性propertyValues进行了存储

Spring在进行属性注入时，会分为如下几种情况：

```
1、注入普通属性，String、int或存储基本类型的集合时，直接通过set方法的反射设置进去；

2、注入单向对象引用属性时，从容器中getBean获取后通过set方法反射设置进去，如果容器中没有，则先创建被注入对象Bean实例（完成整个生命周期）后，在进行注入操作；

3、注入双向对象引用属性时，就比较复杂了，涉及了循环引用（循环依赖）问题，下面会详细阐述解决方案。
```

​		多个实体之间相互依赖并形成闭环的情况就叫做"循环依赖"，也叫做"循环引用"

```java
public class UserServiceImpl implements UserService{
    public void setUserDao(UserDao userDao) {}
    }
public class UserDaoImpl implements UserDao{
    public void setUserService(UserService userService){}
	}
}
```

​		配置

```xml
<bean id="userService" class="com.itheima.service.impl.UserServiceImpl">
	<property name="userDao" ref="userDao"/>
</bean>
<bean id="userDao" class="com.itheima.dao.impl.UserDaoImpl">
	<property name="userService" ref="userService"/>
</bean>
```

​		分析

```
1、UserService 创建完毕，对象已经存在内存中，但是生命周期过程尚未执行，即当前 UserService 是一个半成品

2、UserService 需要注入UserDao，所以执行UserDao的创建，创建完毕后执行后续生命周期过程，由于UserDao需要注入UserService，就从内存中（不是单例池）获得引用进行注入

3、UserDao 生命周期执行完毕后，一个完成的UserDao对象就完成了，返回继续执行UserSerivce的属性注入操作
```

​		Spring提供了三级缓存存储 完整Bean实例 和 半成品Bean实例 ，用于解决循环引用问题在DefaultListableBeanFactory 的上四级父类DefaultSingletonBeanRegistry中提供如下三个Map：

```java
public class DefaultSingletonBeanRegistry ... {
    //1、最终存储单例Bean成品的容器，即实例化和初始化都完成的Bean，称之为"一级缓存"
    Map<String, Object> singletonObjects = new ConcurrentHashMap(256);
    //2、早期Bean单例池，缓存半成品对象，且当前对象已经被其他对象引用了，称之为"二级缓存"
    Map<String, Object> earlySingletonObjects = new ConcurrentHashMap(16);
    //3、单例Bean的工厂池，缓存半成品对象，对象未被引用，使用时在通过工厂创建Bean，称之为"三级缓存"
    Map<String, ObjectFactory<?>> singletonFactories = new HashMap(16);
}

```

​		UserService和UserDao循环依赖的过程结合上述三级缓存描述一下：

```
1、UserService 实例化对象，但尚未初始化，将UserService存储到三级缓存；
2、UserService 属性注入，需要UserDao，从缓存中获取，没有UserDao；
3、UserDao实例化对象，但尚未初始化，将UserDao存储到到三级缓存；
4、UserDao属性注入，需要UserService，从三级缓存获取UserService，UserService从三级缓存移入二级缓存；
5、UserDao执行其他生命周期过程，最终成为一个完成Bean，存储到一级缓存，删除二三级缓存；
6、UserService 注入UserDao；
7、UserService执行其他生命周期过程，最终成为一个完成Bean，存储到一级缓存，删除二三级缓存
```

##### Aware 接口

​		Aware接口是一种框架辅助属性注入的一种思想，其他框架中也可以看到类似的接口。框架具备高度封装性，我们接
触到的一般都是业务代码，一个底层功能API不能轻易的获取到，但是这不意味着永远用不到这些对象，如果用到了，就可以使用框架提供的类似Aware的接口，让框架给我们注入该对象。

​		常用的 Aware 接口

```
ServletContextAware 
	回调方法：setServletContext(ServletContext context) 
	说明：Spring框架回调方法注入ServletContext对象，web环境下才生效

BeanFactoryAware
	回调方法：setBeanFactory(BeanFactory factory) 
	说明：Spring框架回调方法注入beanFactory对象

BeanNameAware
	回调方法：setBeanName(String beanName) 
	说明：Spring框架回调方法注入当前Bean在容器中的beanName

ApplicationContextAware
	回调方法：setApplicationContext(ApplicationContext applicationContext)
	说明：Spring框架回调方法注入applicationContext对象

```



### 其他标签

#### 环境配置

<beans> 标签，除了经常用的做为根标签外，还可以嵌套在根标签内，使用profile属性切换开发环境

```xml
<!-- 配置测试环境下，需要加载的Bean实例 -->
<beans profile="test">
</beans>
<!-- 配置开发环境下，需要加载的Bean实例 -->
<beans profile="dev">
</beans
```

可以使用以下两种方式指定被激活的环境：

```
1、使用命令行动态参数，虚拟机参数位置加载 -Dspring.profiles.active=test
2、使用代码的方式设置环境变量 System.setProperty("spring.profiles.active","test")
```

#### 导入其他配置文件

<import>  标签，用于导入其他配置文件，可以将一个配置文件根据业务某块进行拆分，拆分后，最终通过标签导入到一个主配置文件中，项目加载主配置文件就连同 导入的文件一并加载

```xml
<!--导入用户模块配置文件-->
<import resource="classpath:UserModuleApplicationContext.xml"/>
<!--导入商品模块配置文件-->
<import resource="classpath:ProductModuleApplicationContext.xml"/
```

#### bean 别名

<alias>标签是为某个Bean添加别名，与在 标签上使用name属性添加别名的方式一样，为UserServiceImpl指定四个别名：aaa、bbb、xxx、yyy

```xml
<!--配置UserService-->
<bean id="userService" name="aaa,bbb" class="com.itheima.service.impl.UserServiceImpl">
<property name="userDao" ref="userDao"/>
</bean>
<!--指定别名-->
<alias name="userService" alias="xxx"/>
<alias name="userService" alias="yyy"/>
```

在beanFactory中维护着一个名为aliasMap的Map集合，存储别名和beanName之间的映射关系

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



# 整合第三方框架(待)

xml

xml整合第三方框架有两种整合方案：

```
1、不需要自定义名空间，不需要使用Spring的配置文件配置第三方框架本身内容，例如：MyBatis；
2、需要引入第三方框架命名空间，需要使用Spring的配置文件配置第三方框架本身内容，例如：Dubbo。
```

Spring整合MyBatis的步骤如下：

```
1、导入MyBatis整合Spring的相关坐标；（MyBatis提供了mybatis-spring.jar专门用于两大框架的整合）
2、编写Mapper和Mapper.xml；
3、配置SqlSessionFactoryBean和MapperScannerConfigurer；
4、编写测试代码

```

配置SqlSessionFactoryBean和MapperScannerConfigurer：

```xml
<!--配置数据源-->
<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
    <property name="url" value="jdbc:mysql://localhost:3306/mybatis"></property>
    <property name="username" value="root"></property>
    <property name="password" value="root"></property>
</bean>
<!--配置SqlSessionFactoryBean-->
<bean class="org.mybatis.spring.SqlSessionFactoryBean">
	<property name="dataSource" ref="dataSource"></property>
</bean>
<!--配置Mapper包扫描-->
<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
	<property name="basePackage" value="com.itheima.dao"></property>
</bean>

```

Mapper 接口：

```java
public interface UserMapper {
	List<User> findAll();
}

```

Mapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.itheima.dao.UserMapper">
    <select id="findAll" resultType="com.itheima.pojo.User">
    	select * from tb_user
    </select>
</mapper>

```

测试代码

```java
ClassPathxmlApplicationContext applicationContext = new
    		ClassPathxmlApplicationContext("applicationContext.xml");
UserMapper userMapper = applicationContext.getBean(UserMapper.class);
List<User> all = userMapper.findAll();
System.out.println(all);
```

原理

​		整合包里提供了一个SqlSessionFactoryBean和一个扫描Mapper的配置对象，SqlSessionFactoryBean一旦被实例

化，就开始扫描Mapper并通过动态代理产生Mapper的实现类存储到Spring容器中。相关的有如下四个类：

```
1、SqlSessionFactoryBean：需要进行配置，用于提供SqlSessionFactory；
2、MapperScannerConfigurer：需要进行配置，用于扫描指定mapper注册BeanDefinition；
3、MapperFactoryBean：Mapper的FactoryBean，获得指定Mapper时调用getObject方法；
4、ClassPathMapperScanner：definition.setAutowireMode(2) 修改了自动注入状态，所以
5、MapperFactoryBean中的setSqlSessionFactory会自动注入进去。
```

​		配置SqlSessionFactoryBean作用是向容器中提供SqlSessionFactory，SqlSessionFactoryBean实现了
FactoryBean和InitializingBean两个接口，所以会自动执行getObject() 和afterPropertiesSet()方法

```java
SqlSessionFactoryBean implements FactoryBean<SqlSessionFactory>, InitializingBean{
	public void afterPropertiesSet() throws Exception {
	//创建SqlSessionFactory对象
	this.sqlSessionFactory = this.buildSqlSessionFactory();
	}
	public SqlSessionFactory getObject() throws Exception {
		return this.sqlSessionFactory;
	}
}

```

​		配置 MapperScannerConfigurer 作用是扫描Mapper，向容器中注册 Mapper 对应的 MapperFactoryBean，
MapperScannerConfigurer 实现了 BeanDefinitionRegistryPostProcessor 和InitializingBean 两个接口，会在
postProcessBeanDefinitionRegistry 方法中向容器中注册 MapperFactoryBean

```java
class MapperScannerConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean{
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
        scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ",; \t\n"));
    }
}


class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner {
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
        } else {
        this.processBeanDefinitions(beanDefinitions);
        }
    }
    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        // 设置Mapper的beanClass是org.mybatis.spring.mapper.MapperFactoryBean
        definition.setBeanClass(this.mapperFactoryBeanClass);
        // 设置MapperBeanFactory 进行自动注入
        // autowireMode取值：1是根据名称自动装配，2是根据类型自动装配
        definition.setAutowireMode(2);
    }
}

class ClassPathBeanDefinitionScanner{
    public int scan(String... basePackages) {
    	this.doScan(basePackages);
    }
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        //将扫描到的类注册到beanDefinitionMap中，此时beanClass是当前类全限定名
        this.registerBeanDefinition(definitionHolder, this.registry);
        return beanDefinitions;
    }
}

UserMapper userMapper = applicationContext.getBean(UserMapper.class);

public class MapperFactoryBean<T> extends SqlSessionDaoSupport implements FactoryBean<T> {
    public MapperFactoryBean(Class<T> mapperInterface) {
    	this.mapperInterface = mapperInterface;
    }
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
    	this.sqlSessionTemplate = this.createSqlSessionTemplate(sqlSessionFactory);
    }
    public T getObject() throws Exception {
    	return this.getSqlSession().getMapper(this.mapperInterface);
    }
}


```



Spring 整合 Dubbo

要使用Dubbo提供的命名空间的扩展方式，自定义了一些Dubbo的标签

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
xsi:schemaLocation="http://www.springframework.org/schema/beans 
http://www.springframework.org/schema/beans/spring-beans.xsd 
http://dubbo.apache.org/schema/dubbo http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
    <!--配置应用名称-->
    <dubbo:application name="dubbo1-consumer"/>
    <!--配置注册中心地址-->
    <dubbo:registry address="zookeeper://localhost:2181"/>
    <!--扫描dubbo的注解-->
    <dubbo:annotation package="com.itheima.controller"/>
    <!--消费者配置-->
    <dubbo:consumer check="false" timeout="1000" retries="0"/>
</beans>

```

以Spring的 context 命名空间去进行讲解，该方式也是命名空间扩展方式。
需求：加载外部properties文件，将键值对存储在Spring容器中

```properties
jdbc.url=jdbc:mysql://localhost:3306/mybatis
jdbc.username=root
jdbc.password=root
```

引入context命名空间，在使用context命名空间的标签，使用SpEL表达式在xml或注解中根据key获得value

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:context="http://www.springframework.org/schema/context"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://www.springframework.org/schema/beans 
http://www.springframework.org/schema/beans/spring-beans.xsd
http://www.springframework.org/schema/context 
http://www.springframework.org/schema/context/spring-context.xsd">
<context:property-placeholder location="classpath:jdbc.properties" />
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="url" value="${jdbc.url}"></property>
        <property name="username" value="${jdbc.username}"></property>
        <property name="password" value="${jdbc.password}"></property>
    </bean>
<beans>

```

​		其实，加载的properties文件中的属性最终通过Spring解析后会被存储到了Spring容器的environment中去，不仅自己定义的属性会进行存储，Spring也会把环境相关的一些属性进行存储

