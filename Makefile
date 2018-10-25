setup-clusters:
	${KAFKA_PATH}/bin/zookeeper-server-start.sh ${KAFKA_PATH}/config/zookeeper.properties &
	wait-port localhost:2181 -t 10000
	${KAFKA_PATH}/bin/kafka-server-start.sh ${KAFKA_PATH}/config/server.properties &
	${KAFKA_PATH}/bin/kafka-server-start.sh ${KAFKA_PATH}/config/server-1.properties &
	${KAFKA_PATH}/bin/kafka-server-start.sh ${KAFKA_PATH}/config/server-2.properties &
	wait-port localhost:9092 -t 10000
	wait-port localhost:9093 -t 10000
	wait-port localhost:9094 -t 10000

topics_setup:
	- ${KAFKA_PATH}/bin/kafka-topics.sh --delete --zookeeper localhost:2181 --topic 'question_.*'
	${KAFKA_PATH}/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic question_create_topic
	${KAFKA_PATH}/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic question_update_topic
	${KAFKA_PATH}/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic question_delete_topic
	${KAFKA_PATH}/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic question_state_topic --config cleanup.policy=compact

down:
	ps -ef | grep 'kafka' | grep -v grep | grep -v zookeeper |  awk '{print $$2}' | xargs  kill -9
	ps -ef | grep 'zookeeper' | grep -v grep | awk '{print $$2}' | xargs  kill -9

list_topics:
	${KAFKA_PATH}/bin/kafka-topics.sh --list --zookeeper localhost:2181

listen_to_state:
	${KAFKA_PATH}/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092  --property print.key=true --key-deserializer org.apache.kafka.common.serialization.IntegerDeserializer  --from-beginning --topic question_state_topic