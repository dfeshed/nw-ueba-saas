import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';
import { click, find, render } from '@ember/test-helpers';

let setState;

module('Integration | Component | endpoint host titlebar explore search field', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('Search field should render', async function(assert) {
    this.set('isError', true);
    this.set('errMsg', 'error message');
    await render(hbs`{{host-detail/header/titlebar/explore/search-field isError=isError errMsg=errMsg }}`);
    assert.ok(find('.rsa-form-input'), 'Searchfield rendered');
    assert.ok(find('.rsa-form-input.is-error'), 'search-field Error validated');

  });

  test('Should render Search field with loader', async function(assert) {
    new ReduxDataHelper(setState).searchStatus('wait').build();
    await render(hbs`{{host-detail/header/titlebar/explore/search-field }}`);
    assert.ok(find('.host-explore__loader'), 'search-field loader validated');
  });

  test('Should render Search field with search icon', async function(assert) {
    new ReduxDataHelper(setState).searchStatus('complete').build();
    const redux = this.owner.lookup('service:redux');
    this.set('searchText', 'sys');
    await render(hbs`{{host-detail/header/titlebar/explore/search-field searchText=searchText}}`);
    assert.ok(find('.rsa-icon-search-filled'), 'search-field search icon validated');
    await click('.rsa-form-button');
    const state = redux.getState();
    assert.equal(state.endpoint.explore.showSearchResults, true, 'Defalut action validated');
  });
});