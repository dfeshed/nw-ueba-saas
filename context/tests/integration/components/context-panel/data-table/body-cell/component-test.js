import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { find, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | context-panel/data-table/body-cell', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('tab is renders', async function(assert) {

    // Set any properties with this.set('myProperty', 'value');
    // Handle any actions with this.on('myAction', function(val) { ... });

    await render(hbs `{{context-panel/data-table/body-cell}}`);

    assert.equal(find('.rsa-data-table-body-cell').textContent.trim(), '', 'Empty value should be displayed if column and item is not specified');

    const column = {
      field: 'id',
      title: 'context.lc.md5',
      width: '100'
    };
    const item = {
      'id': 'ID1'
    };
    this.set('column', column);
    this.set('item', item);
    await render(hbs`{{context-panel/data-table/body-cell item=item index=1 column=column}}`);

    assert.equal(find('.rsa-data-table-body-cell').textContent.trim(), 'ID1');
  });

  test('for incident lokkup assignee id is rendered if assignee name is null', async function(assert) {

    const column = {
      field: 'assignee.name',
      title: 'context.incident.assignee',
      width: '100'
    };

    const item = {
      'assignee': {
        'name': null,
        'id': 'admin'
      }
    };
    this.set('column', column);
    this.set('item', item);
    await render(hbs`{{context-panel/data-table/body-cell item=item index=1 column=column}}`);

    assert.equal(find('.rsa-data-table-body-cell').textContent.trim(), 'admin');
  });

  test('for live connect lookup risk rating value is rendered', async function(assert) {

    const column = {
      field: 'risk',
      title: 'context.lc.risk',
      dataType: 'riskRating',
      width: '100'
    };
    const item = {
      'risk': 'UNSAFE'
    };
    this.set('column', column);
    this.set('item', item);
    await render(hbs`{{context-panel/data-table/body-cell item=item index=1 column=column}}`);

    assert.equal(find('.rsa-data-table-body-cell').textContent.trim(), 'UNSAFE');
  });

  test('for endpoint lookup risk score field is rendered', async function(assert) {

    const column = {
      field: 'IOCScore.Score',
      title: 'context.modules.iiocScore',
      dataType: 'riskscore',
      width: '100'
    };
    const item = {
      'IOCScore': {
        'Score': 1024
      }
    };
    this.set('column', column);
    this.set('item', item);
    await render(hbs`{{context-panel/data-table/body-cell item=item index=1 column=column}}`);

    assert.equal(find('.rsa-context-panel__risk-badge__danger-risk').textContent.trim(), '1024');
  });

  test('for alert lookup link to redirect to respond is rendered', async function(assert) {

    const column = {
      field: 'alert.name',
      title: 'context.alerts.name',
      dataType: 'link',
      path: '/respond/alert/{0}',
      width: '100'
    };
    const item = {
      'alert': {
        'name': 'RERULE'
      }
    };
    this.set('column', column);
    this.set('item', item);
    await render(hbs`{{context-panel/data-table/body-cell item=item index=1 column=column}}`);

    assert.ok(find('.link a[href]').getAttribute('href').includes('respond/alert/RERULE'));
  });

  test('for alert lookup alert source field is rendered', async function(assert) {

    const column = {
      field: 'alert.source',
      title: 'context.alerts.source',
      width: '100'
    };
    const item = {
      'alert': {
        'source': 'ECAT'
      }
    };
    this.set('column', column);
    this.set('item', item);
    await render(hbs`{{context-panel/data-table/body-cell item=item index=1 column=column}}`);

    assert.equal(find('.rsa-data-table-body-cell').textContent.trim(), 'Endpoint');
  });

  test('for incident lookup incident created field is rendered', async function(assert) {

    const column = {
      field: 'created.$date',
      title: 'context.incident.created',
      dataType: 'datetime',
      width: '100'
    };
    const item = {
      'created': {
        '$date': '2016-02-09T04:17:06.156Z'
      }
    };
    this.set('column', column);
    this.set('item', item);
    await render(hbs`{{context-panel/data-table/body-cell item=item index=1 column=column}}`);

    assert.equal(find('.rsa-data-table-body-cell').textContent.trim(), '2016/02/09 04:17:06 (3 years ago)');
  });

});
