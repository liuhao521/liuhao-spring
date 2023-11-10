**概念：**

​		面向切面编程，通过预编译和运行期动态代理实现。利用AOP可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率

​		AOP，Aspect Oriented Programming，面向切面编程，是对面向对象编程OOP的升华。OOP是纵向对一个事物的抽象，一个对象包括静态的属性信息，包括动态的方法信息等。而AOP是横向的对不同事物的抽象，属性与属性、方法与方法、对象与对象都可以组成一个切面，而用这种思维去设计编程的方式叫做面向切面编程

**作用：**

​		在程序运行期间，不修改源码对已有的方法进行增强

**优势：**

​		减少重复性代码、提高开发效率、维护方便

**AOP相关术语**

​	连接点：即拦截到的方法，spring只支持方法类型的连接点
​	切点：指我们要对哪些连接点进行拦截的定义
​	通知：即拦截到连接点后要做的事情
​	通知的类型：前置通知，后置通知，异常通知，最终通知，环绕通知

AOP思想的实现方案：

​		动态代理技术，在运行期间，对目标对象的方法进行增强，代理对象同名方法内可以执行原有逻辑的同时嵌入执行其他增强逻辑或其他对象的方法



# **xml-AOP配置**

需要配置哪些东西：

```
配置哪些包、哪些类、哪些方法需要被增强

配置目标方法要被哪些通知方法所增强，在目标方法执行之前还是之后执行增强
```

xml方式配置AOP的步骤：

```
1、导入AOP相关坐标；
2、准备目标类、准备增强类，并配置给Spring管理；
3、配置切点表达式（哪些方法被增强）；
4、配置织入（切点被哪些通知方法增强，是前置增强还是后置增强）。
```

1、导入AOP相关坐标：Spring-context坐标下已经包含spring-aop的包了，所以就不用额外导入了

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.6</version>
</dependency>
```

2、准备目标类、准备增强类，并配置给Spring管理；

代码

```java
public interface UserService {
    void show1();
    void show2();
}

public class UserServiceImpl implements UserService {
    public void show1() {
    	System.out.println("show1...");
    }
    public void show2() {
    	System.out.println("show2...");
    }
}

public class MyAdvice {
    public void beforeAdvice(){
    	System.out.println("beforeAdvice");
    }
public void afterAdvice(){
    	System.out.println("afterAdvice");
    }
}
```

配置

```xml
<!--配置目标类,内部的方法是连接点-->
<bean id="userService" class="com.liuhao.service.impl.UserServiceImpl"/>
<!--配置通知类,内部的方法是增强方法-->
<bean id=“myAdvice" class="com.itheima.advice.MyAdvice"/>
```

3、配置切点表达式（哪些方法被增强）
4、配置织入（切点被哪些通知方法增强，是前置增强还是后置增强）

```xml
<aop:config>
    <!--配置切点表达式,对哪些方法进行增强-->
    <aop:pointcut id="myPointcut" expression="execution(void com.liuhao.service.impl.UserServiceImpl.show1())"/>
    <!--切面=切点+通知-->
    <aop:aspect ref="myAdvice">
    <!--指定前置通知方法是beforeAdvice-->
    <aop:before method="beforeAdvice" pointcut-ref="myPointcut"/>
    <!--指定后置通知方法是afterAdvice-->
    <aop:after-returning method="afterAdvice" pointcut-ref="myPointcut"/>
    </aop:aspect>
</aop:config>
```

xml方式AOP配置详解
		xml配置AOP的方式还是比较简单的，下面看一下AOP详细配置的细节：

```
1、切点表达式的配置方式
2、切点表达式的配置语法
3、通知的类型
4、AOP的配置的两种方式
```

​		切点表达式的配置方式有两种，直接将切点表达式配置在通知上，也可以将切点表达式抽取到外面，在通知上进行引用：

```xml
<aop:config>
	<!--配置切点表达式,对哪些方法进行增强-->
    <aop:pointcut id="myPointcut" expression="execution(void 
    com.itheima.service.impl.UserServiceImpl.show1())"/>
    <!--切面=切点+通知-->
    <aop:aspect ref="myAdvice">
    <!--指定前置通知方法是beforeAdvice-->
    <aop:before method="beforeAdvice" pointcut-ref="myPointcut"/>
    <!--指定后置通知方法是afterAdvice-->
    <aop:after-returning method="afterAdvice" pointcut="execution(void 
    com.itheima.service.impl.UserServiceImpl.show1())"/>
    </aop:aspect>
