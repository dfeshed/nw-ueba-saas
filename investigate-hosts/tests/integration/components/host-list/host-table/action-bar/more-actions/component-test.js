import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | host table action bar more actions', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('Clicking more button will give Reset Risk score and Delete options', async function(assert) {
    new ReduxDataHelper(setState).scanCount(2).build();
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true);
    });
    await render(hbs`{{host-list/host-table/action-bar/more-actions showRiskScoreModal=showRiskScoreModal deleteAction=deleteAction}}`);
    assert.equal(document.querySelector('.host_more_actions button').textContent.trim(), 'More', 'action bar More button label');
    assert.equal(document.querySelectorAll('.host_more_actions .is-disabled').length, 0, 'action bar more button is enabled');
    await click('.host_more_actions button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 2, '2 list options should render.');
    assert.equal(findAll('.rsa-dropdown-action-list li')[0].textContent.trim(), 'Reset Risk Score', 'Reset Risk Score option is rendered.');
    assert.equal(findAll('.rsa-dropdown-action-list li')[1].textContent.trim(), 'Delete', 'Delete option is rendered.');
  });

  test('Clicking Delete options will call passed down action', async function(assert) {
    new ReduxDataHelper(setState).scanCount(2).build();
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true, 'passed action is called');
    });
    await render(hbs`{{host-list/host-table/action-bar/more-actions showRiskScoreModal=showRiskScoreModal deleteAction=deleteAction}}`);
    await click('.host_more_actions button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 2, '2 list options should render.');
    assert.equal(findAll('.rsa-dropdown-action-list li')[0].textContent.trim(), 'Reset Risk Score', 'Reset Risk Score option is rendered.');
    assert.equal(findAll('.rsa-dropdown-action-list li')[1].textContent.trim(), 'Delete', 'Delete option is rendered.');
    await click(findAll('.rsa-dropdown-action-list li')[1]);
  });
});


