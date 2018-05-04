import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { find, findAll, render, settled } from '@ember/test-helpers';

const meta = '.pill-meta';
const metaPowerSelectTrigger = '.pill-meta .ember-power-select-trigger';
const powerSelectOption = '.ember-power-select-option';
const trim = (text) => text.replace(/\s+/g, '').trim();

const initialState = {
  language: [
    { count: 0, format: 'Text', metaName: 'a', flags: 1, displayName: 'A' },
    { count: 0, format: 'Text', metaName: 'b', flags: 2, displayName: 'B' },
    { count: 0, format: 'Text', metaName: 'c', flags: 3, displayName: 'C' },
    { count: 0, format: 'Text', metaName: 'cc', flags: 3, displayName: 'CC' }
  ]
};

let setState;

module('Integration | Component | Pill Meta', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      const fullState = { investigate: { dictionaries: state } };
      patchReducer(this, Immutable.from(fullState));
    };
  });

  test('it shows only the value if inactive', async function(assert) {
    // Set a selection just so we have a value to compare against. Otherwise
    // it'd be an empty string.
    this.set('selection', initialState.language[0]);
    await render(hbs`{{query-container/pill-meta isActive=false selection=selection}}`);
    assert.equal(trim(find(meta).textContent), 'a');
  });

  test('it shows only value if active, but no options', async function(assert) {
    // Set a selection just so we have a value to compare against. Otherwise
    // it'd be an empty string.
    this.set('selection', initialState.language[0]);
    await render(hbs`{{query-container/pill-meta isActive=true selection=selection}}`);
    assert.equal(trim(find(meta).textContent), 'a');
  });

  test('it shows a Power Select if active and has options', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{query-container/pill-meta isActive=true}}`);
    assert.equal(findAll(metaPowerSelectTrigger).length, 1);
  });

  test('it broadcasts a message when a Power Select option is choosen', async function(assert) {
    assert.expect(2);
    setState({ ...initialState });
    this.set('handleMessage', (type, data) => {
      assert.equal(type, 'PILL::META_SELECTED', 'Wrong message type');
      assert.deepEqual(data, initialState.language[1], 'Wrong message data');
    });
    await render(hbs`{{query-container/pill-meta isActive=true sendMessage=(action handleMessage)}}`);
    selectChoose(metaPowerSelectTrigger, powerSelectOption, 1);// option b
    return settled();
  });

  test('it selects meta if a trailing SPACE is entered and there is one option', async function(assert) {
    assert.expect(2);
    setState({ ...initialState });
    this.set('handleMessage', (type, data) => {
      assert.equal(type, 'PILL::META_SELECTED', 'Wrong message type');
      assert.deepEqual(data, initialState.language[1], 'Wrong message data');
    });
    await render(hbs`{{query-container/pill-meta isActive=true sendMessage=(action handleMessage)}}`);
    // We go back to old-skool jQuery for this because fillIn() performs a focus
    // event on the input every time you call it which causes the search to
    // clear out. PowerSelect test helper typeInSearch() ends up just calling
    // fillIn(). Also, fillIn() doesn't seem to properly trigger an InputEvent,
    // so the input handler doesn't get a down-selected list of meta options.
    this.$('input').val('b').trigger('input');
    this.$('input').val(' ').trigger('input');
    return settled();
  });

  test('it does not selects meta if a trailing SPACE is entered and there is more than one option', async function(assert) {
    assert.expect(0);
    setState({ ...initialState });
    this.set('handleMessage', () => {
      assert.notOk('The sendMessage handler was erroneously invoked');
    });
    await render(hbs`{{query-container/pill-meta isActive=true sendMessage=(action handleMessage)}}`);
    this.$('input').val('c').trigger('input');
    this.$('input').val(' ').trigger('input');
    return settled();
  });
});