</aop:config>

```

​		切点表达式是配置要对哪些连接点（哪些类的哪些方法）进行通知的增强，语法如下：

```xml
execution([访问修饰符]返回值类型 包名.类名.方法名(参数))
```

其中：

```
1、访问修饰符可以省略不写；
2、返回值类型、某一级包名、类名、方法名 可以使用 * 表示任意；
3、包名与类名之间使用单点 . 表示该包下的类，使用双点 .. 表示该包及其子包下的类；
4、参数列表可以使用两个点 .. 表示任意参数。
```

举例：

```
//表示访问修饰符为public、无返回值、在com.itheima.aop包下的TargetImpl类的无参方法show
execution(public void com.itheima.aop.TargetImpl.show())
//表述com.itheima.aop包下的TargetImpl类的任意方法
execution(* com.itheima.aop.TargetImpl.*(..))
//表示com.itheima.aop包下的任意类的任意方法
execution(* com.itheima.aop.*.*(..))
//表示com.itheima.aop包及其子包下的任意类的任意方法
execution(* com.itheima.aop..*.*(..))
//表示任意包中的任意类的任意方法
execution(* *..*.*(..))
```

AspectJ的通知由以下五种类型

```
前置通知 < aop:before > 目标方法执行之前执行
后置通知 < aop:after-returning > 目标方法执行之后执行，目标方法异常时，不在执行
环绕通知 < aop:around > 目标方法执行前后执行，目标方法异常时，环绕后方法不在执行
异常通知 < aop:after-throwing > 目标方法抛出异常时执行
最终通知 < aop:after > 不管目标方法是否有异常，最终都会执行

```

环绕通知

```
public void around(ProceedingJoinPoint joinPoint) throws Throwable {
//环绕前
System.out.println("环绕前通知");
//目标方法
joinPoint.proceed();
///环绕后
System.out.println("环绕后通知");
}
```



```
<aop:around method="around" pointcut-ref="myPointcut"/>
```

异常通知

​		当目标方法抛出异常时，异常通知方法执行，且后置通知和环绕后通知不在执行

```
public void afterThrowing(){
System.out.println("目标方法抛出异常了，后置通知和环绕后通知不在执行");
}
```



```
<aop:after-throwing method="afterThrowing" pointcut-ref="myPointcut"/>
```



最终通知

​		类似异常捕获中的finally，不管目标方法有没有异常，最终都会执行的通知

```
public void after(){
System.out.println("不管目标方法有无异常，我都会执行");
}
```



```
<aop:after method="after" pointcut-ref="myPointcut"/>
```





通知方法在被调用时，Spring可以为其传递一些必要的参数

```
JoinPoint 连接点对象，任何通知都可使用，可以获得当前目标对象、目标方法参数等信息
ProceedingJoinPoint JoinPoint子类对象，主要是在环绕通知中执行proceed()，进而执行目标方法
Throwable 异常对象，使用在异常通知中，需要在配置文件中指出异常对象名称
```



JoinPoint 对象

```
public void 通知方法名称(JoinPoint joinPoint){
//获得目标方法的参数
System.out.println(joinPoint.getArgs());
//获得目标对象
System.out.println(joinPoint.getTarget());
//获得精确的切点表达式信息
System.out.println(joinPoint.getStaticPart());
}

```

ProceedingJoinPoint对象

```
public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
System.out.println(joinPoint.getArgs());//获得目标方法的参数
System.out.println(joinPoint.getTarget());//获得目标对象
System.out.println(joinPoint.getStaticPart());//获得精确的切点表达式信息
Object result = joinPoint.proceed();//执行目标方法
return result;//返回目标方法返回值
}
```

Throwable对象

```
public void afterThrowing(JoinPoint joinPoint,Throwable th){
//获得异常信息
System.out.println("异常对象是："+th+"异常信息是："+th.getMessage());
}
```



```
<aop:after-throwing method="afterThrowing" pointcut-ref="myPointcut" throwing="th"/>

