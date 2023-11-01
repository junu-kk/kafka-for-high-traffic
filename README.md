# 카프카 연습
### updates: 2023
- HTTP API의 경우 request 이후 response를 기다리는 동안 blocking이 발생합니다. 이는 기본적으로 publish 후 정상 publish 여부만 수신받는 Kafka에 비해 처리속도가 느릴 수밖에 없습니다.
  - 만약 HTTP API를 비동기로 요청하도록 하거나, 여러 request를 병렬로 처리하도록 하거나, thread pool size를 늘리거나 하면 HTTP API의 처리량이 증가할 것이고
  - 요청을 몇 번 보냈는지가 아닌, 메시지가 몇 개나 도착했는지 측정했다면 보다 정확한 의미의 성능비교가 되었을 것입니다.
- Kafka가 HTTP API보다 나은 점은 안정성에 있습니다. HTTP API는 수신 측 서버에 장애가 나면 메시지가 담긴 request는 처리되지 못하지만, Kafka는 subscribe 쪽 서버에 문제가 생겨도 메시지가 유실되지 않기 때문입니다.

![1](https://miro.medium.com/max/1400/1*ZQE0ttmpOQx1ucfEtbfLbg.jpeg)
![2](https://miro.medium.com/max/1400/1*iog980NZVCzn4DOb8TiDGw.jpeg)
![3](https://miro.medium.com/max/1400/1*hqsuh6PAmDuZEGS7E_1tqQ.jpeg)
