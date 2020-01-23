import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

module('Unit | i18n | default locale fallback test', function(hooks) {
  setupTest(hooks);

  test('should fallback to configured default language', function(assert) {
    const i18n = this.owner.lookup('service:i18n');
    assert.equal(i18n.get('primaryLocale'), 'en-us');
    assert.equal(i18n.t('appTitle'), 'NetWitness Platform');
    assert.equal(i18n.t('uknown.key.foo.bar.bazz'), 'Missing translation "uknown.key.foo.bar.bazz" for locale "en-us"');

    i18n.setLocale(['ja-jp', 'en-us']);
    assert.equal(i18n.get('primaryLocale'), 'ja-jp');
    assert.equal(i18n.t('appTitle'), 'NetWitness Platform');
    assert.equal(i18n.t('uknown.key.foo.bar.bazz'), 'Missing translation "uknown.key.foo.bar.bazz" for locale "ja-jp, en-us"');
  });

});
