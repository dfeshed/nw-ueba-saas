import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import DataHelper from '../../../../helpers/data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { waitUntil, findAll, settled, render } from '@ember/test-helpers';

module('Integration | Component | Incident Events Table', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders', async function(assert) {
    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux).fetchIncidentStoryline();
    await settled();

    await render(hbs`{{rsa-incident/events-table}}`);

    const selector = '.rsa-data-table';
    await waitUntil(() => findAll(selector).length > 0);
    assert.equal(findAll(selector).length, 1, 'Expected to find data table root element in DOM.');
  });

});
