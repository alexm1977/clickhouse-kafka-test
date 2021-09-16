/**
 * Copyright © 2021 Aleksandr Mukhin (alex.omsk1977@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.click.uploader;

import com.fasterxml.jackson.databind.*;
import com.github.click.uploader.config.AppCfg;
import com.github.click.uploader.data.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Starter extends App {

    private final AppCfg appCfg;
    private final ObjectMapper objectMapper =
            new ObjectMapper()
                    .disable(SerializationFeature.INDENT_OUTPUT)
                    .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    private final List<? extends ChData> generators = List.of(new OkDataGenerator(objectMapper),
                                                              new OkDataGenerator(objectMapper),
                                                              new OkDataGenerator(objectMapper),
                                                              new OkDataGenerator(objectMapper),
                                                              new OkDataGenerator(objectMapper),
                                                              new OkDataGenerator(objectMapper));

    private final AtomicInteger count = new AtomicInteger(0);

    Starter(AppCfg appCfg) {
        this.appCfg = appCfg;
    }

    @Override
    public void run() {
        final int rate = 50;

        log.info("Start . . .");

        log.info("Create scheduler");
        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        final int intervalMs = Math.floorDiv(100, rate);

        final Producer<Long, String> producer = createProducer();

        log.info("upload every {}ms", intervalMs);
        executorService.scheduleAtFixedRate(
                () -> {
                    try {
                        uploadData(producer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                0,
                intervalMs,
                TimeUnit.MILLISECONDS);

        Runtime.getRuntime()
               .addShutdownHook(
                       new Thread(
                               () -> {
                                   executorService.shutdown();
                                   try {
                                       executorService.awaitTermination(60, TimeUnit.SECONDS);
                                   } catch (InterruptedException e) {
                                       e.printStackTrace();
                                   }
                               }));
    }

    private Producer<Long, String> createProducer() {
        final Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, appCfg.getKafkaUrl());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<>(props);
    }

    private void uploadData(Producer<Long, String> producer) throws ExecutionException, InterruptedException {

        long batch_size = Long.valueOf(Math.round(Math.random() * 50)).intValue();

        RecordMetadata recordMetadata = (RecordMetadata) producer.send(new ProducerRecord(appCfg.getTopic(),
                                                                                          getBatchData(1)))
                                                                 .get();
        if (!recordMetadata.hasOffset()) throw new RuntimeException("Message sent error");
        else System.out.println("Sent " + count.incrementAndGet() + " message.");

    }

    private String getBatchData(long size) {
        String res = "";
        for (long i = 0; i < size; i++) {
            res = res + getData();
        }
        return res;
    }

    private String getData() {
        int index = Long.valueOf(Math.round(Math.random() * (generators.size()-1))).intValue();
        return generators.get(index).getJson()+"\n";
    }

    public static void main(String[] args) {
        createApp(Starter::new);
    }
}
