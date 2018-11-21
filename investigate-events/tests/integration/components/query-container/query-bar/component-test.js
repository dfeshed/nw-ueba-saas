import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, fillIn, find, findAll, render, settled } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import SELECTORS from '../selectors';
import PILL_SELECTORS from '../pill-selectors';
import { createBasicPill } from '../pill-util';
import { patchReducer } from '../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | Query Bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');

    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('it displays the correct number of query bar links and starts on guided mode', async function(assert) {
    new ReduxDataHelper(setState)
      .queryStats()
      .hasRequiredValuesToQuery(true)
      .build();

    this.set('executeQuery', () => {});

    await render(hbs`
      {{query-container/query-bar executeQuery=executeQuery}}
    `);

    assert.equal(findAll(SELECTORS.queryFormatToggleLinks).length, 2, 'Expected 2 query bars');
    assert.equal(findAll(SELECTORS.guidedQueryBar).length, 1, 'Expected to see Guided Query Bar');
  });

  test('Can toggle between views', async function(assert) {
    // const done = assert.async();
    new ReduxDataHelper(setState)
      .queryStats()
      .hasRequiredValuesToQuery(true)
      .build();
    this.set('executeQuery', () => {});

    await render(hbs`
      {{query-container/query-bar executeQuery=executeQuery}}
    `);
    await click(SELECTORS.queryFormatFreeFormToggle);

    return settled().then(async() => {
      assert.equal(findAll(SELECTORS.freeFormQueryBar).length, 1, 'Expected to see Free Form Query Bar');
      assert.equal(findAll(SELECTORS.freeFormQueryBarFocusedInput).length, 1, 'Expected focus on free-form');
      assert.equal(
        find(SELECTORS.freeFormQueryBarInput).placeholder,
        'Enter multiple complex statements consisting of a Meta Key, Operator, and Value (optional)',
        'Expected a placeholder');

      await click(SELECTORS.queryFormatGuidedToggle);
      return settled().then(() => {
        assert.ok(find(SELECTORS.guidedQueryBar), 'Expected to see Guided Query Bar');
        assert.ok(find(SELECTORS.guidedQueryBarFocusedInput), 'Expected focus on guided');
      });
    });
  });

  test('Toggling from pills to free form copies pill text in', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .queryStats()
      .pillsDataEmpty(true)
      .hasRequiredValuesToQuery(true)
      .build();
    this.set('executeQuery', () => {});

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);

    // Create pill
    await createBasicPill();

    // Click over to free form
    await click(SELECTORS.queryFormatFreeFormToggle);
    assert.equal(find(SELECTORS.freeFormQueryBarInput).value, 'a = \'x\'', 'pill is converted to free form');
  });

  test('Toggling from free form with pill-like input to pills will create non-complex pills', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataEmpty(true)
      .queryStats()
      .hasRequiredValuesToQuery(true)
      .queryView('freeForm')
      .build();
    this.set('executeQuery', () => {});

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);

    // Create pill
    await click(PILL_SELECTORS.freeFormInput);
    await fillIn(PILL_SELECTORS.freeFormInput, 'a = 1');
    await blur(PILL_SELECTORS.freeFormBarContainer);

    await click(SELECTORS.queryFormatGuidedToggle);

    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 2, 'The created pill and the template are two pills present');
    assert.equal(findAll(PILL_SELECTORS.complexPill).length, 0, 'that is not a complex pill');
    assert.equal(findAll(PILL_SELECTORS.queryPill)[0].textContent.replace(/\s/g, ''), 'a=1', 'pill text is correct');
  });

  test('Toggling from free form with complex input to pills will create complex pills', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataEmpty(true)
      .queryStats()
      .hasRequiredValuesToQuery(true)
      .queryView('freeForm')
      .build();
    this.set('executeQuery', () => {});

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);

    // Create pill
    await click(PILL_SELECTORS.freeFormInput);
    await fillIn(PILL_SELECTORS.freeFormInput, '( adslkjalksdj && asdasdsad)');
    await blur(PILL_SELECTORS.freeFormBarContainer);

    await click(SELECTORS.queryFormatGuidedToggle);

    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 1, 'The template is the only query pill present');
    assert.equal(findAll(PILL_SELECTORS.complexPill).length, 1, 'there is a complex pill');
    assert.equal(findAll(PILL_SELECTORS.complexPill)[0].textContent.replace(/\s/g, ''), '(adslkjalksdj&&asdasdsad)', 'pill text is correct');
  });

  test('Initial render of query pills will not have focus', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .queryStats()
      .pillsDataEmpty(true)
      .hasRequiredValuesToQuery(true)
      .build();

    this.set('executeQuery', () => {});

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);

    assert.equal(findAll(PILL_SELECTORS.metaInput).length, 1, 'pill meta is displayed');
    assert.equal(findAll(PILL_SELECTORS.metaInputFocused).length, 0, 'But it is not focused');
  });

  test('After toggling from free form to guided, meta will have focus', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .queryStats()
      .pillsDataEmpty(true)
      .hasRequiredValuesToQuery(true)
      .queryView('freeForm')
      .build();

    this.set('executeQuery', () => {});

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);

    await click(SELECTORS.queryFormatGuidedToggle);

    assert.equal(findAll(PILL_SELECTORS.metaInput).length, 1, 'pill meta is displayed');
    assert.equal(findAll(PILL_SELECTORS.metaInputFocused).length, 1, 'And it is focused');
  });

  test('After toggling from guided to free form, free form will have focus', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .queryStats()
      .pillsDataEmpty(true)
      .hasRequiredValuesToQuery(true)
      .build();

    this.set('executeQuery', () => {});

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);

    await click(SELECTORS.queryFormatFreeFormToggle);
    await click(PILL_SELECTORS.freeFormInput);

    assert.equal(findAll(PILL_SELECTORS.freeFormInputFocus).length, 1, 'free form is focused');
  });

  // TODO: add additional disabled states and uncomment
  // test('renders copy trigger', async function(assert) {
  //   this.set('executeQuery', () => {});
  //   await render(hbs`
  //     {{query-container/query-bar executeQuery=executeQuery}}
  //   `);
  //   assert.equal(findAll('.query-bar-selection .copy-trigger').length, 1);
  // });

  test('renders the correct dom hasWarning', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsHasWarning().build();
    this.set('executeQuery', () => {});
    await render(hbs`
      {{query-container/query-bar executeQuery=executeQuery}}
    `);
    assert.equal(findAll('.query-bar-selection.console-has-warning').length, 1);
  });

  test('renders the correct dom hasError', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsHasError().build();
    this.set('executeQuery', () => {});
    await render(hbs`
      {{query-container/query-bar executeQuery=executeQuery}}
    `);
    assert.equal(findAll('.query-bar-selection.console-has-error').length, 1);
  });

  test('renders the correct dom isOpen', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().queryStats().queryStatsIsOpen().build();
    this.set('executeQuery', () => {});
    await render(hbs`
      {{query-container/query-bar executeQuery=executeQuery}}
    `);
    assert.equal(findAll('.query-bar-selection.is-console-open').length, 1);
  });

  test('Creating separate pills in Guided and toggling back and forth from FF, separate pills will be retained', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);

    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 3, 'Should be 2 pills plus template.');
    await click(SELECTORS.queryFormatFreeFormToggle);
    await click(SELECTORS.queryFormatGuidedToggle);
    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 3, 'Should be 2 pills plus template.');

  });

  test('Creating one filter in FreeForm, toggling to Guided will validate that pill if its not a complex pill', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .build();
    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);

    await click(SELECTORS.queryFormatFreeFormToggle);
    await click(PILL_SELECTORS.freeFormInput);
    await fillIn(PILL_SELECTORS.freeFormInput, 'medium = we');

    await click(SELECTORS.queryFormatGuidedToggle);


    assert.equal(findAll(PILL_SELECTORS.invalidPill).length, 1, 'Pill should get validated and expect an invalid pill');

  });

  test('Creating a pill in Guided, toggling to FF and deleting that filter should delete the pill from state', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .queryStats()
      .pillsDataEmpty(true)
      .hasRequiredValuesToQuery(true)
      .build();
    this.set('executeQuery', () => {});

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);

    // Create pill
    await createBasicPill();

    // Click over to free form
    await click(SELECTORS.queryFormatFreeFormToggle);
    assert.equal(find(SELECTORS.freeFormQueryBarInput).value, 'a = \'x\'', 'expected filter');

    // reset the filter to no value
    await fillIn(SELECTORS.freeFormQueryBarInput, '');

    await click(SELECTORS.queryFormatGuidedToggle);

    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 1, 'Should be just one template and no pills');

  });

  test('Creating a filter in FF, resetting the filter to no value and focusing out should remove any pills from state', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .queryStats()
      .pillsDataEmpty(true)
      .hasRequiredValuesToQuery(true)
      .build();
    this.set('executeQuery', () => {});
    const state = this.owner.lookup('service:redux').getState();
    const { investigate: { queryNode: { pillsData } } } = state;


    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);
    // Click over to free form
    await click(SELECTORS.queryFormatFreeFormToggle);
    await click(PILL_SELECTORS.freeFormInput);
    await fillIn(PILL_SELECTORS.freeFormInput, 'medium = 32');

    await blur(PILL_SELECTORS.freeFormInput);
    assert.equal(find(SELECTORS.freeFormQueryBarInput).value, 'medium = 32', 'expected filter');

    // reset the filter to no value
    await fillIn(SELECTORS.freeFormQueryBarInput, '');
    await blur(PILL_SELECTORS.freeFormInput);

    // no pills in state
    assert.equal(pillsData.length, 0, 'no pills in state');

  });
});
