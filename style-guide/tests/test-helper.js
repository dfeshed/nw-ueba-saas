import resolver from './helpers/resolver';
import './helpers/flash-message';

import { setResolver } from 'ember-qunit';
import { start } from 'ember-cli-qunit';
import loadEmberExam from 'ember-exam/test-support/load';

import { registerWaiter } from './helpers/raf-test-waiter';

registerWaiter();

setResolver(resolver);
loadEmberExam();
start();
