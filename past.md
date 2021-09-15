# 카프카 연습

<br/>

## 중복이나 누락 없이 소재 생성을 온전히 전달하려면?
- 프로듀서 -> 브로커로 **정확히 한 번** 이벤트를 보내야 하고
- 브로커 -> 컨슈머 -> ES로 **정확히 한 번** 등록이 되어야 한다.
이에 대해 하나씩 살펴보도록 하자.  

<br/>


## 1. 프로듀서 -> 브로커로 정확히 한 번 이벤트를 보내려면?
카프카 프로듀서의 멱등성 옵션을 활용하면 된다.  
application.yaml에서, 프로듀서에  
```yaml
spring:
  kafka:
    producer:
      enable.idempotence: true
```
로 설정하게 되면, 브로커로 정확히 한 번 전달이 보장된다.

<br/>


## 2. 브로커 -> 컨슈머 -> ES로 정확히 한 번 저장이 되려면?
겹칠 수 없는 값으로 고유의 아이디를 만들어 보내주면 된다.    
응용하기 나름이겠지만, 겹쳐서 절대 중복이 되면 안되는 값으로 키를 만들어 보내주는 식이다.  
이 프로젝트의 경우,  
![image](#)  
와 같이 입력을 받는데, 이름과 선호색상이 겹치게 하고 싶지 않다면    
```java
sha256(map.getColorName() + ":" + map.getUserName());
```
와 같은 단방향 암호화를 적용하면 될 것이다.  
이를 unique 옵션이 걸려있는(_id 등등) 칼럼에 저장하게 된다면,  
저장 시 중복이 있다면 저절로 튕겨져 나갈 것이다.  

따라서 다음과 같은 식이다.  
```java
@KafkaListener(topics = "brandon-test", groupId = "zicobar")
@Transactional
public void consumeMessage(String msg, Consumer<String, String> consumer) throws NoSuchAlgorithmException {
    // ObjectMapper 써도 무방합니다.
    Gson gson = new Gson();
    UserEventVO map = gson.fromJson(msg, UserEventVO.class);
 
    Test test = Test.builder()
            ._id(sha256(map.getColorName() + ":" + map.getUserName()))
            .timestamp(map.getTimestamp())
            .colorName(map.getColorName())
            .userAgent(map.getUserAgent())
            .userName(map.getUserName())
            .build();
 
 
    try {
        testRepository.save(test);
    } catch (Exception e) {
        System.out.print("중복");
    }
 
    // 수동 커밋
    consumer.commitAsync();
 
}
```

ref
[블로그1](https://sungjk.github.io/2021/01/10/kafka-consumer.html)  
[블로그2](https://medium.com/@shesh.soft/kafka-idempotent-producer-and-consumer-25c52402ceb9)  
[교재](http://www.yes24.com/Product/Goods/99122569)  

<br/>


---

# Spring Boot Kafka
컨슈머를 멀티 스레드로 운영하려면 : concurrency 옵션 하나로 가능.
## 프로듀서
KafkaTemplate 사용. 이는 ProducerFactory를 통해 생성할 수도 있음.
1. 기본 KafkaTemplate을 사용한다
2. 직접 ProducerFactory를 통해 KafkaTemplate을 생성해 사용한다.

### 기본 KafkaTemplate
```java

@SpringBootApplication
public class SpringProducerApplication implements CommandLineRunner {
    private static String TOPIC_NAME = "test";
    
    // 스프링 카프카에서 알아서 주입해줌.
    @Autowired
    private KafkaTemplate<Integer, String> template;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SpringProducerApplication.class);
        app.run(args);
    }

    @Override
    public void run(String... args) {
        for (int i = 0; i < 10; i++) {
            template.send(TOPIC_NAME, "test" + i);
        }
        System.exit(0);
    }
}
```

### 커스텀 KafkaTemplate
각기 다른 클러스터로 전송하는 카프카 프로듀서를 동시에 사용하고 싶다면, 사용할 수 있지만
우리는 클러스터 하나만 사용할 것이기 때문에 넘어가도 무방.

## 컨슈머
똑같이 두 가지 방식 중 하나를 선택할 수 있음.
- 기본 리스너 컨테이너를 사용하거나
- 컨테이너팩토리로 직접 리스너를 만들거나.

두 가지 타입의 리스너가 있음.
- MessageListener : 한 번에 1개의 레코드를 처리함. (기본)
- BatchMessageListener : 한 번에 여러개의 레코드를 처리함.

이를 파생한 6가지의 리스너가 있음.
- AcknowledgingMessageListener
- ConsumerAwareMessageListener
- AcknowledgingConsumerAwareMessageListener
- BatchAcknowledgingMessageListener
- BatchConsumerAwareMessageListener
- BatchAcknowledgingConsumerAwareMessageListener

(아직 이해는 안됨)  
- 매뉴얼 커밋은 Acknowledging이 붙은 리스너를,
- KafkaConsumer 인스턴스에 직접 접근하려면 ConsumerAware가 붙은 리스너를
사용하면 된다고 함.

ackmode 7가지 (커밋이랑 비슷함)

KafkaListener 어노테이션 자체가 변하진 않고,
파라미터가 달라지는 차이가 있다.

### MessageListener
aka 레코드 리스너
한 번에 하나의 레코드를 처리한다.
```java

@SpringBootApplication
public class SpringConsumerApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SpringConsumerApplication.class);
        app.run(args);
    }

    /**
     * 가장 기본적인 리스너 선언
     * poll()이 호출되어 가져온 레코드들은
     * 차례대로 메시지값을 파라미터로 받게 됨.
     * 레코드의 각 키와 값 처리를 해주면 됨.
     */
    @KafkaListener(topics = "test", groupId="test-group-00")
    public void recordListener(ConsumerRecord<String, String> record) {
        logger.info(record.toString());
    }

    /**
     * 메시지 값을 파라미터로 받는 리스너.
     * StringDeserializer를 사용해 통짜 String으로 전달받게 됨.
     */
    @KafkaListener(topics = "test", groupId="test-group-01")
    public void singleTopicListener(String messageValue) {
        logger.info(messageValue);
    }

    /**
     * 리스너마다 컨슈머 옵션값을 부여하고 싶으면 아래와 같이.
     */
    @KafkaListener(topics = "test", groupId="test-group-02", properties = {
            "max.poll.interval.ms:60000",
            "auto.offset.reset:earliest"
    })
    public void singleTopicWithPropertiesListener(String messageValue) {
        logger.info(messageValue);
    }

    /**
     * concurrency 옵션을 쓰면 멀티쓰레드를 돌릴 수 있음.
     * 3이라 하면 3개의 쓰레드를 돌린다.
     * 파티션이 10개인 토픽을 구독하려면 concurrency를 10으로 설정하면 효율이 가장 좋음.
     */
    @KafkaListener(topics = "test", groupId="test-group-03", concurrency="3")
    public void concurrentTopicListener (String messageValue) {
        logger.info(messageValue);
    }

    /**
     * 특정 토픽의 특정 파티션만 구독하고 싶으면 아래와 같이 하면 되는데..
     * 굳이 이렇게 안할것같다.
     */
    @KafkaListener(topics = "test", groupId="test-group-04", topicPartitions = {
            @TopicPartition(topic = "test01", partitions = {"0", "1"}),
            @TopicPartition(topic = "test02", partitionOffsets = 
            @PartitionOffset(partition = "0", initialOffset = "3"))
    })
    public void singleTopicWithPropertiesListener(ConsumerRecord<String, String> record) {
        logger.info(record.toString());
    }

}

```

### BatchMessageListener
aka 배치 리스너  
한 번에 여러 개의 레코드를 처리한다.  
파라미터로 `ConsumerRecord` 혹은 `String` 대신 `ConsumerRecords` 혹은 `List`로 받는다.  
자세한 코드는 생략


### BatchConsumerAwareMessageListener
aka 배치 컨슈머 리스너  
추가로 컨슈머 인스턴스를 파라미터로 받는다.  
동기 커밋, 비동기 커밋을 활용할 수 있다.


### BatchAcknowledgingMessageListener
aka 배치 커밋 리스너  
추가로 Acknowledgement 인스턴스를 파라미터로 받는다.  
AckMode를 사용할 수 있다.

### BatchAcknowledgingConsumerAwareMessageListener
aka 배치 커밋 컨슈머 리스너  
AckMode도, 컨슈머도 사용할 수 있다.  
커밋 수행을 위해 AckMode는 MANUAL_IMMEDIATE 로 설정해준다.
```java
class SpringConsumerApplication{
    /**
     * 배치커밋리스너
     * 사용자가 원하는 타이밍에 acknowledge()로 커밋.
     */
    @KafkaListener(topics="test", groupId="test-group-01")
    public void commitListener(ConsumerRecords<String, String> records, Acknowledgement ack){
        records.forEach(record -> logger.info(record.toString()));
        ack.acknowledge();
    }

    /**
     * 배치컨슈머리스너
     * 사용자가 원하는 타이밍에 동기/비동기로 커밋 가능
     */
    @KafkaListener(topics="test", groupId="test-group-02")
    public void consumerCommitListener(
            ConsumerRecords<String, String> records,
            Consumer<String, String> consumer
            ) {
        records.forEach(record -> logger.info(record.toString()));
        consumer.commitAsync();
    }
}
```

