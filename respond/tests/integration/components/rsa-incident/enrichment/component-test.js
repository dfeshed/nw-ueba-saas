import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

let i18n;

module('Integration | Component | Incident Enrichment', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    i18n = this.owner.lookup('service:i18n');
  });

  const threshold = 50;

  const data = {
    ctxhub: {
      domain_is_whitelisted: true
    },
    whois: {
      age_score: threshold - 1
    }
  };

  test('it renders', async function(assert) {
    this.setProperties({
      i18n,
      data,
      dataKey: 'ctxhub.domain_is_whitelisted'
    });

    await render(hbs`{{rsa-incident/enrichment dataKey=dataKey data=data threshold=0 i18n=i18n}}`);
    const $el = this.$('.rsa-enrichment');
    assert.ok($el.length, 'Expected to find root element in DOM.');
  });

  test('it doesnt render enrichments that dont meet the threshold', async function(assert) {
    this.setProperties({
      i18n,
      data,
      dataKey: 'whois.age_score',
      threshold
    });

    await render(hbs`{{rsa-incident/enrichment dataKey=dataKey data=data threshold=threshold i18n=i18n}}`);
    const $el = this.$('.rsa-enrichment');
    assert.notOk($el.length, 'Expected root element to be missing from DOM.');
  });
});
