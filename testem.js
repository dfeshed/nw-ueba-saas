/* eslint-env node */
const fs = require('fs');
const path = require('path');
const util = require('util');
const Transform = require('stream').Transform;

const pathToMultiReporter = path.join(__dirname, 'scripts', 'node', 'node_modules', 'testem-multi-reporter');
const MultiReporter = require(pathToMultiReporter);

const appName = path.basename(process.cwd());
const pathToTestem = path.join(__dirname, appName, 'node_modules', 'testem');
const TAPReporter = require(path.join(pathToTestem, 'lib/reporters/tap_reporter'));
const XunitReporter = require(path.join(pathToTestem, 'lib/reporters/xunit_reporter'));

const reporters = [{
  ReporterClass: TAPReporter,
  args: [false, undefined, { get: () => false }]
}];

if (process.env.NODE_ENV === 'production') {
  const outDir = path.join(__dirname, 'junit');
  if (!fs.existsSync(outDir)) {
    fs.mkdirSync(outDir);
  }

  // Creating a transform stream that'll weed out instances
  // of 'PhantomJS 2.1` and replace them with 'PhantomJS' since
  // the jenkins plugin organizes its stuff using the '.'
  // in the title. Don't do this and we have to drill through
  // ' PhantomJS2 >> 1 >> our tests ' in the UI
  function CatchPhantom(options) {
    if (!(this instanceof CatchPhantom)) {
      return new CatchPhantom(options);
    }
    Transform.call(this, options);
  }
  util.inherits(CatchPhantom, Transform);

  CatchPhantom.prototype._transform = function (chunk, enc, cb) {
    const noPhantomChunk = chunk.toString().replace(/PhantomJS 2.1/g, 'PhantomJS');
    this.push(noPhantomChunk);
    cb();
  };

  const outPath = path.join(outDir, `junit.${appName}.xml`);
  const outStream = fs.createWriteStream(outPath);

  var phantomCatcher = new CatchPhantom();
  phantomCatcher.pipe(outStream);

  reporters.push({
    ReporterClass: XunitReporter,
    args: [false, phantomCatcher, { get: () => false }]
  });
}

const multiReporter = new MultiReporter({ reporters });

module.exports = {
  "test_page": "tests/index.html?hidepassed",
  "disable_watching": true,
  "launch_in_ci": [
    "Chrome"
  ],
  "launch_in_dev": [
    "Chrome"
  ],
  browser_args: {
    Chrome: {
      mode: 'ci',
      args: [
        '--disable-gpu',
        '--headless',
        '--remote-debugging-port=6222',
        '--window-size=1440,900'
      ]
    }
  },
  parallel: 4,
  reporter: multiReporter
};
