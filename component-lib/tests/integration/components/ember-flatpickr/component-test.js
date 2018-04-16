import { module, test } from 'qunit';
import Component from '@ember/component';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, settled } from '@ember/test-helpers';

module('Integration | Component | ember-flatpickr', function(hooks) {
  setupRenderingTest(hooks);

  test('date picker will reflect proper locale after change occurs', async function(assert) {
    assert.expect(2);

    class FakeClazz extends Component {
      get layout() {
        return hbs`<button onclick={{action go}}>go</button>{{ember-flatpickr date=(readonly today) locale=locale onChange="go"}}`;
      }
    }

    this.owner.register('component:test-clazz', FakeClazz);

    this.set('go', () => {
      this.set('locale', 'ja');
    });

    this.set('locale', 'en');
    this.set('today', '1991-01-01T06:00:00.000Z');

    await render(hbs`{{test-clazz go=(action go) locale=locale today=today}}`);

    await click('.flatpickr-input');

    assert.equal(document.querySelector('.cur-month').textContent.trim(), 'January');

    await click('button');

    return settled().then(async () => {
      assert.equal(document.querySelector('.cur-month').textContent.trim(), '1月');
    });
  });
});
