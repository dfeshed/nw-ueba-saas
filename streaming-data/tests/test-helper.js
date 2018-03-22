import resolver from './helpers/resolver';
import { setResolver } from '@ember/test-helpers';
import { start } from 'ember-qunit';
import loadEmberExam from 'ember-exam/test-support/load';

setResolver(resolver);
loadEmberExam();
start();
