import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../../../helpers/patch-reducer';


module('Integration | Component | mft-container/mft-help', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('mft-action-bar has rendered', async function(assert) {

    await render(hbs`{{host-detail/downloads/mft-container/mft-help}}`);
    assert.equal(findAll('.mft-help').length, 1, 'mft-help rendered');
    assert.equal(findAll('.line-one').length, 2, 'help texts rendered');
  });


});