```



AOP的另一种配置方式，该方式需要通知类实现Advice的子功能接口

```
public interface Advice {
}

```

例如：通知类实现了前置通知和后置通知接口

```
public class Advices implements MethodBeforeAdvice, AfterReturningAdvice {
public void before(Method method, Object[] objects, Object o) throws Throwable {
System.out.println("This is before Advice ...");
}
public void afterReturning(Object o, Method method, Object[] objects, Object o1) throws 
Throwable {
System.out.println("This is afterReturn Advice ...");
}}
```



切面使用advisor标签配置

```
<aop:config>
<!-- 将通知和切点进行结合 -->
<aop:advisor advice-ref="advices" pointcut="execution(void 
com.itheima.aop.TargetImpl.show())"/>
</aop:config>

```

又例如：通知类实现了方法拦截器接口

```
public class MyMethodInterceptor implements MethodInterceptor {
@Override
public Object invoke(MethodInvocation methodInvocation) throws Throwable {
System.out.println("前置逻辑功能...");
//执行目标方法
Object invoke = 
methodInvocation.getMethod().invoke(methodInvocation.getThis(),methodInvocation.getArguments());
System.out.println("后置逻辑功能...");
return invoke;}}

```

切面使用advisor标签配置

```
<aop:config>
<!-- 将通知和切点进行结合 -->
<aop:advisor advice-ref=“myMethodInterceptor" pointcut="execution(void 
com.itheima.aop.TargetImpl.show())"/>
</aop:config>

```

使用aspect和advisor配置区别如下：

1）配置语法不同：

```
<!-- 使用advisor配置 -->
<aop:config>
<!-- advice-ref:通知Bean的id -->
<aop:advisor advice-ref="advices" pointcut="execution(void 
com.itheima.aop.TargetImpl.show())"/>
</aop:config>
<!-- 使用aspect配置 -->
<aop:config>
<!-- ref:通知Bean的id -->
<aop:aspect ref="advices">
<aop:before method="before" pointcut="execution(void 
com.itheima.aop.TargetImpl.show())"/>
</aop:aspect>
</aop:config>

```

2）通知类的定义要求不同，advisor 需要的通知类需要实现Advice的子功能接口：

```
public class Advices implements MethodBeforeAdvice {
public void before(Method method, Object[] objects, Object o) throws Throwable {
System.out.println("This is before Advice ...");
}
public void afterReturning(Object o, Method method, Object[] objects, Object o1) throws 
Throwable {
System.out.println("This is afterReturn Advice ...");
}}
```



aspect 不需要通知类实现任何接口，在配置的时候指定哪些方法属于哪种通知类型即可，更加灵活方便：

```
public class Advices {
public void before() {
System.out.println("This is before Advice ...");
}
public void afterReturning() {
System.out.println("This is afterReturn Advice ...");
}}

```

3）可配置的切面数量不同：

```
1、一个advisor只能配置一个固定通知和一个切点表达式；
2、一个aspect可以配置多个通知和多个切点表达式任意组合，粒度更细。
```

4）使用场景不同：

```
1、如果通知类型多、允许随意搭配情况下可以使用aspect进行配置；
2、如果通知类型单一、且通知类中通知方法一次性都会使用到的情况下可以使用advisor进行配置；
3、在通知类型已经固定，不用人为指定通知类型时，可以使用advisor进行配置，例如后面要学习的Spring事务
控制的配置；
```

## xml方式AOP原理剖析

# 注解方式AOP基本使用



Spring的AOP也提供了注解方式配置，使用相应的注解替代之前的xml配置，xml配置AOP时，我们主要配置了三
部分：目标类被Spring容器管理、通知类被Spring管理、通知与切点的织入（切面），如下：

```
<!--配置目标-->
<bean id="target" class="com.itheima.aop.TargetImpl"></bean>
<!--配置通知-->
<bean id="advices" class="com.itheima.aop.Advices"></bean>
<!--配置aop-->
<aop:config proxy-target-class="true">
<aop:aspect ref="advices">
<aop:around method="around" pointcut="execution(* com.itheima.aop.*.*(..))"/>
</aop:aspect>
</aop:config>

