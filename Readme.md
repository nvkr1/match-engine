# RingBootEngine
Open Source Matching engine written in Java, built on top of Spring Boot framework and [LMAX Disruptor](https://lmax-exchange.github.io/disruptor/disruptor.html)

### Benchmark (Might need more)
#### Resources Spec:
- Match Engine - EKS Fargate Deployment 4vCPU, 8GB RAM 
- [Locust](https://locust.io) total 8vCPU 16GB RAM 6 worker 16 users
- 16 locust users send trade orders constantly with normal distribution price range, 10% cancel orders for full 1 minute:
#### Average Metrics:
- 7'000 RPS (Requests per second)
- 14'000 OPS (Operations per second)
- 2 milliseconds response

#### JTL Report
##### Overall
![Overall](https://raw.githubusercontent.com/bbattulga/match-engine/main/images/rps-7000-overall.jpg)
##### Requests
![Requests](https://raw.githubusercontent.com/bbattulga/match-engine/main/images/rps-7000-requests.jpg)

### Technology stack
- Java 17
- Spring Boot 3
- RabbitMQ
- Java's TreeSet for Order Book Implementation
- [LMAX Disruptor](https://lmax-exchange.github.io/disruptor/disruptor.html)

### Architecture
![Architecture](https://raw.githubusercontent.com/bbattulga/match-engine/main/images/architecture.jpg)