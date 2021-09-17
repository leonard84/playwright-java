/*
 * Copyright (c) Microsoft Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microsoft.playwright;

import com.microsoft.playwright.options.HttpHeader;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPageNetworkResponse extends TestBase {
  @Test
  void shouldReportMultipleSetCookieHeaders() {
    server.setRoute("/headers", exchange -> {
      exchange.getResponseHeaders().add("Set-Cookie", "a=b");
      exchange.getResponseHeaders().add("Set-Cookie", "c=d");
      exchange.sendResponseHeaders(200, 0);
      exchange.getResponseBody().close();
    });
    page.navigate(server.EMPTY_PAGE);
    Response response = page.waitForResponse("**/*", () -> page.evaluate("fetch('/headers')"));
    List<HttpHeader> headers = response.headersArray();
    List<String> cookies = headers.stream().filter(
      httpHeader -> "set-cookie".equals(httpHeader.name.toLowerCase())).map(h -> h.value).collect(Collectors.toList());
    assertEquals(asList("a=b", "c=d"), cookies);
    assertEquals(null, response.headerValue("not-there"));
    assertEquals("a=b\nc=d", response.headerValue("set-cookie"));
    assertEquals(asList("a=b", "c=d"), response.headerValues("set-cookie"));
  }

}