```



目标类被Spring容器管理、通知类被Spring管理

```
@Component("target")
public class TargetImpl implements Target{
public void show() {
System.out.println("show Target running...");
}
}
@Component
public class AnnoAdvice {
public void around(ProceedingJoinPoint joinPoint) throws Throwable {
System.out.println("环绕前通知...");
joinPoint.proceed();
System.out.println("环绕后通知...");
}
}

```

配置aop，其实配置aop主要就是配置通知类中的哪个方法（通知类型）对应的切点表达式是什么

```
@Component
@Aspect //第一步
public class AnnoAdvice {
@Around("execution(* com.itheima.aop.*.*(..))") //第二步
public void around(ProceedingJoinPoint joinPoint) throws Throwable {
System.out.println("环绕前通知...");
joinPoint.proceed();
System.out.println("环绕后通知...");}}

```

注解@Aspect、@Around需要被Spring解析，所以在Spring核心配置文件中需要配置aspectj的自动代理

```
<aop:aspectj-autoproxy/>
```



如果核心配置使用的是配置类的话，需要配置注解方式的aop自动代理

```
@Configuration
@ComponentScan("com.itheima.aop")
@EnableAspectJAutoProxy //第三步
public class ApplicationContextConfig {
}

```

各种注解方式通知类型

```
//前置通知
@Before("execution(* com.itheima.aop.*.*(..))")
public void before(JoinPoint joinPoint){}
//后置通知
@AfterReturning("execution(* com.itheima.aop.*.*(..))")
public void AfterReturning(JoinPoint joinPoint){}
//环绕通知
@Around("execution(* com.itheima.aop.*.*(..))")
public void around(ProceedingJoinPoint joinPoint) throws Throwable {}
//异常通知
@AfterThrowing("execution(* com.itheima.aop.*.*(..))")
public void AfterThrowing(JoinPoint joinPoint){}
//最终通知
@After("execution(* com.itheima.aop.*.*(..))")
public void After(JoinPoint joinPoint){}

```



切点表达式的抽取，使用一个空方法，将切点表达式标注在空方法上，其他通知方法引用即可

```
@Component
@Aspect
public class AnnoAdvice {
//切点表达式抽取
@Pointcut("execution(* com.itheima.aop.*.*(..))")
public void pointcut(){}
//前置通知
@Before("pointcut()")
public void before(JoinPoint joinPoint){}
//后置通知
@AfterReturning("AnnoAdvice.pointcut()")
public void AfterReturning(JoinPoint joinPoint){}
// ... 省略其他代码 ...
}

```











```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-4.0.xsd">

	<!-- 配置 bean -->
	<bean id="arithmeticCalculator" 
		class="com.atguigu.spring.aop.xml.ArithmeticCalculatorImpl"></bean>

	<!-- 配置切面的 bean. -->
	<bean id="loggingAspect"
		class="com.atguigu.spring.aop.xml.LoggingAspect"></bean>

	<bean id="vlidationAspect"
		class="com.atguigu.spring.aop.xml.VlidationAspect"></bean>

	<!-- 配置 AOP -->
	<aop:config>
		<!-- 配置切点表达式 -->
		<aop:pointcut expression="execution(* com.atguigu.spring.aop.xml.ArithmeticCalculator.*(int, int))" 
			id="pointcut"/>
		<!-- 配置切面及通知 -->
		<aop:aspect ref="loggingAspect" order="2">
			<aop:before method="beforeMethod" pointcut-ref="pointcut"/>
			<aop:after method="afterMethod" pointcut-ref="pointcut"/>
			<aop:after-throwing method="afterThrowing" pointcut-ref="pointcut" throwing="e"/>
			<aop:after-returning method="afterReturning" pointcut-ref="pointcut" returning="result"/>
			<!--  
			<aop:around method="aroundMethod" pointcut-ref="pointcut"/>
			-->
		</aop:aspect>	
		<aop:aspect ref="vlidationAspect" order="1">
			<aop:before method="validateArgs" pointcut-ref="pointcut"/>
		</aop:aspect>
	</aop:config>

