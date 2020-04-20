import Application from '../app';
import { setApplication } from '@ember/test-helpers';
import config from '../config/environment';

import './helpers/flash-message';

import { start } from 'ember-qunit';
import loadEmberExam from 'ember-exam/test-support/load';

import LinkComponent from '@ember/routing/link-component';
LinkComponent.reopen({
  attributeBindings: ['test-id']
});

setApplication(Application.create(config.APP));
loadEmberExam();
start();
