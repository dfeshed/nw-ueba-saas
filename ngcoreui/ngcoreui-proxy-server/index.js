/* eslint-disable */
const http = require('http');
const httpProxy = require('http-proxy');

const prot = 'http';
const addr = '10.101.216.73';
const port = 50102;
const host = `${addr}:${port}`;
const origin = `${prot}://${addr}:${port}`;

// proxy server
const proxyServer = new httpProxy.createProxyServer({
  auth: 'admin:netwitness',
  target: origin,
  prependPath: false
  // changeOrigin: true
});

// proxied host & origin http headers need to match
proxyServer.on('proxyReq', function(proxyReq, req, res, options) {
  proxyReq.setHeader('Host', host);
  proxyReq.setHeader('Origin', origin);
});

// proxied host & origin ws headers need to match
proxyServer.on('proxyReqWs', function(proxyReq, req, res, options) {
  proxyReq.setHeader('Host', host);
  proxyReq.setHeader('Origin', origin);
});

proxyServer.on('error', function (err, req, res) {
  console.log(`proxy error: ${err}`);
});

// http server using the proxy for http
const httpServer = http.createServer(function(req, res) {
  proxyServer.web(req, res);
});

// http server using the proxy for ws
httpServer.on('upgrade', function(req, socket, head) {
  console.log('*** upgrade request ***');
  proxyServer.ws(req, socket, head);
});

console.log(`httpServer listening on localhost:${port}`);
console.log(`...proxying to ${origin}`);

httpServer.listen(port);
