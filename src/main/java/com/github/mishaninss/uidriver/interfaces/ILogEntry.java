/*
 * Copyright 2018 Sergey Mishanin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mishaninss.uidriver.interfaces;

import java.util.logging.Level;

public interface ILogEntry {

    /**
     * Gets the logging entry's severity.
     *
     * @return severity of log statement
     */
    Level getLevel();

    /**
     * Gets the timestamp of the log statement in milliseconds since UNIX Epoch.
     *
     * @return timestamp as UNIX Epoch
     */
    long getTimestamp();

    /**
     * Gets the log entry's message.
     *
     * @return the log statement
     */
    String getMessage();
}