</beans>
```

​	1、把通知bean也交给IOC管理
​	2、使用aop:config标签表明开始AOP的配置
​	3、aop:aspect标签表明配置切面
​		属性：id：唯一id
​		      ref：指定通知类的bean的id
​	4、在aop:aspect使用子标签配置通知的类型：aop:before
​		属性：method：用于指定切面类中哪个方法来做这个通知
​		      pointcut：切入点表达式，对哪些连接点做通知
​		      pointcut-ref：引入配置的切点
​			pointcut="execution(public void com.liuhao.test.Test.test())"
​	5、切入点表达式
​		关键字：execution
​		表达式：修饰访问符(省) 
​			返回值（*任意返回值） 
​			包名（..*任意包，不管几级，要考虑级则每一级一个*）
​			.类名（*）
​			.方法名（*）
​			（参数列表（））
​				空的代表没有参数的方法
​				..所有参数结构都可以
​				*所有有参结构
​				基本类型直接写，引用类型，包名.类名，
​		例子：public void com.liuhao.test.Test.test()
​		实际开发中，切入点一般切到业务层里的所有方法
​		切点配置：aop：aspect的子标签aop:pointcut 
​			属性id：切点id
​			    expression：用于指定表达式内容
   			也可以写在aop:config标签下，即所有切面都可以用，但是必须写在切面前面
​	6、几个通知的执行顺序
​	7、环绕通知

**注解AOP**

​	1、在切面类上加@Aspect
​	2、在方法上加通知类型，通知注解@Before("切点方法名()")
​	3、定义一个切点方法，使用@Pointcut（"execution(表达式)"）
​	4、在配置文件里里开启注解AOP的支持
​		<aop:aspect-autoproxy><>
​	5、用于声明通知的注解
​		@Before: 声明该方法为前置通知.相当于xml配置中的<aop:before>标签

​		@AfterReturning: 声明该方法为后置通知.相当于xml配置中的<aop:after-returning>标签

​		@AfterThrowing: 声明该方法为异常通知.相当于xml配置中的<aop:after-throwing>标签
​		
​		@After: 声明该方法为最终通知.相当于xml配置中的<aop:after>标签
​		@Around: 声明该方法为环绕通知.相当于xml配置中的<aop:around>标签
   7、不使用xml，全注解AOP
​		只需要在主配置类上加@EnableAspectJAutoProxy
​	8、使用注解配置AOP的bug
​		在使用注解配置AOP时,会出现一个bug. 四个通知的调用顺序依次是:前置通知,最终通知,后置通知. 这会导致一些资源在执行最终通	知时提前被释放掉了,而执行后置通知时就会出错.	

# **四、JDBC**

1、JdbcTemplate

```java
//准备数据源，spring的内置数据源
		DriverManagerDataSource ds = new DriverManagerDataSource()
		ds.setDriverClassName("驱动全限定类名")
		ds.setUrl("url")
		ds.setUsername("username")
		ds.setPassword("password")
//创建对象
		JdbcTemplate jt = new JdbcTemplate();
//设置数据源
		jt.setDataSource(ds)
//执行操作
		jt.execute("sql")
```

# **五、Spring事务控制**

JavaEE 体系进行分层开发,事务处理位于业务层,Spring提供了分层设计业务层的事务处理解决方案

**Spring中事务控制的API**

**1、PlatformTransactionManager接口**

是Spring提供的事务管理器,它提供了操作事务的方法如下:

```java
TransactionStatus getTransaction(TransactionDefinition definition): 获得事务状态信息
void commit(TransactionStatus status): 提交事务
void rollback(TransactionStatus status): 回滚事务

// 在实际开发中我们使用其实现类:使用SpringJDBC或iBatis进行持久化数据时使用
org.springframework.jdbc.datasource.DataSourceTransactionManager

