import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from '../../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import startApp from '../../../../../helpers/start-app';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import sinon from 'sinon';
import * as DataCreators from 'investigate-hosts/actions/data-creators/details';
import wait from 'ember-test-helpers/wait';

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

sinon.stub(DataCreators, 'setNewTabView');

test('Should call action when the tab is clicked', function(assert) {
  this.render(hbs`{{host-detail/header/titlebar}}`);
  return wait().then(() => {
    this.$('.rsa-nav-tab')[4].click();
    assert.equal(DataCreators.setNewTabView.calledOnce, true, 'action is called');
    assert.equal(DataCreators.setNewTabView.args[0][0], this.$(this.$('.rsa-nav-tab')[4]).text().trim().toUpperCase(), 'Rendered the tab name that is passed');
    DataCreators.setNewTabView.restore();
  });
});