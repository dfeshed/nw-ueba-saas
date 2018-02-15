import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import Immutable from 'seamless-immutable';
import engineResolverFor from '../../../../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../../../../helpers/patch-reducer';
import $ from 'jquery';

let setState;

moduleForComponent('host-detail/header/titlebar/explore/search-field', 'Integration | Component | endpoint host titlebar explore search field', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    setState = (state) => {
      applyPatch(Immutable.from(state));
      this.inject.service('redux');
    };
    this.registry.injection('component', 'i18n', 'service:i18n');
  },
  afterEach() {
    revertPatch();
  }
});

test('Search field should render', function(assert) {

  this.set('isError', true);
  this.set('errMsg', 'error message');

  this.render(hbs`{{host-detail/header/titlebar/explore/search-field isError=isError errMsg=errMsg }}`);
  const searchField = $('.rsa-form-input');
  assert.equal(searchField.length, 1, 'Searchfield rendered');
  assert.equal($(searchField).hasClass('is-error'), true, 'search-field Error validated');

});


test('Should render Search field with loader', function(assert) {
  new ReduxDataHelper(setState).searchStatus('wait').build();

  this.render(hbs`{{host-detail/header/titlebar/explore/search-field }}`);
  const searchFieldLoader = $('.host-explore__loader');
  assert.equal(searchFieldLoader.length, 1, 'search-field loader validated');

});


test('Should render Search field with search icon', function(assert) {
  new ReduxDataHelper(setState).searchStatus('complete').build();

  this.render(hbs`{{host-detail/header/titlebar/explore/search-field}}`);
  const searchFieldIcon = $('.rsa-icon-search-filled');
  assert.equal(searchFieldIcon.length, 1, 'search-field search icon validated');
});