2、TransactionDefinition: 事务定义信息对象,提供查询事务定义的方法如下:
1、String getName(): 获取事务对象名称
2、int getIsolationLevel(): 获取事务隔离级别,设置两个事务之间的数据可见性
1、事务的隔离级别由弱到强,依次有如下五种:
1、ISOLATION_DEFAULT: Spring事务管理的的默认级别,使用数据库默认的事务隔离级别.
2、ISOLATION_READ_UNCOMMITTED: 读未提交.事务中的修改,即使没有提交,其他事务也可以看得到.会导致脏读,不					可重复读,幻读
3、ISOLATION_READ_COMMITTED: 读已提交(Oracle数据库默认隔离级别).一个事务不会读到其它并行事务已修改但					未提交的数据.避免了脏读,但会导致不可重复读,幻读
4、ISOLATION_REPEATABLE_READ: 重复读(Mysql数据库默认的隔离级别).一个事务不会读到其它并行事务已修改且					已提交的数据,(只有当该事务提交之后才会看到其他事务提交的修改).避免了脏读,不可重复读,但会导致					幻读
5、ISOLATION_SERIALIZABLE: 串行化事务串行执行,一个时刻只能有一个事务被执行避免了脏读,不可重复读,幻读
2、事务的安全隐有如下三种,他们可以通过设置合理的隔离级别来避免:
1、脏读: 一个事务读到另外一个事务还未提交(可能被回滚)的脏数据
2、不可重复读: 一个事务执行期间另一事务提交修改,导致第一个事务前后两次查询结果不一致
3、幻读: 一个事务执行期间另一事务提交添加数据,导致第一个事务前后两次查询结果到的数据条数不同.
3、getPropagationBehavior(): 获取事务传播行为,设置新事务是否事务以及是否使用当前事务.
我们通常使用的是前两种: REQUIRED和SUPPORTS.事务传播行为如下:
1、REQUIRED: Spring默认事务传播行为. 若当前没有事务,就新建一个事务;若当前已经存在一个事务中,加入到这					个事务中.增删改查操作均可用

2、SUPPORTS: 若当前没有事务,就不使用事务;若当前已经存在一个事务中,加入到这个事务中.查询操作可用

3、MANDATORY: 使用当前的事务,若当前没有事务,就抛出异常

4、REQUERS_NEW: 新建事务,若当前在事务中,把当前事务挂起

5、NOT_SUPPORTED: 以非事务方式执行操作,若当前存在事务,就把当前事务挂起

6、NEVER:以非事务方式运行,若当前存在事务,抛出异常

7、NESTED:若当前存在事务,则在嵌套事务内执行;若当前没有事务,则执行REQUIRED类似的操作
4、int getTimeout(): 获取事务超时时间. Spring默认设置事务的超时时间为-1,表示永不超时.
5、boolean isReadOnly(): 获取事务是否只读. Spring默认设置为false,建议查询操作中设置为true
3、TransactionStatus: 事务状态信息对象,提供操作事务状态的方法如下：
1、void flush(): 刷新事务

2、boolean hasSavepoint(): 查询是否存在存储点

3、boolean isCompleted(): 查询事务是否完成

4、boolean isNewTransaction(): 查询是否是新事务

5、boolean isRollbackOnly(): 查询事务是否回滚

6、void setRollbackOnly(): 设置事务回滚
```

3、使用Spring进行事务控制
	1、Spring配置式事务控制
	        1、项目准备
			1、导入jar包
			2、创建bean.xml并导入约束
			3、准备数据库表和实体类
	2、使用xml配置事务控制：Spring的配置式事务控制本质上是基于AOP的,因此下面我们在bean.xml中配置事务管理器			PlatformTransactionManager对象并将其与AOP配置建立联系.
			1、配置事务管理器并注入数据源

```
<!--向Spring容器中注入一个事务管理器-->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <!--注入数据源-->
    <property name="dataSource" ref="dataSource" />
</bean>	
```

​			2、配置事务通知并在通知中配置其属性
​				1、使用<tx:advice>标签声明事务配置,其属性如下:
​					1、id: 事务配置的id
​					2、transaction-manager: 该配置对应的事务管理器
​				2、在<tx:advice>标签内包含<tx:attributes>标签表示配置事务属性
​				3、在<tx:attributes>标签内包含<tx:method>标签,为切面上的方法配置事务属性,<tx:method>标签的属性如下:
​						name: 拦截到的方法,可以使用通配符*

2、配置事务通知并在通知中配置其属性
				1、使用<tx:advice>标签声明事务配置,其属性如下:
					1、id: 事务配置的id
					2、transaction-manager: 该配置对应的事务管理器
				2、在<tx:advice>标签内包含<tx:attributes>标签表示配置事务属性
				3、在<tx:attributes>标签内包含<tx:method>标签,为切面上的方法配置事务属性,<tx:method>标签的属性如下:
						name: 拦截到的方法,可以使用通配符*

```
					isolation: 事务的隔离级别,Spring默认使用数据库的事务隔离级别

					propagation: 事务的传播行为,默认为REQUIRED.增删改方法应该用REQUIRED,查询方法							可以使用SUPPORTS

					read-only: 事务是否为只读事务,默认为false.增删改方法应该用false,查询方法可以							使用
					true
