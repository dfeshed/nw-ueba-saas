import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';


module('Integration | Component | Anomalies', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Anomalies tab loaded', async function(assert) {

    await render(hbs`{{host-detail/anomalies}}`);
    assert.equal(findAll('.host-anomalies').length, 1, 'Anomalies tab loaded');
    assert.equal(find('.label').textContent.trim(), 'Image Hooks', 'Image Hooks tab present');
  });
});
