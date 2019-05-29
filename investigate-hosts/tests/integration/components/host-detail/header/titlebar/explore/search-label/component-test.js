import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | endpoint host titlebar explore search label', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      applyPatch(Immutable.from(state));
      this.redux = this.owner.lookup('service:redux');
    };
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('Search label should render with loader', async function(assert) {
    new ReduxDataHelper(setState)
      .searchStatus('wait')
      .searchValue('0anacron')
      .build();

    await render(hbs`{{host-detail/header/titlebar/explore/search-label }}`);
    const searchFieldLoader = document.querySelectorAll('.host-explore__loader');
    assert.equal(searchFieldLoader.length, 1, 'search-label loader validated');

  });

  test('Search label should render with close button', async function(assert) {
    new ReduxDataHelper(setState)
      .searchStatus('complete')
      .searchValue('0anacron')
      .build();
    await render(hbs`{{host-detail/header/titlebar/explore/search-label }}`);
    const closingButton = document.querySelectorAll('.rsa-form-button');
    assert.equal(closingButton.length, 1, 'search-label loader clase button');

  });
});