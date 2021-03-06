package org.miejski.simple;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.miejski.TopologyBuilder;

class SimpleMapTopology implements TopologyBuilder {

    @Override
    public Topology buildTopology(StreamsBuilder builder) {
        KStream<String, Integer> stream = builder.stream("input-topic", Consumed.with(Serdes.String(), Serdes.Integer()));
        stream.map((key, value) -> KeyValue.pair(key, value * 2)).to("output-topic");
        return builder.build();
    }
}
