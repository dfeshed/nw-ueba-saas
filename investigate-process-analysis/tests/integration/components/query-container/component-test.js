import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, settled, fillIn, blur, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ContextualHelp from 'component-lib/services/contextual-help';
import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;

const minuteInput = '.date-time-input.minute input';
const invalidSelector = '.rsa-investigate-query-container__time-selector.time-range-invalid';

const setTimeRange = () => {
  const startTimeinSec = 1508091780;
  const endTimeinSec = 1508178179;
  new ReduxDataHelper(setState)
    .startTime(startTimeinSec)
    .endTime(endTimeinSec)
    .build();
};

module('Integration | Component | Query Container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('it renders service selector and time selector and a button ', async function(assert) {
    await render(hbs`{{query-container}}`);
    assert.equal(findAll('.rsa-investigate-query-container__time-selector').length, 1, 'Expected to render time selector');
    assert.equal(findAll('.rsa-investigate-query-container__service-selector').length, 1, 'Expected to render service selector');
  });

  test('it renders an error when start time is greater than end time', async function(assert) {
    setTimeRange();

    await render(hbs`{{query-container}}`);

    assert.equal(findAll(invalidSelector).length, 0);
    await fillIn(minuteInput, '');
    // focus out to set it
    await blur(minuteInput);
    return settled().then(() => {
      assert.equal(findAll(invalidSelector).length, 1);
    });

  });


  test('custom time range removes clears the error and set the correct time', async function(assert) {
    setTimeRange();

    await render(hbs`{{query-container}}`);

    assert.equal(findAll(invalidSelector).length, 0);
    await fillIn(minuteInput, '');
    // focus out to set it
    await blur(minuteInput);
    return settled().then(async() => {
      assert.equal(findAll(invalidSelector).length, 1);
      await click('.time-selector .rsa-form-button');
      await click('.rsa-dropdown-action-list li:nth-child(9)');
      assert.equal(findAll(invalidSelector).length, 0);
    });

  });

  test('it allows user to change the time', async function(assert) {

    setTimeRange();

    await render(hbs`{{query-container}}`);

    assert.equal(find('.rsa-date-time-range').getAttribute('title').trim(), 'Calculated duration:  23 hours 59 minutes 59 seconds');
    await fillIn(minuteInput, '1');
    // focus out to set it
    await blur(minuteInput);
    return settled().then(() => {
      assert.equal(findAll(invalidSelector).length, 0);
      assert.equal(find('.rsa-date-time-range').getAttribute('title').trim(), 'Calculated duration:  1 days 21 minutes 59 seconds');
    });

  });


  test('it renders the contextual help button', async function(assert) {
    await render(hbs`{{query-container}}`);
    this.owner.register('service:contextualHelp', ContextualHelp.extend({
      goToHelp(module, topic) {
        assert.equal(module, 'investigation');
        assert.equal(topic, 'invProcessAnalysis');
      }
    }));
    await click('.query-container .rsa-icon-help-circle-lined');
    return settled();
  });
});
