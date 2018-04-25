import resolver from './helpers/resolver';
import { setResolver } from '@ember/test-helpers';
import { start } from 'ember-cli-qunit';
import loadEmberExam from 'ember-exam/test-support/load';
import './helpers/flash-message';

setResolver(resolver);
loadEmberExam();
start();
