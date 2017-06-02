import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import waitForReduxStateChange from '../../../helpers/redux-async-helpers';
import sinon from 'sinon';

let dispatchSpy, redux;

moduleForComponent('rsa-alerts', 'Integration | Component | Respond Alerts', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');

    // inject and handle redux
    this.inject.service('redux');
    redux = this.get('redux');

    dispatchSpy = sinon.spy(redux, 'dispatch');
  },
  afterEach() {
    dispatchSpy.restore();
  }
});

test('The rsa-alerts component renders to the DOM', function(assert) {
  this.render(hbs`{{rsa-alerts}}`);
  assert.equal(this.$('.rsa-alerts').length, 1, 'The rsa-alerts component should be found in the DOM');
});

test('The returned alerts appear as rows in the table', function(assert) {
  this.render(hbs`{{rsa-alerts}}`);
  // Wait until the alerts are added
  const getItems = waitForReduxStateChange(redux, 'respond.alerts.items');
  getItems.then(() => {
    // check to make sure we see the alerts appear in the data table
    assert.ok(this.$('.rsa-data-table-body-row').length >= 1, 'At least one row of alerts appears in the data table');
  });
});
