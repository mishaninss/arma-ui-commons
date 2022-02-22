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

package com.github.mishaninss.arma.uidriver;

import com.github.mishaninss.arma.data.UiCommonsProperties;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.interfaces.IDownloadsManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalDownloadsManager implements IDownloadsManager, InitializingBean {

  public static final String DOWNLOADS_DIR_PROPERTY = "arma.driver.downloads.dir";
  public static final String BROWSER_DOWNLOADS_DIR_PROPERTY = "arma.driver.browser.downloads.dir";
  public static final String DOWNLOADS_PER_SESSION_PROPERTY = "arma.driver.downloads.per.session";

  @Autowired
  private UiCommonsProperties properties;
  @Reporter
  private IReporter reporter;

  @Value("${" + DOWNLOADS_DIR_PROPERTY + ":}")
  private String baseDownloadsDir;

  private String downloadsDir;

  @Value("${" + DOWNLOADS_PER_SESSION_PROPERTY + ":false}")
  private boolean downloadsPerSession;

  @Override
  public void afterPropertiesSet() {
    if (downloadsPerSession && StringUtils.isNotBlank(baseDownloadsDir)) {
      downloadsDir = Path.of(baseDownloadsDir, UUID.randomUUID().toString()).toString();
    } else {
      downloadsDir = baseDownloadsDir;
    }
  }

  @Override
  public String getDownloadsDir() {
    return downloadsDir;
  }

  @Override
  public File getDownloadedFile(String fileName) {
    return Paths.get(getDownloadsDir(), fileName).toFile();
  }

  @Override
  public List<String> getDownloadedFileNames() {
    Path dir = Paths.get(getDownloadsDir());
    try (Stream<Path> files = Files.list(dir)) {
      return files.map(path -> path.toFile().getName()).collect(Collectors.toList());
    } catch (IOException ex) {
      reporter.ignoredException(ex);
      return Collections.emptyList();
    }
  }

  @Override
  public List<File> getDownloadedFiles() {
    Path dir = Paths.get(getDownloadsDir());
    try (Stream<Path> files = Files.list(dir)) {
      return files.map(Path::toFile).collect(Collectors.toList());
    } catch (IOException ex) {
      reporter.ignoredException(ex);
      return Collections.emptyList();
    }
  }
}
