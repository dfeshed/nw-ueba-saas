import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';

module('Integration | Component | properties-for/determine-value', function(hooks) {
  setupRenderingTest(hooks);

  test('should render N/A when value is null, undefined or empty string', async function(assert) {
    this.set('thing', '');
    await render(hbs`{{properties-for/determine-value value=thing}}`);
    assert.equal(this.element.textContent.trim(), 'N/A');

    this.set('thing', 'foo');
    await render(hbs`{{properties-for/determine-value value=thing}}`);
    assert.equal(this.element.textContent.trim(), 'foo');

    this.set('thing', null);
    await render(hbs`{{properties-for/determine-value value=thing}}`);
    assert.equal(this.element.textContent.trim(), 'N/A');

    this.set('thing', 'bar');
    await render(hbs`{{properties-for/determine-value value=thing}}`);
    assert.equal(this.element.textContent.trim(), 'bar');

    this.set('thing', undefined);
    await render(hbs`{{properties-for/determine-value value=thing}}`);
    assert.equal(this.element.textContent.trim(), 'N/A');

    this.set('thing', '  ');
    await render(hbs`{{properties-for/determine-value value=thing}}`);
    assert.equal(this.element.textContent.trim(), 'N/A');

    this.set('thing', []);
    await render(hbs`{{properties-for/determine-value value=thing}}`);
    assert.equal(this.element.textContent.trim(), 'N/A');

    this.set('thing', 1234);
    await render(hbs`{{properties-for/determine-value value=thing}}`);
    assert.equal(this.element.textContent.trim(), '1234');

    this.set('thing', new Date('March 1, 2001'));
    await render(hbs`{{properties-for/determine-value value=thing}}`);
    assert.ok(this.element.textContent.trim().includes('Mar 01 2001 00:00:00'));
  });
});
