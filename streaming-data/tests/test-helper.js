import Application from '../app';
import config from '../config/environment';
import resolver from './helpers/resolver';
import { setResolver, setApplication } from '@ember/test-helpers';
import { start } from 'ember-qunit';
import loadEmberExam from 'ember-exam/test-support/load';

setApplication(Application.create(config.APP));
setResolver(resolver);
loadEmberExam();
start();
