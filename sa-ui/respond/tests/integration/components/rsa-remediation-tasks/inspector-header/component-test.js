import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { render, click, find, fillIn } from '@ember/test-helpers';
import sinon from 'sinon';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';

module('Integration | Component | Remediation Task Inspector Header', function(hooks) {
  let dispatchSpy;
  setupRenderingTest(hooks, {
    integration: true,
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    patchReducer(this, Immutable.from({}));
    const redux = this.owner.lookup('service:redux');
    this.set('redux', redux);
    dispatchSpy = sinon.spy(redux, 'dispatch');
    this.send = (actionName, ...args) => this.actions[actionName].apply(this, args);
  });
  hooks.afterEach(function() {
    dispatchSpy.restore();
  });

  const selectors = {
    id: '.id',
    name: '.name'
  };

  test('The rsa-remediation-tasks/new-task component renders to the DOM', async function(assert) {
    this.set('info', {
      id: 'REM-1234',
      name: 'Re-image the host'
    });
    this.set('onChange', () => assert.ok(true, 'The update handler is called'));
    await render(hbs`{{rsa-remediation-tasks/inspector-header info=info updateItem=(action onChange)}}`);
    assert.equal(find(selectors.id).textContent.trim(), 'REM-1234', 'The ID appears in the component as expected');
    assert.equal(find(`${selectors.name} .editable-field__value`).textContent.trim(), 'Re-image the host', 'The name appears in the component as expected');
  });

  test('The updateItem handler is called when the user changes the remediation task name', async function(assert) {
    assert.expect(2);
    this.set('info', {
      id: 'REM-1234',
      name: 'Re-image the host'
    });
    this.set('onChange', () => assert.ok(true, 'The update handler is called'));
    await render(hbs`{{rsa-remediation-tasks/inspector-header info=info updateItem=(action onChange)}}`);
    await click(`${selectors.name}  .editable-field__value`);
    await fillIn(`${selectors.name} input`, 'Shutdown the host');
    await click('.confirm-changes button');
    assert.equal(find(`${selectors.name} .editable-field__value`).textContent.trim(), 'Shutdown the host', 'The value of the edit field is the new value');
  });

});
