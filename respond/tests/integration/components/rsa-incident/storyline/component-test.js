import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import DataHelper from '../../../../helpers/data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';

module('Integration | Component | Incident Storyline', function(hooks) {
  setupRenderingTest(hooks, {
    integration: true,
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders story line list', async function(assert) {
    patchReducer(this, Immutable.from({}));
    new DataHelper(this.owner.lookup('service:redux')).fetchIncidentStoryline();
    await render(hbs`{{rsa-incident/storyline}}`);

    const elements = findAll('.rsa-list');
    assert.equal(elements.length, 1, 'Expected to find list root element in DOM.');

    const rows = elements[0].querySelectorAll('.rsa-incident-storyline-item');
    assert.ok(rows.length, 'Expected to find at least one storyline item element in DOM.');
  });
});