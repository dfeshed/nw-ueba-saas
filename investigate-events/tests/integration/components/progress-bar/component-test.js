import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, render } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | Progress Bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it renders and supports a percent attribute', async function(assert) {
    this.set('myProgress', null);

    await render(hbs`{{progress-bar percent=myProgress}}`);
    let value;
    assert.ok(find('.rsa-progress-bar'), 'Expected root DOM element.');

    const pbFill = find('.js-progress-bar__fill');
    value = window.getComputedStyle(pbFill).getPropertyValue('flex-basis');
    assert.equal(value, '0%', 'Expected default display to be 0%.');

    this.set('myProgress', 12.8);
    value = window.getComputedStyle(pbFill).getPropertyValue('flex-basis');
    assert.equal(value, '13%', 'Expected numeric values to be displayed as rounded integers.');

    this.set('myProgress', 'a');
    value = window.getComputedStyle(pbFill).getPropertyValue('flex-basis');
    assert.equal(value, '0%', 'Expected non-numeric values to display as 0%.');

    this.set('myProgress', 120);
    value = window.getComputedStyle(pbFill).getPropertyValue('flex-basis');
    assert.equal(value, '100%', 'Expected numbers > 100 to display as 100%.');

    this.set('myProgress', -10);
    value = window.getComputedStyle(pbFill).getPropertyValue('flex-basis');
    assert.equal(value, '0%', 'Expected numbers < 0 to display as 0%.');
  });
});