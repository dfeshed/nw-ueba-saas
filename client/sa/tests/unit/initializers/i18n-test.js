import Ember from 'ember';
import { initialize } from '../../../initializers/i18n';
import { module, test } from 'qunit';

var application;

module('Unit | Initializer | i18n', {
  beforeEach: function() {
    Ember.run(function() {
      application = Ember.Application.create();
      application.deferReadiness();
    });
  }
});

// Replace this with your real tests.
test('it works', function(assert) {
  initialize( application);

  // you would normally confirm the results of the initializer here
  assert.ok(true);
});