```

timeout: 指定超时时间,默认值为-1,表示永不超时

```
					rollback-for: 指定一个异常,当发生该异常时,事务回滚;发生其他异常时,事务不回滚							.无默认值,表示发生任何异常都回滚

					no-rollback-for: 指定一个异常,当发生该异常时,事务不回滚;发生其他异常时,事务							回滚.无默认值,表示发生任何异常都回滚
```

<!-- 配置事务的通知-->
<tx:advice id="txAdvice" transaction-manager="transactionManager">
    <tx:attributes>
        <!--匹配所有方法-->
        <tx:method name="*" propagation="REQUIRED" read-only="false" rollback-for="" no-rollback-for=""/>
        

```
    <!--匹配所有查询方法-->
    <tx:method name="find*" propagation="SUPPORTS" read-only="true"/>
    
    <!--第二个<tx:method>匹配得更精确,所以对所有查询方法,匹配第二个事务管理配置;对其他查询方法,匹配第一个事务管理配置-->
</tx:attributes>
```

</tx:advice>

```
		3、配置AOP并为事务管理器事务管理器指定切入点
```

<!--配置AOP-->
<aop:config>
    <!-- 配置切入点表达式-->
    <aop:pointcut id="pt1" expression="execution(* cn,maoritian.service.impl.*.*(..))"></aop:pointcut>
    <!--为事务通知指定切入点表达式-->
    <aop:advisor advice-ref="txAdvice" pointcut-ref="pt1"/>
</aop:config>

```
	3、使用半注解配置事务控制
		1、配置事务管理器并注入数据源
```

<!--向Spring容器中注入一个事务管理器-->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <!--注入数据源-->
    <property name="dataSource" ref="dataSource"></property>
</bean>

```
		2、在业务层使用@Transactional注解,其参数与<tx:method>的属性一致.
			1、该注解可以加在接口,类或方法上，三个位置上的注解优先级依次升高
				1、对接口加上@Transactional注解,表示对该接口的所有实现类进行事务控制
				2、对类加上@Transactional注解,表示对类中的所有方法进行事务控制
				3、对具体某一方法加以@Transactional注解,表示对具体方法进行事务控制
```

// 业务层实现类,事务控制应在此层
@Service("accountService")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)    // 读写型事务配置
public class AccountServiceImpl implements IAccountService {

```
@Autowired
private IAccountDao accountDao;

@Override
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true) // 只读型事务配置,会覆盖上面对类的读写型事务配置 
public Account findAccountById(Integer accountId) {
    return accountDao.findAccountById(accountId);
}
 
@Override
public void transfer(String sourceName, String targetName, Float money) {
    // 转账操作的实现...
}
```

}

```
	4、使用纯注解式事务配置
		不使用xml配置事务,就要在cn.maoritian.config包下新建一个事务管理配置类TransactionConfig,对其加上			@EnableTransactionManagement注解以开启事务控制.
```

**示例**

**1、事务控制配置类TransactionConfig类**   

```java
@Configuration                  
@EnableTransactionManagement    // 开启事务控制
public class TransactionConfig {
    
    // 创建事务管理器对象
    @Bean(name="transactionManager")
	
    public PlatformTransactionManager createTransactionManager(@Autowired DataSource dataSource){ 
        return new DataSourceTransactionManager(dataSource);
        }	
}
```

**2、JDBC配置类JdbcConfig类**

```java
@Configuration                                  
@PropertySource("classpath:jdbcConfig.properties")  
public class JdbcConfig {

    @Value("${jdbc.driver}")    
	private String driver;
    
    @Value("${jdbc.url}")
	private String url;
    
    @Value("${jdbc.username}")
	private String username;
    
    @Value("${jdbc.password}")  
	private String password;
    
