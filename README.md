# WebSocketizer
WebSocketizer - a tool for automatically refactoring RESTFul HTTP web service to WebSocket application by generating Web Socket Server and Client without altering the functionality exposed by the RESTFul application. WebSocketizer has two major components - Pattern Identifier (PI) &amp; Code Generator (COGEN). PI is responsible for parsing the code and identifying definitive patterns it expects in a JAX-RS RESTFul service.

## Run

gradle fatJar

java -cp "WebSocketizer-1.0.jar" com.javacoders.websocketizer.client.ConsoleClient --input={project_base_path}

Sample Command: java -cp "WebSocketizer-1.0.jar" com.javacoders.websocketizer.client.ConsoleClient --input=C:\Sam\Work\WorkSpace\WebSocketizer\src\