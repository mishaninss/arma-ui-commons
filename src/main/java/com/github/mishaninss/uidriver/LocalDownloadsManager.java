/*
 * Copyright 2019 Sergey Mishanin
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

package com.github.mishaninss.uidriver;

import com.github.mishaninss.data.UiCommonsProperties;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.interfaces.IDownloadsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LocalDownloadsManager implements IDownloadsManager {

    @Autowired
    private UiCommonsProperties properties;
    @Reporter
    private IReporter reporter;

    @Override
    public File getDownloadedFile(String fileName) {
        return Paths.get(properties.driver().downloadsDir, fileName).toFile();
    }

    @Override
    public List<String> getDownloadedFileNames() {
        Path dir = Paths.get(properties.driver().downloadsDir);
        try (Stream<Path> files = Files.list(dir)){
            return files.map(path -> path.toFile().getName()).collect(Collectors.toList());
        } catch (IOException ex){
            reporter.ignoredException(ex);
            return Collections.emptyList();
        }
    }

    @Override
    public List<File> getDownloadedFiles() {
        Path dir = Paths.get(properties.driver().downloadsDir);
        try (Stream<Path> files = Files.list(dir)){
            return files.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException ex){
            reporter.ignoredException(ex);
            return Collections.emptyList();
        }
    }
}
