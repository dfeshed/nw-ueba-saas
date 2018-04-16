import resolver from './helpers/resolver';
import './helpers/flash-message';
import {
  setResolver
} from 'ember-qunit';
import { start } from 'ember-cli-qunit';
import loadEmberExam from 'ember-exam/test-support/load';

setResolver(resolver);
loadEmberExam();
start();
