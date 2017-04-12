import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('rsa-incident-enrichment', 'Integration | Component | Incident Enrichment', {
  integration: true,
  resolver: engineResolverFor('respond'),
  setup() {
    this.inject.service('i18n');
  }
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

test('it renders', function(assert) {
  this.setProperties({
    i18n: this.get('i18n'),
    data,
    dataKey: 'ctxhub.domain_is_whitelisted'
  });

  this.render(hbs`{{rsa-incident/enrichment dataKey=dataKey data=data threshold=0 i18n=i18n}}`);
  const $el = this.$('.rsa-enrichment');
  assert.ok($el.length, 'Expected to find root element in DOM.');
});

test('it doesnt render enrichments that dont meet the threshold', function(assert) {
  this.setProperties({
    i18n: this.get('i18n'),
    data,
    dataKey: 'whois.age_score',
    threshold
  });

  this.render(hbs`{{rsa-incident/enrichment dataKey=dataKey data=data threshold=threshold i18n=i18n}}`);
  const $el = this.$('.rsa-enrichment');
  assert.notOk($el.length, 'Expected root element to be missing from DOM.');
});