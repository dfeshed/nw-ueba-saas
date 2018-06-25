import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/risk-panel/respond-link', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders incident link', async function(assert) {
    this.set('id', 'INC-1');
    this.set('path', 'incident');
    await render(hbs`{{endpoint/risk-panel/respond-link entryId=id path=path}}`);
    assert.equal(find('a.respond-link').textContent.trim(), 'INC-1', 'Link title is correct');
    assert.equal(find('a.respond-link').href.endsWith('/respond/incident/INC-1'), true, 'link url is correct');
  });

  test('it renders alert link with block', async function(assert) {
    this.set('id', '5afcffbedb7a8b75269a0040');
    this.set('path', 'alert');
    this.set('icon', 'expand-6');
    await render(hbs`{{#endpoint/risk-panel/respond-link entryId=id path=path}}
    {{rsa-icon name=icon}}
    {{/endpoint/risk-panel/respond-link}}`);
    assert.equal(findAll('a.respond-link i.rsa-icon-expand-6-filled').length, 1, 'Icon is rendered');
    assert.equal(find('a.respond-link').href.endsWith('/respond/alert/5afcffbedb7a8b75269a0040'), true, 'link url is correct');
  });
});
