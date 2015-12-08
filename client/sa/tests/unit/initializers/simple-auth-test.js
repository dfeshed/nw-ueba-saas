import Ember from 'ember';
import SimpleAuthInitializer from '../../../initializers/simple-auth';
import { module, test } from 'qunit';

let application;

module('Unit | Initializer | simple auth', {
  beforeEach() {
    Ember.run(function() {
      application = Ember.Application.create();
      application.deferReadiness();
    });
  }
});

// Replace this with your real tests.
test('it works', function(assert) {
  SimpleAuthInitializer.initialize(application);

  // you would normally confirm the results of the initializer here
  assert.ok(true);
});
