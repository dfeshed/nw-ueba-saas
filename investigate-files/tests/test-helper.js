import Application from '../app';
import { setApplication } from '@ember/test-helpers';
import config from '../config/environment';

import { start } from 'ember-qunit';
import loadEmberExam from 'ember-exam/test-support/load';

setApplication(Application.create(config.APP));
loadEmberExam();
start();