    // 创建JdbcTemplate对象
	@Bean(name="jdbcTemplate")
	@Scope("prototype") 
	public JdbcTemplate createJdbcTemplate(@Autowired DataSource dataSource){
    	return new JdbcTemplate(dataSource);
	}
    
    // 创建数据源对象
	@Bean(name="dataSource")
	public DataSource createDataSource(){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
	}
}
```

 **3、Spring主配置类SpringConfig**

```java
@Configuration
@ComponentScan("cn.maoritian")
@Import({JdbcConfig.class, TransactionConfig.class})
public class SpringConfig {}
```

**4、创建数据库表如下**

```sql
create database learnSpringTransaction; --创建数据库
use learnSpringTransaction;
-- 创建表
create table account(
id int primary key auto_increment,
name varchar(40),
money float
)charset=utf8;
```

**5、java实体类**

```java
public class Account implements Serializable {
    
	private Integer id;

	private String name;

	private Float money;
    
    // 省略setter getter
	public String toString() {
        return "Account{id=" + id + ", name='" + name + '\'' + ", money=" + money + '}'; 
    }   
```

  **6、Service层接口** 

```java
//  业务层接口
public interface IAccountService {
    // 根据id查询账户信息
    Account findAccountById(Integer accountId);
    // 转账
    void transfer(String sourceName,String targetName,Float money);
}
```

**7、Service层实现类**

```java
// 业务层实现类,事务控制应在此层
@Service("accountService")
public class AccountServiceImpl implements IAccountService {
    
    @Autowired
    private IAccountDao accountDao;

    @Override
    public Account findAccountById(Integer accountId) {
        return accountDao.findAccountById(accountId);
    }

    @Override
    public void transfer(String sourceName, String targetName, Float money) {
        System.out.println("start transfer");
        // 1.根据名称查询转出账户
        Account source = accountDao.findAccountByName(sourceName);
        // 2.根据名称查询转入账户
        Account target = accountDao.findAccountByName(targetName);
        // 3.转出账户减钱
        source.setMoney(source.getMoney() - money);
        // 4.转入账户加钱
        target.setMoney(target.getMoney() + money);
        // 5.更新转出账户
        accountDao.updateAccount(source);

        int i = 1 / 0;

        // 6.更新转入账户
        accountDao.updateAccount(target);
    }
}
```

**8、Dao层接口**    

```java
// 持久层接口
public interface IAccountDao {
    
    // 根据Id查询账户
    Account findAccountById(Integer accountId);	
    // 根据名称查询账户
    Account findAccountByName(String accountName);

    // 更新账户
    void updateAccount(Account account);
```

**9、Dao层实现类**

```java
//持久层实现类
@Repository("accountDao")
public class AccountDaoImpl implements IAccountDao {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 根据id查询账户
    @Override
    public Account findAccountById(Integer accountId) {
        List<Account> accounts = jdbcTemplate.query("select * from account where id = ?", new BeanPropertyRowMapper<Account>(Account.class), accountId);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    // 根据用户名查询账户
    @Override
    public Account findAccountByName(String accountName) {
        List<Account> accounts = jdbcTemplate.query("select * from account where name = ?", new BeanPropertyRowMapper<Account>(Account.class), accountName);
        if (accounts.isEmpty()) {
            return null;
        }
        if (accounts.size() > 1) {
            throw new RuntimeException("结果集不唯一");
        }
        return accounts.get(0);
    }

    // 更新账户
    @Override
    public void updateAccount(Account account) {
        jdbcTemplate.update("update account set name=?,money=? where id=?", account.getName(), account.getMoney(), account.getId());
    }
```

**10、在bean.xml中配置数据源以及要扫描的包**

```xml
<!--配置 创建Spring容器时要扫描的包-->
<context:component-scan base-package="com.itheima"></context:component-scan>

<!--配置 数据源-->
<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="com.mysql.jdbc.Driver"></property>
    <property name="url" value="jdbc:mysql://localhost:3306/spring_day02"></property>
    <property name="username" value="root"></property>
    <property name="password" value="1234"></property>
</bean>    

<!--配置 JdbcTemplate-->
<bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
    <property name="dataSource" ref="dataSource"></property>
</bean>
```





