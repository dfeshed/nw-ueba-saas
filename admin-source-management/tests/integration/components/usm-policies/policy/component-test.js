import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { patchFlash } from '../../../../helpers/patch-flash';
import { throwSocket } from '../../../../helpers/patch-socket';

const initialState = {
  policy: {
    name: '',
    description: '',
    scheduleConfig: {
      enabledScheduledScan: false,
      scheduleOptions: {
        scanStartDate: null,
        scanStartTime: [10, 0],
        recurrenceInterval: 5,
        recurrenceIntervalUnit: 'DAYS',
        runOnDaysOfWeek: []
      },
      scanOptions: {
        cpuMaximum: 75,
        cpuMaximumOnVirtualMachine: 85
      }
    }
  },
  policyStatus: null // wait, complete, error
};

const policyData = Immutable.from({
  id: 'policy_001',
  name: 'Policy 001',
  description: 'Policy 001 of policy policy_001',
  scheduleConfig: {
    scheduleOptions: {
      scanStartTime: [10, 0]
    }
  }
});

let setState;

module('Integration | Component | usm-policies/policy', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      const fullState = { usm: { policy: state } };
      patchReducer(this, Immutable.from(fullState));
    };
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.lookup('service:dateFormat').set('selected', 'MM/dd/yyyy');
    this.owner.lookup('service:timeFormat').set('selected', 'HR12');

  });

  test('Save button is disabled when there is no policy name', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{usm-policies/policy}}`);
    assert.equal(findAll('.confirm-button.is-disabled').length, 1, 'The Save button is disabled when there is no policy name');
  });

  test('On failing to save a policy, an error flash message is shown', async function(assert) {
    assert.expect(2);
    setState({ ...initialState, policy: policyData });
    this.set('transitionToPolicies', () => {}); // avoid annoying console error
    await render(hbs`{{usm-policies/policy transitionToPolicies=(action transitionToPolicies)}}`);

    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.policy.saveFailure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
    });

    const el = findAll('.confirm-button:not(.is-disabled) button')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    await click(el);
  });

  test('On successfully saving a policy, a success flash message is shown, and the transitionToPolicies action is called', async function(assert) {
    assert.expect(3);
    setState({ ...initialState, policy: policyData });

    const done = assert.async();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.policy.saveSuccess');
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    this.set('transitionToPolicies', () => {
      assert.ok('transition to policy called');
    });

    await render(hbs`{{usm-policies/policy transitionToPolicies=(action transitionToPolicies)}}`);
    const el = findAll('.confirm-button:not(.is-disabled) button')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    await click(el);
  });

  test('A loading spinner is displayed if the policyStatus property is "wait"', async function(assert) {
    setState({ ...initialState, policyStatus: 'wait' });
    await render(hbs`{{usm-policies/policy}}`);
    assert.equal(findAll('.loading-overlay .rsa-loader').length, 1, 'A loading spinner appears');
  });
});
