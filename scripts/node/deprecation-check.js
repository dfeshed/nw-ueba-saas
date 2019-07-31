/* eslint-disable no-console */

// Report JSON takes this format
//
// [
//   {
//     "appName": "ngcoreui",
//     "report": {
//       "deprecationDetail": {
//         "computed-property.volatile": 32,
//         "computed-property.override": 4
//       },
//       "totalDeprecations": 36
//     }
//   },
//   {
//     "appName": "streaming-data",
//     "report": {
//       "deprecationDetail": {},
//       "totalDeprecations": 0
//     }
//   }
// ]

const baselineReportApps = require('/mnt/libhq-SA/SAStyle/build-statistics/application-deprecation-report.json');
const buildReportApps = require('./application-deprecation-report.json');

// uncomment to test locally (after you create these files)
// const baselineReportApps = require('./baseline.json');
// const buildReportApps = require('./app.json');

const ignoreDeprecations = ['computed-property.volatile'];

const errors = [];

const _doneSuccess = () => {
  process.exit(0);
};

const _doneErrors = () => {
  errors.forEach((err) => console.log(err));
  process.exit(1);
};

if (buildReportApps.length === 0) {
  console.log('No deprecations in build report, no need to process report for added deprecations');
  _doneSuccess();
  return;
}

// Build report has deprecations, process each app in the report
// and assemble potential errors
buildReportApps.forEach(({ appName, report }) => {

  // Find the matching app report in the baseline
  const baselineAppReport = baselineReportApps.find((app) => app.appName === appName);

  // Iterate over all the apps in the build report
  Object.entries(report.deprecationDetail).forEach(([ depName, depInstances ]) => {

    if (ignoreDeprecations.includes(depName)) {
      // deprecation we can't avoid, potentially because it is in a
      // vendor library that we currently can't stop using more of
      return;
    }

    // Is there not a report for this app or for this deprecation?
    // Then this deprecation is brand new, so log the error as such
    const appHasDeprecations = baselineAppReport && !!baselineAppReport.report && !!baselineAppReport.report.deprecationDetail;
    const appHasThisDeprecation = appHasDeprecations && !!baselineAppReport.report.deprecationDetail[depName];
    if (!appHasDeprecations || !appHasThisDeprecation) {
      errors.push(`New deprecation [[ ${depName} ]] found [[ ${depInstances} ]] times in app [[ ${appName} ]]`);
      return;
    }

    const baselineDepInstances = baselineAppReport.report.deprecationDetail[depName];
    if (baselineDepInstances < depInstances) {
      const diff = depInstances - baselineDepInstances;
      errors.push(`Deprecation [[ ${depName} ]] found [[ ${depInstances} ]] times in app [[ ${appName} ]], this number increased [[ ${diff} ]] from the baseline`);
    }
  });
});

if (errors.length === 0) {
  _doneSuccess();
  return;
}

_doneErrors();
