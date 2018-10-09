import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

const entitiesConfig = [
  {
    title: 'files',
    class: 'flag-square-2',
    count: 10
  },
  {
    title: 'users',
    class: 'account-circle-1',
    count: 1
  }
];

const data = {
  files: [{
    name: 'Sketchy.exe',
    score: '95'
  },
  {
    name: 'File2.exe',
    score: '9'
  }]
};

module('Integration | Component | endpoint/risk-entities', function(hooks) {
  setupRenderingTest(hooks);

  test('check files', async function(assert) {
    this.set('entities', entitiesConfig);
    await render(hbs`{{endpoint/risk-entities entities=entities}}`);
    assert.equal(findAll('.risk-entities').length, 1, 'risk entities is rendered');
    assert.equal(findAll('.risk-entities .header').length, 2, 'file and host two headers are present');
  });

  test('arrow is down', async function(assert) {
    this.set('isCollapsed', false);
    this.set('entities', entitiesConfig);
    this.set('entitiesData', data);
    await render(hbs`{{endpoint/risk-entities entities=entities isCollapsed=isCollapsed entitiesData=entitiesData}}`);
    assert.equal(findAll('.rsa-icon-arrow-down-12-filled').length, 1, 'down arrow icon is available');
    assert.equal(findAll('.entity-data .rsa-risk-score').length, 2, '2 risk score are available');
    assert.equal(findAll('.entity-data .entity-name')[0].innerText.trim(), 'Sketchy.exe', 'Sketchy.exe is the first file name');
  });

  test('arrow is up when not collapsed', async function(assert) {
    this.set('isCollapsed', true);
    this.set('entities', entitiesConfig);
    await render(hbs`{{endpoint/risk-entities entities=entities isCollapsed=isCollapsed}}`);
    assert.equal(findAll('.rsa-icon-arrow-right-12-filled').length, 1, 'up arrow icon is available');
  });
});
