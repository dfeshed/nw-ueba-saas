import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from '../../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import startApp from '../../../../../helpers/start-app';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';

const application = startApp();
initialize(application);

let setState;

moduleForComponent('host-detail/header/titlebar', 'Integration | Component | TitleBar', {
  integration: true,
  resolver: engineResolver('investigate-hosts'),
  beforeEach() {
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
    this.registry.injection('component', 'i18n', 'service:i18n');
  },
  afterEach() {
    revertPatch();
  }
});

test('Should render the hostname properly', function(assert) {
  new ReduxDataHelper(setState).hostName('XYZ').build();
  this.render(hbs`{{host-detail/header/titlebar}}`);
  assert.equal(this.$('.host-name').text(), 'XYZ', 'Rendered the hostname properly');
});


test('Should call action when the tab is clicked', function(assert) {
  assert.expect(1);
  new ReduxDataHelper(setState).hostName('XYZ').build();
  this.render(hbs`{{host-detail/header/titlebar}}`);
  this.$('.rsa-nav-tab')[3].click();
  return waitFor(() => {
    return this.get('redux').getState().endpoint.visuals.activeHostDetailTab === 'FILES';
  }).then(() => {
    assert.equal(this.$('.rsa-nav-tab.is-active').text().trim().toUpperCase(), 'FILES', 'Rendered the tab name that is passed');
  });
});
