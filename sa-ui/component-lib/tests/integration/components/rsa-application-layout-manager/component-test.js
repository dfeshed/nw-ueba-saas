import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
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

module('Integration | Component | rsa application layout manager', function(hooks) {
  setupRenderingTest(hooks);

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-application-layout-manager}}`);
    assert.equal(findAll('hbox.rsa-application-layout-manager').length, 1);
  });

  test('it binds the proper classes', async function(assert) {
    this.set('layoutService', layoutService);
    await render(
      hbs `{{rsa-application-layout-manager layoutService=layoutService main=layoutService.main journalPanel=layoutService.journalPanel contextPanel=layoutService.contextPanel panelA=layoutService.panelA panelB=layoutService.panelB panelC=layoutService.panelC panelD=layoutService.panelD panelE=layoutService.panelE}}`
    );
    assert.equal(findAll('.rsa-application-layout-manager.journalPanelClass').length, 1);
    assert.equal(findAll('.rsa-application-layout-manager.contextPanelClass').length, 1);
    assert.equal(findAll('.rsa-application-layout-manager.panelAClass').length, 1);
    assert.equal(findAll('.rsa-application-layout-manager.panelBClass').length, 1);
    assert.equal(findAll('.rsa-application-layout-manager.panelCClass').length, 1);
    assert.equal(findAll('.rsa-application-layout-manager.panelDClass').length, 1);
    assert.equal(findAll('.rsa-application-layout-manager.panelEClass').length, 1);

    assert.equal(this.get('layoutService.main'), 'main');
    assert.equal(this.get('layoutService.journalPanel'), 'journalPanel');
    assert.equal(this.get('layoutService.contextPanel'), 'contextPanel');
    assert.equal(this.get('layoutService.panelA'), 'panelA');
    assert.equal(this.get('layoutService.panelB'), 'panelB');
    assert.equal(this.get('layoutService.panelC'), 'panelC');
    assert.equal(this.get('layoutService.panelD'), 'panelD');
    assert.equal(this.get('layoutService.panelE'), 'panelE');
  });
});
