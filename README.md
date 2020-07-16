## 基于 Feign 的客户端装配器
## 一、项目职能
- 基于中台契约自定义装配`FeignClient`
- 基于`Hystrix`的熔断、限流
- 基于`Feign`的默认降级策略
- 基于`Feign`的超时、重试
- `XRC`传递、请求鉴权配置（自行扩展）
- 自定义请求、响应日志打印样式
- 提供扩展点

## 二、执行流程
#### 1、扫描标有`@ApiContract`注解的元数据<br/>
#### 2、通过`ApiClientFactoryBean`动态构造`Bean`的定义<br/>
#### 3、在`ApiClientFactoryBean`的`getObject()`方法中进行自动装配的核心流程<br/>
#### 4、通过注解的元数据，给`Bean`的定义设置其它属性 <br/>
#### 5、注册`Bean`的定义

## 三、接入
#### 1、引入 Jar
```
// API 客户端装配器
compile("com.qimok:api-client-assembler:$apiClientAssemblerVersion")
// 如引入用户中台契约
compile("com.xxx:user-mid-contract:$userMidContractversion")
```
#### 2、使用
```
@Autowired(required = false)                              
public ServiceContract serviceClient;
```

## 四、配置相关

#### 1、FeignClient 底层 HTTP 实现
完全由服务消费方自己去配置，如果没有配置，则默认使用`openfeign`中的`HttpClient`
> 服务消费方配置举例(具体如何配置，请自行搜索)：<br/>
  feign.httpclient.enabled=false<br/>
  feign.okhttp.enabled=true<br/>
  feign.httpclient.max-connections-per-route=30<br/>
  feign.httpclient.max-connections=200<br/>
  feign.httpclient.disable-ssl-validation=false<br/>
  
#### 2、请求拦截器
一共有两类请求拦截器：鉴权拦截器、XRC 拦截器（自行扩展） <br/>
> 配置举例：<br/>
feign.auth.config.ServiceClient.authMod=reaper<br/>

#### 3、熔断、线程池配置(只要开启熔断，每个契约对应的`Client`都要进行熔断和线程池的配置)
> 启用 Hystrix 配置：<br/>
feign.hystrix.enabled=true <br/>
熔断配置举例：<br/>
feign.hystrix.config.ServiceClient.groupKey=ServiceClient <br/>
feign.hystrix.config.ServiceClient.commandKey=ServiceClient <br/>
feign.hystrix.config.ServiceClient.circuitBreakerEnabled=true <br/>
feign.hystrix.config.ServiceClient.executionTimeoutInMilliseconds=20000 <br/>
...<br/>
线程池配置举例：<br/>
feign.thread.config.ServiceClient.groupKey=ServiceClient <br/>
feign.thread.config.ServiceClient.coreSize=300 <br/>
feign.thread.config.ServiceClient.maximumSize=400 <br/>
feign.thread.config.ServiceClient.maxQueueSize=80 <br/>
...

#### 4、超时配置
如果服务消费方没有自定义配置，则走默认配置(可参考`RequestOptionsProperties#RequestOptionsConfiguration`)
> 配置举例：<br/>
feign.request.option.config.ServiceClient.connectTimeoutMillis=5000 <br/>
feign.request.option.config.ServiceClient.readTimeoutMillis=5000 <br/>
feign.request.option.config.ServiceClient.follow-redirects=false <br/>
...

#### 5、重试配置
如果服务消费方没有自定义配置，则走默认配置(默认不重试)。当超时时，如果配置了重试策略，则会触发重试
> 配置举例：<br/>
feign.retryer.config.ServiceClient.period=100 <br/>
feign.retryer.config.ServiceClient.max-period=1000 <br/>
feign.retryer.config.ServiceClient.max-attempts=2 <br/>
...

#### 6、日志配置
自定义请求、响应日志打印样式

#### 7、降级策略（基于 Feign）
目前只实现了一套默认的降级策略，触发降级时，返回`error`

#### 8、配置扩展
当需要接入的中台契约接口类(每个契约接口类对应一个`Client`)特别多的时候，配置也就特别多，<br/>
为了减少配置，并保证某些配置可以共用(`注意是共用，而不是复用`)，于是，对以上 3、4、5 的配置进行了扩展。<br/>
>熔断配置举例：<br/>
feign.hystrix.config.`ServiceClient1-ServiceClient2-ServiceClient3`.groupKey=ServiceClient <br/>
feign.hystrix.config.`ServiceClient1-ServiceClient2-ServiceClient3`.commandKey=ServiceClient <br/>
feign.hystrix.config.`ServiceClient1-ServiceClient2-ServiceClient3`.circuitBreakerEnabled=true <br/>
feign.hystrix.config.`ServiceClient1-ServiceClient2-ServiceClient3`.executionTimeoutInMilliseconds=20000 <br/>
线程池配置举例：<br/>
feign.thread.config.`ServiceClient1-ServiceClient2-ServiceClient3`.groupKey=ServiceClient <br/>
feign.thread.config.`ServiceClient1-ServiceClient2-ServiceClient3`.coreSize=300 <br/>
feign.thread.config.`ServiceClient1-ServiceClient2-ServiceClient3`.maximumSize=400 <br/>
feign.thread.config.`ServiceClient1-ServiceClient2-ServiceClient3`.maxQueueSize=80 <br/>
超时配置举例：<br/>
feign.request.option.config.`ServiceClient1-ServiceClient2-ServiceClient3`.connectTimeoutMillis=5000 <br/>
feign.request.option.config.`ServiceClient1-ServiceClient2-ServiceClient3`.readTimeoutMillis=5000 <br/>
feign.request.option.config.`ServiceClient1-ServiceClient2-ServiceClient3`.follow-redirects=false <br/>
重试配置举例：<br/>
feign.retryer.config.`ServiceClient1-ServiceClient2-ServiceClient3`.period=100 <br/>
feign.retryer.config.`ServiceClient1-ServiceClient2-ServiceClient3`.max-period=1000 <br/>
feign.retryer.config.`ServiceClient1-ServiceClient2-ServiceClient3`.max-attempts=2 <br/>

#### 9、配置注意点：
- 所有数字相关的配置假如出现冲突，优先使用数字最小的配置<br/>
举例，假如熔断配置中的`executionTimeoutInMilliseconds`设置为1秒，重试配置中的`period`设置为2秒，则不会触发重试。
- 以上配置严格区分大小写
- 契约的接口类命名是`ServiceContract`，但是进行如上配置的时候，应该将`Contract`替换为`Client`，否则无法处理(目的：将契约和`Client`的定义区分开)
- 针对`8、配置扩展`中的配置，切记同一个`Client`不能配置多个不同的配置<br/>
>举例：<br/>
1.同一条配置重复<br/>
feign.hystrix.config.`ServiceClient1-ServiceClient1`.groupKey=ServiceClient (不允许)<br/>
2.不同条配置重复<br/>
feign.request.option.config.`ServiceClient1-ServiceClient2-ServiceClient3`.connectTimeoutMillis=5000
feign.request.option.config.`ServiceClient1`.connectTimeoutMillis=5000

