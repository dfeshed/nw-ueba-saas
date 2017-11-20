import { moduleForComponent, test } from 'ember-qunit';
import wait from 'ember-test-helpers/wait';
import hbs from 'htmlbars-inline-precompile';
import DataHelper from '../../../../helpers/data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import EmberObject from 'ember-object';

const data = {
  eventType: { name: 'FILES' }
};

moduleForComponent('recon-event-actionbar/export-files', 'Integration | Component | recon event actionbar/export files', {
  integration: true,
  beforeEach() {
    this.registry.injection('component:recon-event-actionbar/export-files', 'i18n', 'service:i18n');
    this.inject.service('redux');
    this.inject.service('flash-messages');
    this.inject.service('access-control');
    initialize(this);
  }
});

test('it renders', function(assert) {
  new DataHelper(this.get('redux')).initializeData();

  this.render(hbs`{{recon-event-actionbar/export-files}}`);

  return wait().then(() => {
    assert.equal(this.$('.export-files-button').length, 1, 'Expected download files button');
  });
});

test('the button disables when accessControl.hasInvestigateContentExportAccess is false', function(assert) {
  const _data = {
    ...data,
    fileExtractStatus: 'idle'
  };

  new DataHelper(this.get('redux')).initializeData(_data);

  this.set('accessControl', EmberObject.create({}));
  this.set('accessControl.hasInvestigateContentExportAccess', false);

  this.render(hbs`{{recon-event-actionbar/export-files accessControl=accessControl}}`);

  return wait().then(() => {
    assert.equal(this.$('.export-files-button.is-disabled').length, 1, 'Expected disabled download button');
  });
});
