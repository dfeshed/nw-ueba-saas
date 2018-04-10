import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

module('Unit | i18n | default locale fallback test', function(hooks) {
  setupTest(hooks);

  test('should fallback to configured default language', function(assert) {
    const i18n = this.owner.lookup('service:i18n');

    assert.equal(i18n.get('locale'), 'en-us');
    assert.equal(`${i18n.t('application.title')}`, 'NetWitness Suite');
    assert.equal(`${i18n.t('uknown.key.foo.bar.bazz')}`, 'Missing translation: uknown.key.foo.bar.bazz');

    i18n.set('locale', 'mo');
    assert.equal(i18n.get('locale'), 'mo');
    assert.equal(`${i18n.t('application.title')}`, 'NetWitness Suite');
    assert.equal(`${i18n.t('uknown.key.foo.bar.bazz')}`, 'Missing translation: uknown.key.foo.bar.bazz');
  });

});
