import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

const layoutService = {
  journalPanelClass: 'journalPanelClass',
  contextPanelClass: 'contextPanelClass',
  panelAClass: 'panelAClass',
  panelBClass: 'panelBClass',
  panelCClass: 'panelCClass',
  panelDClass: 'panelDClass',
  panelEClass: 'panelEClass',
  journalPanel: 'journalPanel',
  contextPanel: 'contextPanel',
  panelA: 'panelA',
  panelB: 'panelB',
  panelC: 'panelC',
  panelD: 'panelD',
  panelE: 'panelE',
  main: 'main'
};

moduleForComponent('rsa-application-layout-manager', 'Integration | Component | rsa application layout manager', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-application-layout-manager}}`);
  assert.equal(this.$().find('hbox.rsa-application-layout-manager').length, 1);
});

test('it binds the proper classes', function(assert) {
  this.set('layoutService', layoutService);
  this.render(hbs `{{rsa-application-layout-manager layoutService=layoutService main=layoutService.main journalPanel=layoutService.journalPanel contextPanel=layoutService.contextPanel panelA=layoutService.panelA panelB=layoutService.panelB panelC=layoutService.panelC panelD=layoutService.panelD panelE=layoutService.panelE}}`);
  assert.equal(this.$().find('.rsa-application-layout-manager.journalPanelClass').length, 1);
  assert.equal(this.$().find('.rsa-application-layout-manager.contextPanelClass').length, 1);
  assert.equal(this.$().find('.rsa-application-layout-manager.panelAClass').length, 1);
  assert.equal(this.$().find('.rsa-application-layout-manager.panelBClass').length, 1);
  assert.equal(this.$().find('.rsa-application-layout-manager.panelCClass').length, 1);
  assert.equal(this.$().find('.rsa-application-layout-manager.panelDClass').length, 1);
  assert.equal(this.$().find('.rsa-application-layout-manager.panelEClass').length, 1);

  assert.equal(this.get('layoutService.main'), 'main');
  assert.equal(this.get('layoutService.journalPanel'), 'journalPanel');
  assert.equal(this.get('layoutService.contextPanel'), 'contextPanel');
  assert.equal(this.get('layoutService.panelA'), 'panelA');
  assert.equal(this.get('layoutService.panelB'), 'panelB');
  assert.equal(this.get('layoutService.panelC'), 'panelC');
  assert.equal(this.get('layoutService.panelD'), 'panelD');
  assert.equal(this.get('layoutService.panelE'), 'panelE');
});
