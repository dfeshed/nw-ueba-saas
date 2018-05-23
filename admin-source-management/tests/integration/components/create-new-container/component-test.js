import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../helpers/vnext-patch';
import { waitForSockets } from '../../../helpers/wait-for-sockets';
import wait from 'ember-test-helpers/wait';

const initialState = {
  policy: {
    name: '',
    description: ''
  },
  policyList: [],
  policyStatus: null,
  policySaveStatus: null // wait, complete, error
};

const policyData = Immutable.from({
  'id': 'policy_001',
  'name': 'Policy 001',
  'description': 'Policy 001 of policy policy_001'
});

let setState;

module('Integration | Component | create-new-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      const fullState = { policy: state };
      patchReducer(this, Immutable.from(fullState));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Save button is disabled when there is no policy name', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{create-new-container}}`);
    assert.equal(findAll('.confirm-button.is-disabled').length, 1, 'The Save button is disabled when there is no policy name');
  });

  test('Clicking the save button calls transitionToPolicies', async function(assert) {
    assert.expect(1);
    const done = waitForSockets();
    setState({ ...initialState, policy: policyData });
    this.set('transitionToPolicies', () => {
      assert.ok('transition to policy called');
    });
    await render(hbs`{{create-new-container transitionToPolicies=(action transitionToPolicies)}}`);
    const el = findAll('.confirm-button:not(.is-disabled) button')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    await click(el);
    return wait().then(() => done());
  });

  test('A loading spinner is displayed if the policySaveStatus property is "wait"', async function(assert) {
    setState({ ...initialState, policySaveStatus: 'wait' });
    await render(hbs`{{create-new-container}}`);
    assert.equal(findAll('.loading-overlay .rsa-loader').length, 1, 'A loading spinner appears');
  });
});
