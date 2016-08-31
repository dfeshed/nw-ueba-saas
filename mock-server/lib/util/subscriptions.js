/* eslint-disable no-console */
/* global process, require */

import fs from 'fs';
import path from 'path';
import read from 'fs-readdir-recursive';
import chalk from 'chalk';
import chokidar from 'chokidar';

let registry = {};
let boostrappedWatcher = false;

const rediscover = function(directories, filePathChanged) {
  console.log(chalk.green(`mock-server detected file change [[ ${filePathChanged} ]] reloading registry`));
  discoverSubscriptions(directories);
};

const watchDirectories = function(directories) {
  const watcher = chokidar.watch(directories, {
    ignoreInitial: true,
    ignored: /[\/\\]\./,
    persistent: true
  });

  watcher
    .on('add', (p) => rediscover(directories, p))
    .on('change', (p) => rediscover(directories, p))
    .on('unlink', (p) => rediscover(directories, p));
};

const report = function(locs, subs) {
  if (!Object.keys(subs).length) {
    console.error(chalk.red('\nNo subscriptions found! Exiting...\n'));
    process.exit(1);
  } else {
    console.log(`Found a total of ${Object.keys(subs).length} subscriptions inside ${locs}`);
    console.log('Subscription list:');
    Object.keys(subs).forEach((sub) => console.log(`${subs[sub].subscriptionDestination}: ${subs[sub].requestDestination}`));
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
      console.error(chalk.red(`\nPath passed in [[ ${dir} ]] is not a directory. Exiting...\n`));
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
      .map((fullFileName) => {
        return {
          sub: require(fullFileName).default,
          fullFileName
        };
      })
       // filter out those that are not subscription files
      .filter(({ sub }) => {
        return sub &&
          sub.subscriptionDestination &&
          sub.requestDestination &&
          (sub.message || sub.page);
      })
      // check for multiple APIs
      .forEach(({ sub, fullFileName }) => {

        // eliminating cache so that reload can work
        if (require.cache[fullFileName]) {
          delete require.cache[fullFileName];
        }

        if (sub.message && sub.page) {
          console.error(
            chalk.red(
              `\nCannot implement both \`message\` and \`page\` in same destination file. Throwing out: ${sub.subscriptionDestination} + ${sub.requestDestination}\n`));
        } else {
          subscriptions.push(sub);
        }
      });
  });

  const subscriptionObject = {};
  subscriptions.forEach((sub) => {
    if (subscriptionObject[sub.subscriptionDestination]) {
      console.error(chalk.red(`\nDuplicate subscriptions detected for [[ ${sub.subscriptionDestination} ]], first one detected will be used.\n`));
    } else {
      subscriptionObject[sub.subscriptionDestination] = sub;
    }
  });
  report(subscriptionLocations, subscriptionObject);


  registry = subscriptionObject;

  // start up watcher
  if (!boostrappedWatcher) {
    watchDirectories(subscriptionLocations);
    boostrappedWatcher = true;
  }

};

const subscriptionList = function() {
  return registry;
};

export {
  subscriptionList,
  discoverSubscriptions
};