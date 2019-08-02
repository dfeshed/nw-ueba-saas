import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { click, fillIn, findAll, render, triggerKeyEvent } from '@ember/test-helpers';
import sinon from 'sinon';

import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import KEY_MAP from 'investigate-events/util/keys';
import PILL_SELECTORS from '../pill-selectors';
import guidedCreators from 'investigate-events/actions/guided-creators';

const ENTER_KEY = KEY_MAP.enter.key;
const X_KEY = 'KeyX';

const addFreeFormFilterSpy = sinon.spy(guidedCreators, 'addFreeFormFilter');
const updatedFreeFormTextSpy = sinon.spy(guidedCreators, 'updatedFreeFormText');
const spys = [addFreeFormFilterSpy, updatedFreeFormTextSpy];

let setState;

module('Integration | Component | Free Form', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
    spys.forEach((s) => s.resetHistory());
    initialize(this.owner);
  });

  hooks.afterEach(function() {
    spys.forEach((s) => s.resetHistory());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });

  test('it triggers execute query action when user enters text and presses enter', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState).language().pillsDataEmpty().hasRequiredValuesToQuery(true).build();

    this.set('executeQuery', () => {
      assert.ok(true, 'Should execute query');
    });

    await render(hbs`{{query-container/free-form executeQuery=(action executeQuery)}}`);

    await click(PILL_SELECTORS.freeFormInput);
    await fillIn(PILL_SELECTORS.freeFormInput, 'medium = 1');
    await triggerKeyEvent(PILL_SELECTORS.freeFormInput, 'keydown', ENTER_KEY);

    assert.equal(addFreeFormFilterSpy.callCount, 1, 'The add pill action creator was called once');
    assert.propEqual(addFreeFormFilterSpy.args[0][0], {
      pillData: {
        meta: 'medium',
        operator: '=',
        value: '1',
        type: 'query'
      },
      position: 0,
      shouldAddFocusToNewPill: false,
      fromFreeFormMode: true
    }, 'The action creator was called with the right arguments');
  });

  test('it does not trigger an action to add a filter if the text hasnt changed', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState).pillsDataPopulated().hasRequiredValuesToQuery(true).build();

    this.set('executeQuery', () => {});

    await render(hbs`{{query-container/free-form executeQuery=(action executeQuery)}}`);
    await click(PILL_SELECTORS.freeFormInput);
    await triggerKeyEvent(PILL_SELECTORS.freeFormInput, 'keydown', ENTER_KEY);

    assert.equal(addFreeFormFilterSpy.callCount, 0, 'The add pill action creator was not called');
  });

  test('it does not trigger search action when hasRequiredValuesToQuery isn\'t set', async function(assert) {
    assert.expect(0);

    new ReduxDataHelper(setState)
      .pillsDataPopulated()
      .hasRequiredValuesToQuery(false)
      .build();

    this.set('executeQuery', () => {
      assert.ok(false, 'Should not get here');
    });

    await render(hbs`{{query-container/free-form executeQuery=(action executeQuery)}}`);

    await click(PILL_SELECTORS.freeFormInput);
    await triggerKeyEvent(PILL_SELECTORS.freeFormInput, 'keydown', ENTER_KEY);
  });

  test('it does not add any filters to state if the text is empty', async function(assert) {
    assert.expect(2);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .hasRequiredValuesToQuery(true)
      .build();

    this.set('executeQuery', () => {
      assert.ok(true, 'Should get here');
    });

    await render(hbs`{{query-container/free-form executeQuery=(action executeQuery)}}`);

    await click(PILL_SELECTORS.freeFormInput);
    await triggerKeyEvent(PILL_SELECTORS.freeFormInput, 'keydown', ENTER_KEY);

    assert.equal(addFreeFormFilterSpy.callCount, 0, 'The add pill action creator was not called');
  });

  test('it sends out an action to update the free for string', async function(assert) {
    assert.expect(2);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .hasRequiredValuesToQuery(true)
      .build();

    this.set('executeQuery', () => {});

    await render(hbs`{{query-container/free-form executeQuery=(action executeQuery)}}`);

    await click(PILL_SELECTORS.freeFormInput);
    // await triggerKeyEvent(PILL_SELECTORS.freeFormInput, 'keydown', X_KEY);

    await fillIn(PILL_SELECTORS.freeFormInput, 'm');
    await triggerKeyEvent(PILL_SELECTORS.freeFormInput, 'keyup', X_KEY);

    assert.equal(updatedFreeFormTextSpy.callCount, 1, 'The add pill action creator was called once');
    assert.deepEqual(
      updatedFreeFormTextSpy.args[0][0],
      'm',
      'The action creator was called with the right arguments'
    );
  });

  test('should not have focus when set to not have focus', async function(assert) {
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .hasRequiredValuesToQuery(true)
      .build();

    this.set('executeQuery', () => {});

    await render(hbs`
      {{query-container/free-form
        executeQuery=(action executeQuery)
        takeFocus=false
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.freeFormInputFocus).length, 0, 'Should not have focus');
  });

  test('should have focus when set to have focus', async function(assert) {
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .hasRequiredValuesToQuery(true)
      .build();

    this.set('executeQuery', () => {});

    await render(hbs`
      {{query-container/free-form
        executeQuery=(action executeQuery)
        takeFocus=true
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.freeFormInputFocus).length, 1, 'Should have focus');
  });


});