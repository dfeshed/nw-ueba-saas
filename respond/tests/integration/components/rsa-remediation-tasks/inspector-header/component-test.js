import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import sinon from 'sinon';

let dispatchSpy, redux;

moduleForComponent('rsa-remediation-tasks/inspector-header', 'Integration | Component | Remediation Task Inspector Header', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');

    this.inject.service('redux');
    redux = this.get('redux');

    dispatchSpy = sinon.spy(redux, 'dispatch');
  },
  afterEach() {
    dispatchSpy.restore();
  }
});

const selectors = {
  id: '.id',
  name: '.name'
};

test('The rsa-remediation-tasks/new-task component renders to the DOM', function(assert) {
  this.set('info', {
    id: 'REM-1234',
    name: 'Re-image the host'
  });
  this.on('onChange', () => {
    assert.ok(true, 'The update handler is called');
  });
  this.render(hbs`{{rsa-remediation-tasks/inspector-header info=info updateItem=(action 'onChange')}}`);
  assert.equal(this.$(selectors.id).text().trim(), 'REM-1234', 'The ID appears in the component as expected');
  assert.equal(this.$(`${selectors.name} .editable-field__value`).text().trim(), 'Re-image the host', 'The name appears in the component as expected');
});

test('The updateItem handler is called when the user changes the remediation task name', function(assert) {
  assert.expect(2);
  this.set('info', {
    id: 'REM-1234',
    name: 'Re-image the host'
  });
  this.on('onChange', () => {
    assert.ok(true, 'The update handler is called');
  });
  this.render(hbs`{{rsa-remediation-tasks/inspector-header info=info updateItem=(action 'onChange')}}`);
  this.$(`${selectors.name}  .editable-field__value`).click();
  return wait().then(() => {
    this.$(`${selectors.name} input`).val('Shutdown the host').change();
    return wait();
  })
  .then(() => {
    this.$('.confirm-changes button').click();
    return wait();
  })
  .then(() => {
    assert.equal(this.$(`${selectors.name} .editable-field__value`).text().trim(), 'Shutdown the host', 'The value of the edit field is the new value');
  });
});


