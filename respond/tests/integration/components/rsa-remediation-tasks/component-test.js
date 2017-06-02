import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import waitForReduxStateChange from '../../../helpers/redux-async-helpers';

let redux;

moduleForComponent('rsa-remediation-tasks', 'Integration | Component | Respond Remediation Tasks', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');

    // inject and handle redux
    this.inject.service('redux');
    redux = this.get('redux');
  }
});

test('The rsa-remediation-tasks component renders to the DOM', function(assert) {
  this.render(hbs`{{rsa-remediation-tasks}}`);
  assert.equal(this.$('.rsa-remediation-tasks').length, 1, 'The rsa-remediation-tasks component should be found in the DOM');
});

test('The returned remediation-tasks appear as rows in the table', function(assert) {
  this.render(hbs`{{rsa-remediation-tasks}}`);
  // Wait until the remediation-tasks are added
  const getItems = waitForReduxStateChange(redux, 'respond.remediationTasks.items');
  getItems.then(() => {
    // check to make sure we see the remediation-tasks appear in the data table
    assert.ok(this.$('.rsa-data-table-body-row').length >= 1, 'At least one row of remediation-tasks appears in the data table');
  });
});
