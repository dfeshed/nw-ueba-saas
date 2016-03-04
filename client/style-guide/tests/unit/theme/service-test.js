import Ember from 'ember';
import { moduleFor, test } from 'ember-qunit';

moduleFor('service:theme', 'Unit | Service | theme', {
  // Specify the other units that are required for this test.
  // needs: ['service:foo']
});

test('it exists and works', function(assert) {
  const MY_THEME_NAME = 'my-test-theme';

  assert.expect(3);

  let service = this.subject();
  assert.ok(service, 'Service not defined.');

  // Select a theme.
  service.set('selected', MY_THEME_NAME);

  // Confirm that the DOM has changed to the selected theme.
  let $root = Ember.$('body');

  assert.ok(
    $root.hasClass(`rsa-${MY_THEME_NAME}`),
    'Chose a theme, but could not find the corresponding CSS class applied to DOM.'
  );

  // Confirm that the service can read the selected theme.
  assert.equal(service.get('selected'), MY_THEME_NAME,
    'Chose a theme, but service could not return the selected theme back.');

});
