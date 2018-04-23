import resolver from './helpers/resolver';
import { setResolver } from '@ember/test-helpers';
import { start } from 'ember-cli-qunit';
import loadEmberExam from 'ember-exam/test-support/load';

import { registerRafWaiter } from './helpers/raf-test-waiter';

registerRafWaiter();

setResolver(resolver);
loadEmberExam();
start();
