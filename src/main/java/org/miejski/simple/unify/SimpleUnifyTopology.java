package org.miejski.simple.unify;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.miejski.TopologyBuilder;
import org.miejski.questions.KStreamStateLeftJoin;

import static java.util.Arrays.asList;
import static org.apache.kafka.common.serialization.Serdes.Integer;
import static org.apache.kafka.common.serialization.Serdes.String;
import static org.apache.kafka.streams.kstream.Consumed.with;


public class SimpleUnifyTopology implements TopologyBuilder {

    public static final String NEW_TOPIC = "new_topic";
    public static final String UPDATE_TOPIC = "update_topic";
    public static final String DELETE_TOPIC = "delete_topic";
    public static final String UNIFIED_TOPIC = "unified_topic";
    public static final int DELETED_VALUE = -1;
    public static final String NUMBERS_TABLE_TOPIC = "something";
    public static final String NUMBERS_STORE_NAME = "numbersFinal";



    @Override
    public Topology buildTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, Integer> inputStream = streamsBuilder.stream(asList(NEW_TOPIC, UPDATE_TOPIC, DELETE_TOPIC), with(String(), Integer()));
        inputStream.to(UNIFIED_TOPIC, Produced.with(String(), Integer()));

        streamsBuilder
                .table(NUMBERS_TABLE_TOPIC, with(String(), Integer()), Materialized.as(NUMBERS_STORE_NAME))
                .filter((k, v) -> v != null);


        NumbersJoiner valJoiner = new NumbersJoiner();
        KStream<String, Integer> transformCreateStream = inputStream.transform(() -> new KStreamStateLeftJoin<>(NUMBERS_STORE_NAME, valJoiner), NUMBERS_STORE_NAME);

        transformCreateStream.to(NUMBERS_TABLE_TOPIC);

        return streamsBuilder.build();
    }
}