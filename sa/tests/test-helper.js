import 'sa/tests/helpers/with-feature';
import resolver from './helpers/resolver';
import './helpers/flash-message';

import { start } from 'ember-cli-qunit';
import { setResolver } from 'ember-qunit';
import loadEmberExam from 'ember-exam/test-support/load';

import { run } from '@ember/runloop';
import QUnit from 'qunit';

QUnit.testDone(({ module, name }) => {
  if (run.hasScheduledTimers()) {
    console.log('Existing timers', run.backburner._timers); // eslint-disable-line
    console.log('Existing debouncees', run.backburner._debouncees); // eslint-disable-line
    console.log('Existing throttlers', run.backburner._throttlers); // eslint-disable-line
    throw new Error(`A timer existed at the end of the following test: ${module}: ${name}`);
  }
});

setResolver(resolver);
loadEmberExam();
start();
