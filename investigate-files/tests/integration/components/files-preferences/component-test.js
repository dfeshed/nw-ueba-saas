import { moduleForComponent, test } from 'ember-qunit';
import wait from 'ember-test-helpers/wait';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import DataHelper from '../../../helpers/data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleForComponent('files-preferences', 'Integration | Component | files preferences', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    this.inject.service('redux');
    this.registry.injection('component', 'i18n', 'service:i18n');
    initialize(this);
  }
});

test('it renders', function(assert) {
  new DataHelper(this.get('redux')).initializeData();
  this.render(hbs`{{files-preferences}}{{preferences-panel}}`);
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return wait().then(() => {
    assert.equal(this.$('header.preference-panel-js').text().trim(), 'Files Preferences', 'Make sure header title is present');
    // assert.equal(this.$('div.rsa-preferences-field-content').length, 2, '2 preference fields are rendered');
  });
});
