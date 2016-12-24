var http = require('http');
var dns = require('dns');
var hostname = require('os').hostname();

var ip = null;

var handleRequest = function(request, response) {
  response.writeHead(200);
  if (!ip) {
	  dns.lookup(hostname, function (err, add, fam) {
	  	ip = add;
		response.end("Hello Kubernetes from " + add);
	  });
  } else {
  	response.end("Hello Kubernetes from " + ip);
  }
}

var www = http.createServer(handleRequest);
www.listen(8080);