



**AOP 相关术语**

​	连接点：即拦截到的方法，spring 只支持方法类型的连接点
​	切点：指我们要对哪些连接点进行拦截的定义
​	通知：即拦截到连接点后要做的事情
​	通知的类型：前置通知，后置通知，异常通知，最终通知，环绕通知

AOP 思想的实现方案：

​		动态代理技术，在运行期间，对目标对象的方法进行增强，代理对象同名方法内可以执行原有逻辑的同时嵌入执行其他增强逻辑或其他对象的方法

# **xml 配置**

步骤：

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
<bean id="myAdvice" class="com.itheima.advice.MyAdvice"/>
```

3、配置切点表达式（哪些方法被增强）
4、配置织入（切点被哪些通知方法增强，是前置增强还是后置增强）

```xml
<aop:config>
    <!--配置切点表达式,对哪些方法进行增强-->
    <aop:pointcut id="myPointcut" 
              expression="execution(void com.liuhao.service.impl.UserServiceImpl.show1())"/>
    <!--切面=切点+通知-->
    <aop:aspect ref="myAdvice">
        <!--指定前置通知方法是beforeAdvice-->
        <aop:before method="beforeAdvice" pointcut-ref="myPointcut"/>
        <!--指定后置通知方法是afterAdvice-->
        <aop:after-returning method="afterAdvice" pointcut-ref="myPointcut"/>
    </aop:aspect>
</aop:config>
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

## 切点表达式

切点表达式是配置要对哪些连接点（哪些类的哪些方法）进行通知的增强，语法如下：

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

```java
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

## 通知

AspectJ的通知由以下五种类型

```
前置通知 < aop:before > 目标方法执行之前执行
后置通知 < aop:after-returning > 目标方法执行之后执行，目标方法异常时，不在执行
环绕通知 < aop:around > 目标方法执行前后执行，目标方法异常时，环绕后方法不在执行
异常通知 < aop:after-throwing > 目标方法抛出异常时执行
最终通知 < aop:after > 不管目标方法是否有异常，最终都会执行

```

环绕通知

```java
public void around(ProceedingJoinPoint joinPoint) throws Throwable {
    //环绕前
    System.out.println("环绕前通知");
    //目标方法
    joinPoint.proceed();
    ///环绕后
    System.out.println("环绕后通知");
}
```

```xml
<aop:around method="around" pointcut-ref="myPointcut"/>
```

异常通知

​		当目标方法抛出异常时，异常通知方法执行，且后置通知和环绕后通知不在执行

```java
public void afterThrowing(){
	System.out.println("目标方法抛出异常了，后置通知和环绕后通知不在执行");
}
```

```xml
<aop:after-throwing method="afterThrowing" pointcut-ref="myPointcut"/>
```

最终通知

​		类似异常捕获中的finally，不管目标方法有没有异常，最终都会执行的通知

```java
public void after(){
	System.out.println("不管目标方法有无异常，我都会执行");
}
```

```xml
<aop:after method="after" pointcut-ref="myPointcut"/>
```

通知方法在被调用时，Spring可以为其传递一些必要的参数

```
JoinPoint 连接点对象，任何通知都可使用，可以获得当前目标对象、目标方法参数等信息
ProceedingJoinPoint JoinPoint子类对象，主要是在环绕通知中执行proceed()，进而执行目标方法
Throwable 异常对象，使用在异常通知中，需要在配置文件中指出异常对象名称
```

JoinPoint 对象

```java
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

```java
public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    System.out.println(joinPoint.getArgs());//获得目标方法的参数
    System.out.println(joinPoint.getTarget());//获得目标对象
    System.out.println(joinPoint.getStaticPart());//获得精确的切点表达式信息
    Object result = joinPoint.proceed();//执行目标方法
    return result;//返回目标方法返回值
}
```

Throwable对象

```java
public void afterThrowing(JoinPoint joinPoint,Throwable th){
    //获得异常信息
    System.out.println("异常对象是："+th+"异常信息是："+th.getMessage());
}
```

```xml
<aop:after-throwing method="afterThrowing" pointcut-ref="myPointcut" throwing="th"/>
```

AOP的另一种配置方式，该方式需要通知类实现Advice的子功能接口

```java
public interface Advice {
}
```

例如：通知类实现了前置通知和后置通知接口

```java
public class Advices implements MethodBeforeAdvice, AfterReturningAdvice {
    
    public void before(Method method, Object[] objects, Object o) throws Throwable {
    	System.out.println("This is before Advice ...");
    }
    
    public void afterReturning(Object o, Method method, Object[] objects, Object o1) throws 
    Throwable {
    	System.out.println("This is afterReturn Advice ...");
    }
}
```

切面使用advisor标签配置

```xml
<aop:config>
    <!-- 将通知和切点进行结合 -->
    <aop:advisor advice-ref="advices" pointcut="execution(void 
    com.itheima.aop.TargetImpl.show())"/>
</aop:config>
```

又例如：通知类实现了方法拦截器接口

```java
public class MyMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
    	System.out.println("前置逻辑功能...");
    	//执行目标方法
		Object invoke = methodInvocation.getMethod()
            .invoke(methodInvocation.getThis(),methodInvocation.getArguments());
		System.out.println("后置逻辑功能...");
		return invoke;
    }
}
```

切面使用advisor标签配置

```xml
<aop:config>
    <!-- 将通知和切点进行结合 -->
    <aop:advisor advice-ref=“myMethodInterceptor" pointcut="execution(void 
    com.itheima.aop.TargetImpl.show())"/>
</aop:config>
```

使用aspect和advisor配置区别如下：

1）配置语法不同：

```xml
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

```java
public class Advices implements MethodBeforeAdvice {
    
    public void before(Method method, Object[] objects, Object o) throws Throwable {
    	System.out.println("This is before Advice ...");
    }
    
    public void afterReturning(Object o, Method method, Object[] objects, Object o1) throws 
    Throwable {
    	System.out.println("This is afterReturn Advice ...");
	}
}
```

aspect 不需要通知类实现任何接口，在配置的时候指定哪些方法属于哪种通知类型即可，更加灵活方便：

```java
public class Advices {
    public void before() {
    	System.out.println("This is before Advice ...");
    }
    
    public void afterReturning() {
    	System.out.println("This is afterReturn Advice ...");
    }
}

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

## 

# 注解配置

​		Spring 的 AOP 也提供了注解方式配置，使用相应的注解替代之前的 xml 配置，xml 配置 AOP 时，我们主要配置了三部分：目标类被Spring容器管理、通知类被Spring管理、通知与切点的织入（切面），如下：

```xml
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

```java
@Component("target")
public class TargetImpl implements Target{
    public void show() {
    	System.out.println("show Target running...");
    }
}

@Component
@Aspect // 第一步
public class AnnoAdvice {
	@Around("execution(* com.itheima.aop.*.*(..))") //第二步
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕前通知...");
        joinPoint.proceed();
        System.out.println("环绕后通知...");
    }
}

```

注解@Aspect、@Around需要被Spring解析，所以在Spring核心配置文件中需要配置aspectj的自动代理

```xml
<aop:aspectj-autoproxy/>
```

如果核心配置使用的是配置类的话，需要配置注解方式的aop自动代理

```java
@Configuration
@ComponentScan("com.itheima.aop")
@EnableAspectJAutoProxy //第三步
public class ApplicationContextConfig {
    
}

```

各种注解方式通知类型

```java
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

```java
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

