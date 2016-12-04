# WebSocketizer
WebSocketizer - a tool for automatically refactoring RESTFul HTTP web service to WebSocket application by generating Web Socket Server and Client without altering the functionality exposed by the RESTFul application. WebSocketizer has two major components - Pattern Identifier (PI) &amp; Code Generator (COGEN). PI is responsible for parsing the code and identifying definitive patterns it expects in a JAX-RS RESTFul service.

#### Technology Stack
* Java, JAX-RS, Javax-Websocket, REST

# Usage

### Download
* `git clone https://github.com/shivam-maharshi/WebSocketizer`<br>
* `cd WebSocketizer`

### Build
* `gradle fatJar`

## Run

* `java -cp {$WEBSOCKETIZER_BASE_PATH}\build\libs\WebSocketizer-1.0.jar com.javacoders.websocketizer.client.ConsoleClient --input={$PROJECT_BASE_PATH}`

* `Sample Command: java -cp C:\Sam\Work\WorkSpace\WebSocketizer\build\libs\WebSocketizer-1.0.jar com.javacoders.websocketizer.client.ConsoleClient --input=C:\Sam\Work\WorkSpace\WebSocketizer\src\main\java\com\javacoders\service\rs`
