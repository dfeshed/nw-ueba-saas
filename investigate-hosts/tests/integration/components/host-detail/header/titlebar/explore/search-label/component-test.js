import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import engineResolverFor from '../../../../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import $ from 'jquery';

let setState;

moduleForComponent('host-detail/header/titlebar/explore/search-label', 'Integration | Component | endpoint host titlebar explore search label', {
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

test('Search label should render with loader', function(assert) {
  new ReduxDataHelper(setState)
  .searchStatus('wait')
  .searchValue('0anacron')
  .build();

  this.render(hbs`{{host-detail/header/titlebar/explore/search-label }}`);
  const searchFieldLoader = $('.host-explore__loader');
  assert.equal(searchFieldLoader.length, 1, 'search-label loader validated');

});

test('Search label should render with close button', function(assert) {
  new ReduxDataHelper(setState)
  .searchStatus('complete')
  .searchValue('0anacron')
  .build();
  this.render(hbs`{{host-detail/header/titlebar/explore/search-label }}`);
  const closingButton = $('.rsa-form-button');
  assert.equal(closingButton.length, 1, 'search-label loader clase button');

});