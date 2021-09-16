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

import com.github.click.uploader.config.AppCfg;
import com.typesafe.config.*;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

@Slf4j
abstract class App implements Runnable {

    static void createApp(Function<AppCfg, Runnable> appFactory) {
        final Config config = ConfigFactory
                .load()
                .getConfig("app");
        log.info("Starting app with config: {}", config.root().render());
        final AppCfg appCfg = ConfigBeanFactory
                .create(config, AppCfg.class);
        appFactory
                .apply(appCfg)
                .run();
    }

}
