## 一、前言

我们在项目开发中，如果一个项目需要加载的资源比较多，每一次重启就需要好几分钟，每一次更新完项目，就需要重新启动，这样会消耗很多时间，我们单位外包给其他公司开发了一套系统，每一次根据实际需求修改项目的时候，都需要启动很长一段时间，有些时候，出去上个厕所回来才启动完成，真的很煎熬，在项目中使用**spring-boot-devtools**,真的会更高效！

## 二、热部署的实现原理

devtools主要是有两个Class Loader:

- 第一个Class Loader 是用来加载不会改变的类，比如第三方的代码
- 第二个Class Loader 叫做restart class loader，主要加载会改变的类，也就是我们自己的代码

如果代码发生改变，那么restart class loader就会被替换掉，我们可以在后面的例子中看到

## 三、本地使用devtools

### 3.1 依赖

```java
        <!--devtools-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
```

### 3.2 IDEA配置



### 3.3 Coding

```java
@RestController
public class DevController {

    @GetMapping("/dev")
    public HashMap<String,Object> dev(){
        Integer code = 200;
        String message = "success";
      
        //获取restart class loader的全限定类名
        String data = this.getClass().getClassLoader().toString();

        HashMap<String,Object> result = new HashMap<>();
        result.put("code",code);
        result.put("message",message);
        result.put("data",data);

        return result;
    }

}
```

我们主要可以通过查看**restart class loader**的全限定类名，查看数据改变，class loader是否会被替换

- 测试一：启动项目 访问/dev

```json
{
  "code":200,
  "data":"org.springframework.boot.devtools.restart.classloader.RestartClassLoader@5a5b30be",
  "message":"success"
}
```

我们可以看到 **org.springframework.boot.devtools.restart.classloader.RestartClassLoader@5a5b30be**

- 测试二：将message修改为ok

```json
{
  "code":200,
  "data":"org.springframework.boot.devtools.restart.classloader.RestartClassLoader@275fd8bf",
  "message":"ok"
}
```

我们可以看到class loader变成了 **org.springframework.boot.devtools.restart.classloader.RestartClassLoader@275fd8bf**，说明class loader已经不是之前的，变成了一个新的class loader

## 四、远程调试

devtools不仅仅支持本地热部署，还支持远程热部署，我们把项目运行到服务器上，然后执行热部署操作

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <version>2.3.7.RELEASE</version>
    <configuration>
        <mainClass>com.yangzinan.dev.SpringbootDevtoolsApplication</mainClass>
        <!--这个配置，表示打包时不应该配出devtools-->
        <excludeDevtools>false</excludeDevtools>
    </configuration>
</plugin>
```

### 打包部署

>  打包之前，我们需要设置远程访问的密码

```bash
application.properties

#远程访问密码
spring.devtools.remote.secret=mic123
```

> 上传springboot项目到服务器



>  构建docker镜像

```bash
#创建dockerfile
vi Dockerfile

#输入以下内容
FROM hub.c.163.com/xbingo/jdk8
ADD ./springboot-devtools-0.0.1-SNAPSHOT.jar  /springboot.jar
EXPOSE 8080
CMD ["java","-jar","/springboot.jar"]

#构建镜像,注意：最后有个英文状态下的句号
docker build -t springboot-dev:1.0 .

#保存后启动项目
docker run -d -p 8080:8080 --name springboot-dev springboot-dev:1.0

```

> IDEA配置

新建一个启动类，需要配置一下几个内容：


> 使用RemoteApplication启动项目

```bash
2021-04-15 21:34:40.200  WARN 1698 --- [           main] o.s.b.d.r.c.RemoteClientConfiguration    : The connection to http://192.168.2.128:8080 is insecure. You should use a URL starting with 'https://'.
2021-04-15 21:34:40.213  INFO 1698 --- [           main] o.s.b.d.a.OptionalLiveReloadServer       : LiveReload server is running on port 35729
```

> 测试

- 首先，我们先看一下访问http://ip:port/dev展示的内容

```json
{
  "code":200,
  "data":"org.springframework.boot.devtools.restart.classloader.RestartClassLoader@2c59ca75",
  "message":"success"
}
```

- 然后我们修改message为fail，最后，进行build

```java
@RestController
public class DevController {

    @GetMapping("/dev")
    public HashMap<String,Object> dev(){
        Integer code = 200;
        String message = "fail";
        String data = this.getClass().getClassLoader().toString();

        HashMap<String,Object> result = new HashMap<>();
        result.put("code",code);
        result.put("message",message);
        result.put("data",data);

        return result;
    }

}
```


我们再次访问，看一下返回的json有没有改变

```json
{
  "code":200,
  "data":"org.springframework.boot.devtools.restart.classloader.RestartClassLoader@5300aebf",
  "message":"fail"
}
```

message已经被成功修改



今天，主要讲的内容是devtools，但是在项目中，还穿插着一些内容，比如：Docker镜像构建，今天到此结束

晚安，明天见！
