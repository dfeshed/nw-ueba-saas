/* eslint-disable no-console */
/* global process, require */

import fs from 'fs';
import path from 'path';
import read from 'fs-readdir-recursive';

const report = function(locs, subs) {
  if (!subs.length) {
    console.error('No subscriptions found! Exiting...');
    process.exit(1);
  } else {
    console.log(`Found a total of ${subs.length} subscriptions inside ${locs}`);
    console.log('Subscription list:');
    subs.forEach((sub) => console.log(`${sub.subscriptionDestination}: ${sub.requestDestination}`));
  }
};

const discoverSubscriptions = function(subscriptionLocations) {
  const subscriptions = [];

  // normalize to array
  if (!Array.isArray(subscriptionLocations)) {
    subscriptionLocations = [subscriptionLocations];
  }

  // ensure the locations are directories
  subscriptionLocations.forEach(function(dir) {
    const isDirectory = fs.statSync(dir).isDirectory();
    if (!isDirectory) {
      console.error(`Path passed in [[ ${dir} ]] is not a directory.`);
      process.exit(1);
    }
  });

  // pull out the subscriptions
  subscriptionLocations.forEach(function(dir) {
    read(dir)
      // build full path
      .map((fileName) => path.join(dir, fileName))
      // don't want directories
      .filter((fullFileName) => fs.statSync(fullFileName).isFile())
      // require in the files, dealing with module system interop between es6 and node, so need .default
      .map((fullFileName) => require(fullFileName).default)
       // filter out those that are not subscription files
      .filter((sub) => sub && sub.subscriptionDestination && sub.requestDestination && sub.createSendMessage)
      .forEach((sub) => subscriptions.push(sub)); // add
  });

  report(subscriptionLocations, subscriptions);

  const subscriptionObject = {};
  subscriptions.forEach((sub) => subscriptionObject[sub.subscriptionDestination] = sub);

  return subscriptionObject;
};

export {
  discoverSubscriptions
};