process.setMaxListeners(1000);
var cli = require("ember-cli/lib/cli");
var args = process.argv.slice(1);
args.unshift('test');
cli({
  inputStream: process.stdin,
  outputStream: process.stdout,
  cliArgs: args
});