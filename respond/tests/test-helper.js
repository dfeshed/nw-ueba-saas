import Application from '../app';
import { setApplication } from '@ember/test-helpers';
import config from '../config/environment';

import './helpers/flash-message';

import { start } from 'ember-cli-qunit';
import loadEmberExam from 'ember-exam/test-support/load';

setApplication(Application.create(config.APP));
loadEmberExam();
start();
