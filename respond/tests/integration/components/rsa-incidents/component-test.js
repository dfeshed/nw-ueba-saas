import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import waitForReduxStateChange from '../../../helpers/redux-async-helpers';
import sinon from 'sinon';

let dispatchSpy, redux;

moduleForComponent('rsa-incidents', 'Integration | Component | Respond Incidents', {
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

test('The rsa-incidents component renders to the DOM', function(assert) {
  this.render(hbs`{{rsa-incidents}}`);
  assert.equal(this.$('.rsa-incidents').length, 1, 'The rsa-incidents component should be found in the DOM');
});

test('The returned incidents appear as rows in the table', function(assert) {
  this.render(hbs`{{rsa-incidents}}`);
  // Wait until the alerts are added
  const getItems = waitForReduxStateChange(redux, 'respond.incidents.items');
  getItems.then(() => {
    // check to make sure we see the alerts appear in the data table
    assert.ok(this.$('.rsa-data-table-body-row').length >= 1, 'At least one row appears in the data table');
  });
});
