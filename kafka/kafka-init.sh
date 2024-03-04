#!/bin/bash

# Start Kafka
start-kafka.sh &
KAFKA_PID=$!

# Function to check if Kafka is ready
is_kafka_ready() {
    kafka-topics.sh --list --bootstrap-server localhost:9092 > /dev/null 2>&1
    return $?
}

# Function to create a Kafka topic if it does not already exist
create_topic_if_not_exists() {
    local topic_name=$1
    # Check if the topic already exists
    if kafka-topics.sh --list --bootstrap-server localhost:9092 | grep -q "^${topic_name}$"; then
        echo "Topic ${topic_name} already exists."
    else
        kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic ${topic_name}
        echo "Topic ${topic_name} created."
    fi
}

# Wait for Kafka to be ready
echo "Waiting for Kafka to be ready..."
while ! is_kafka_ready; do
    echo "Kafka is not ready yet. Waiting..."
    sleep 5
done
echo "Kafka is ready."

# List of topics to be created
topics=("auth-user-registered" "user-details-created" "profile-completed" "user-details-failed" "profile-completed-failed")

# Loop through the list and create each topic if it does not exist
for topic in "${topics[@]}"; do
    create_topic_if_not_exists $topic
done

# Wait for Kafka to exit (if running in foreground)
wait $KAFKA_PID