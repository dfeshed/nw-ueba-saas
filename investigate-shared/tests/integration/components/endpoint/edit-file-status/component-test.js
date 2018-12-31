import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Service from '@ember/service';
import { computed } from '@ember/object';

module('Integration | Component | endpoint/edit-file-status', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('service:accessControl', Service.extend({
      endpointCanManageFiles: computed(function() {
        return true;
      })
    }));
  });

  test('Renders the edit file-status button', async function(assert) {
    await render(hbs`{{endpoint/edit-file-status}}`);
    assert.equal(findAll('.edit-file-status').length, 1, 'Edit Files status button has rendered.');
    assert.equal(findAll('.file-status-button')[0].textContent.trim(), 'Change File Status', 'change-file-status button text verified.');
  });

  test('it should disable the edit-file-status button', async function(assert) {
    await render(hbs`{{endpoint/edit-file-status isDisabled=true}}`);
    assert.equal(findAll('.file-status-button')[0].classList.contains('is-disabled'), true, 'Edit files status Button is disabled');
  });

  test('it should enable the edit-file-status button', async function(assert) {
    await render(hbs`{{endpoint/edit-file-status isDisabled=false}}`);
    assert.equal(findAll('.file-status-button')[0].classList.contains('is-disabled'), false, 'Edit files status Button is enabled');
  });

  test('it should call the external function when single selection', async function(assert) {
    assert.expect(2);
    this.set('itemList', new Array(1));
    this.set('getSavedFileStatus', function(selections) {
      assert.equal(selections.length, 1);
    });
    this.set('retrieveRemediationStatus', function() {
      assert.ok(true);
    });
    await render(hbs`{{endpoint/edit-file-status isDisabled=false itemList=itemList retrieveRemediationStatus=(action retrieveRemediationStatus) getSavedFileStatus=(action getSavedFileStatus)}}`);
    await click('.file-status-button button');
  });
});
