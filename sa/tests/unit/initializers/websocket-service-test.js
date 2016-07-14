import Ember from 'ember';
import { initialize } from '../../../initializers/websocket-service';
import { module, test } from 'qunit';

const {
  run,
  Application
} = Ember;

let application;

module('Unit | Initializer | websocket service', {
  beforeEach() {
    run(function() {
      application = Application.create();
      application.deferReadiness();
    });
  }
});

// Replace this with your real tests.
test('it works', function(assert) {
  initialize(application);

  // you would normally confirm the results of the initializer here
  assert.ok(true);
});
