import resolver from './helpers/resolver';
import { setResolver } from '@ember/test-helpers';

import './helpers/flash-message';

import { start } from 'ember-cli-qunit';
import loadEmberExam from 'ember-exam/test-support/load';

setResolver(resolver);
loadEmberExam();
start();
