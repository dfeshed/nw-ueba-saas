/* eslint-disable no-console */

// Usage:
// node deprecation-report url [-w]
// - url - The URL of the RAW job output you'd like to process for deprecations
//         These URLs usually end in "consoleText"
// - -w  - If you want to write the detailed application output to a json file,
//         then add -w
const rp = require('request-promise');
const fs = require('fs');

let url;
if (process.argv.length > 2) {
  url = process.argv[2];
}

if (!url) {
  console.log('NO URL PROVIDED, QUITTING');
  process.exit(1);
  return;
}

if (url.indexOf('consoleText') === -1) {
  url += 'consoleText';
}

console.log('GENERATING DEPRECATION REPORT USING URL: ', url);

const options = { rejectUnauthorized: false };

const _retrieveOutput = async() => {
  const jenkinsJobOutput = await rp(url, options);
  return jenkinsJobOutput;
};

const _parseAppsReport = (output) => {

  // break the output down into each application
  const apps = output.split(' is good!')
    .map((app) => {
      // right here app is the console output for a single
      // application and at the end of each app is the name
      // of the app since we split on " is good!" which is
      // preceded by the name
      // ex: investigate-events is good!
      return {
        appName: app.split(' ').pop(),
        appText: app
      };
    });

  // remove the last one as it is just the ending bits of the
  // job text unrelated to individual application processing
  apps.pop();

  const appsWithReport = apps.map((app) => {
    // split the text by deprecation id and remove the first item
    const deprecationTextChunks = app.appText.split(' [deprecation id: ');
    deprecationTextChunks.shift();

    // create report template
    const report = {
      deprecationDetail: {},
      totalDeprecations: deprecationTextChunks.length
    };

    deprecationTextChunks.forEach((depText) => {
      const deprecationIds = depText.split('] ');
      const dep = deprecationIds.shift();
      if (report.deprecationDetail[dep]) {
        report.deprecationDetail[dep]++;
      } else {
        report.deprecationDetail[dep] = 1;
      }
    });

    return {
      appName: app.appName,
      report
    };
  });

  return appsWithReport;
};

const _logOverallReport = (report) => {
  const overallReport = {};
  report.forEach((app) => {
    const depDeets = app.report.deprecationDetail;
    Object.entries(depDeets).forEach(([key, value]) => {
      if (overallReport[key]) {
        overallReport[key] += value;
      } else {
        overallReport[key] = value;
      }
    });
  });

  // Write the overall report
  console.log(JSON.stringify(overallReport, null, 2));
  console.log('TOTAL DEPRECATIONS: ', Object.values(overallReport).reduce((a, b) => a + b, 0));
};

const orchestrate = async() => {
  const output = await _retrieveOutput();
  const appsReport = _parseAppsReport(output);
  _logOverallReport(appsReport);

  const shouldWriteReport = process.argv.indexOf('-w') > 1;
  if (shouldWriteReport) {
    fs.writeFileSync('application-deprecation-report.json', JSON.stringify(appsReport, null, 2));
  }
};

orchestrate();


