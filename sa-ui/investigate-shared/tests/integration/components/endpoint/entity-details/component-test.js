import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

const HOST_DETAIL_PROPERTY_TABS = [
  {
    label: 'investigateHosts.tabs.fileDetails',
    name: 'FILE_DETAILS',
    selected: true
  },
  {
    label: 'investigateHosts.tabs.riskDetails',
    name: 'RISK'
  }
];

module('Integration | Component | endpoint/entity-details', function(hooks) {
  setupRenderingTest(hooks);

  test('entity-details is rendered', async function(assert) {
    assert.expect(5);
    this.set('title', 'filename.exe');
    this.set('tabs', HOST_DETAIL_PROPERTY_TABS);
    this.set('closeAction', () => {
      assert.ok(true);
    });
    this.set('setTabAction', () => {
      this.set('tabs', [{
        label: 'investigateHosts.tabs.riskDetails',
        name: 'RISK',
        selected: true
      }]);
    });

    await render(hbs`{{endpoint/entity-details
          title=title
          tabs=tabs
          setTabAction=setTabAction
          closeAction=closeAction
        }}`);

    assert.equal(find('h3.entity-title').textContent.trim().includes('filename.exe'), true, 'Selected process name is displayed');
    assert.equal(findAll('.rsa-nav-tab').length, 2, '2 tabs are rendered');
    assert.equal(findAll('.rsa-nav-tab.is-active')[0].textContent.trim(), 'File Details', 'Default tab is file details');
    await click(findAll('.rsa-nav-tab')[1]);
    assert.equal(findAll('.rsa-nav-tab.is-active')[0].textContent.trim(), 'Risk Details', 'Risk details tab is selected');
    await click(find('.close-icon'));
  });
});
