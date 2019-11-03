# distributed-computing-with-apache-ignite
Capstone project of the course "Java Engineer to Scalable Backend Developer" at Grid Dynamics

## How to run?

### Build project:

`./gradlew clean build`

### Start ignite cluster in docker:

`docker-compose up -d ignite1 ignite2 ignite3`

### Start product-counter service either in local or in docker:

**Local:** `./gradlew bootRun`

**Docker:** `docker-compose up --build --force-recreate -d product-counter`

## Services

* Ignite cluster:
    Cluster of 3 server nodes with TcpDiscoveryVmIpFinder as a Discovery SPI and enabled peer class loading
    * hostnames: ignite1,ignite2,ignite3
    * ports: 47100-47102:47100,47500-47502:47500

* Product-counter: 
    Simple Spring Boot application loading products from the jcpenney dataset 
    to the replicated Ignite cache "Product Cache" through data streams 
    and then executing IgniteCallable task to count products by price ranges (0-49.99,50-99.99,100+) in the cache 
    * hostname: product-counter
