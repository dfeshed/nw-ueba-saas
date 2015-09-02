import Ember from 'ember';
import ThemedComponentMixin from '../../../mixins/themed-component';
import { module, test } from 'qunit';

module('Unit | Mixin | themed component');

// Replace this with your real tests.
test('it works', function(assert) {
  var ThemedComponentObject = Ember.Object.extend(ThemedComponentMixin);
  var subject = ThemedComponentObject.create();
  assert.ok(subject);
});
