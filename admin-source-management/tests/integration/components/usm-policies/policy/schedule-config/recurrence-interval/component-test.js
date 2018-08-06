import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import { clickTrigger } from '../../../../../../helpers/ember-power-select';

module('Integration | Component | usm-policies/policy/schedule-config/recurrence-interval', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  test('should render recurrence interval fields', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/recurrence-interval}}`);
    assert.equal(findAll('.recurrence-interval').length, 1, 'expected to have root element in DOM');
  });

  test('should display daily and weekly recurrence type', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/recurrence-interval}}`);
    assert.equal(findAll('.recurrence-type').length, 2, 'expected to have two radio button in dom');
  });

  test('should display Daily recurrence fields on clicking the Daily radio button', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/recurrence-interval}}`);
    assert.equal(this.$('.recurrence-interval input:eq(0)').val(), 'DAYS', 'expected to render DAYS as first field');
    assert.equal(findAll('.recurrence-run-interval').length, 1, 'expected to render dropdown for run interval');
    assert.equal(findAll('input[type=radio]:checked').length, 1, 'Expected to select default radio button');
    return wait().then(() => {
      clickTrigger();
      assert.ok(this.$('.ember-power-select-option:contains("1")').attr('aria-disabled') !== 'true');
      assert.ok(this.$('.ember-power-select-option:contains("20")').attr('aria-disabled') !== 'true');
    });
  });

  test('should display weeks recurrence field options on clicking the Weekly radio button', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/recurrence-interval}}`);
    this.$('.recurrence-interval input:eq(1)').click();
    assert.equal(this.$('input[type=radio]:eq(1):checked').length, 1, 'Expected to select Weekly radio button');
    assert.equal(findAll('.recurrence-run-interval__week-options').length, 1, 'Expected to display week options');
  });

  test('should select the week on clicking the available week options', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/recurrence-interval}}`);
    this.$('.recurrence-interval input:eq(1)').click();
    assert.equal(this.$('.recurrence-run-interval__week-options').length, 1, 'Expected to display week options');
    this.$('.week-button:eq(0)').click();
    assert.equal(this.$('.week-button:eq(0).is-primary').length, 1);
  });
});
