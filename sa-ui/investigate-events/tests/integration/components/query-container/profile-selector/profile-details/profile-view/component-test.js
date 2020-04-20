import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | Profile Details - Profile View', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const profileViewSelector = '.profile-view';
  const profileNameSelector = '.profile-name';
  const metaGroupNameSelector = '.meta-group-name';
  const columnGroupNameSelector = '.column-group-name';
  const prequeryConditionsSelector = '.prequery-conditions.scroll-box.readonly';
  const prequeryConditionsItemSelector = `${prequeryConditionsSelector} ul li`;
  const prequeryPillSelector = `${prequeryConditionsItemSelector}.prequery-pill`;
  const prequeryOperatorSelector = `${prequeryConditionsItemSelector}.prequery-pill-operator`;
  const prequeryTextIconSelector = `${prequeryPillSelector} i.is-text.rsa-icon.rsa-icon-search`;
  const nameSelector = '.name';
  const valueSelector = '.value';

  const pillsData1 = [
    {
      id: '1',
      meta: 'a',
      operator: '=',
      value: '\'x\'',
      type: 'query'
    },
    {
      id: '2',
      type: 'operator-and'
    },
    {
      id: '3',
      meta: 'b',
      operator: '=',
      value: '\'y\'',
      type: 'query'
    }
  ];
  const pillsData2 = [
    ...pillsData1,
    {
      id: '100',
      type: 'operator-or'
    },
    {
      id: '101',
      meta: 'b',
      operator: '=',
      value: '\'zzz\'',
      type: 'text'
    }
  ];
  const profile1 = {
    id: '2222',
    name: 'Some Profile',
    profileGroup: {
      name: 'Profile Group 1'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      id: 'cg1',
      name: 'Column Group 1'
    },
    metaGroup: {
      id: 'mg1',
      name: 'Meta Group 1'
    },
    preQuery: 'service=80',
    preQueryPillsData: pillsData1,
    contentType: 'USER'
  };

  test('it renders profile properties correctly', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    this.set('editProfile', () => {});
    this.set('profile', profile1);
    await render(hbs`{{query-container/profile-selector/profile-details/profile-view
      profile=profile
      editProfile=editProfile
    }}`);

    assert.equal(findAll(profileViewSelector).length, 1, 'Shall render profile view');
    assert.equal(findAll(profileNameSelector).length, 1, 'Shall render profile-name div with correct class');
    assert.equal(findAll(`${profileNameSelector} ${nameSelector}`).length, 1, 'Shall render profile-name name div with correct class');
    assert.equal(findAll(`${profileNameSelector} ${valueSelector}`).length, 1, 'Shall render profile-name value div with correct class');
    assert.equal(find(`${profileNameSelector} ${nameSelector}`).innerText.trim(), translation.t('investigate.profile.profileName'), 'Shall render profile-name name div with correct text');
    assert.equal(find(`${profileNameSelector} ${valueSelector}`).innerText.trim(), profile1.name, 'Shall render profile name correctly');

    assert.equal(findAll(metaGroupNameSelector).length, 1, 'Shall render meta-group-name div with correct class');
    assert.equal(findAll(`${metaGroupNameSelector} ${nameSelector}`).length, 1, 'Shall render meta-group-name name div with correct class');
    assert.equal(findAll(`${metaGroupNameSelector} ${valueSelector}`).length, 1, 'Shall render meta-group-name value div with correct class');
    assert.equal(find(`${metaGroupNameSelector} ${nameSelector}`).innerText.trim(), 'Meta Group', 'Shall render meta-group-name name div with correct text');
    assert.equal(find(`${metaGroupNameSelector} ${valueSelector}`).innerText.trim(), profile1.metaGroup.name, 'Shall render meta group name correctly');

    assert.equal(findAll(columnGroupNameSelector).length, 1, 'Shall render column-group-name div with correct class');
    assert.equal(findAll(`${columnGroupNameSelector} ${nameSelector}`).length, 1, 'Shall render column-group-name name div with correct class');
    assert.equal(findAll(`${columnGroupNameSelector} ${valueSelector}`).length, 1, 'Shall render column-group-name value div with correct class');
    assert.equal(find(`${columnGroupNameSelector} ${nameSelector}`).innerText.trim(), 'Column Group', 'Shall render column-group-name name div with correct text');
    assert.equal(find(`${columnGroupNameSelector} ${valueSelector}`).innerText.trim(), profile1.columnGroup.name, 'Shall render column group name correctly');

    assert.equal(findAll(prequeryConditionsSelector).length, 1, 'Shall render prequery-conditions div with correct class');
    assert.equal(findAll(`${prequeryConditionsSelector} ${nameSelector}`).length, 1, 'Shall render prequery-conditions name div with correct class');
    assert.equal(findAll(`${prequeryConditionsSelector} ${valueSelector}`).length, 1, 'Shall render prequery-conditions value div with correct class');
    assert.equal(findAll(`${prequeryConditionsSelector} ul.value`).length, 1, 'Shall render prequery-conditions ul');
  });

  test('it renders correct number of pills with correct text', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    this.set('editProfile', () => {});
    this.set('profile', profile1);
    await render(hbs`
      {{query-container/profile-selector/profile-details/profile-view
        profile=profile
        editProfile=editProfile
      }}
    `);
    assert.equal(findAll(prequeryPillSelector).length, 2, 'Shall render correct number of pills');
    assert.equal(findAll(prequeryOperatorSelector).length, 1, 'Shall render correct number of operators');
    assert.equal(findAll(prequeryConditionsItemSelector).length, 3,
      'Shall render correct number of items for prequery conditions');
    assert.equal(find(prequeryPillSelector).innerText.trim(), 'a = \'x\'', 'Shall render correct text in pill');
    assert.equal(find(prequeryOperatorSelector).innerText.trim(),
      translation.t('investigate.profile.prequeryConditions.and'), 'Shall render correct text for operator');
    assert.equal(findAll(prequeryTextIconSelector).length, 0, 'Shall not render icon in non-text pills');
  });

  test('it renders icon correctly to indicate text pill in prequery conditions', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    this.set('profile', { ...profile1, preQueryPillsData: pillsData2 });
    this.set('editProfile', () => {});
    await render(hbs`
      {{query-container/profile-selector/profile-details/profile-view
        profile=profile
        editProfile=editProfile
      }}
    `);
    assert.equal(findAll(prequeryPillSelector).length, 3, 'Shall render correct number of pills');
    assert.equal(findAll(prequeryOperatorSelector).length, 2, 'Shall render correct number of operators');
    assert.equal(findAll(prequeryConditionsItemSelector).length, 5, 'Shall render correct number of items for prequery conditions');
    assert.equal(findAll(prequeryTextIconSelector).length, 1, 'Shall render icon to indicate text pill');
    assert.equal(findAll(prequeryOperatorSelector)[0].innerText.trim(),
      translation.t('investigate.profile.prequeryConditions.and'), 'Shall render correct text for operator');
    assert.equal(findAll(prequeryOperatorSelector)[1].innerText.trim(),
      translation.t('investigate.profile.prequeryConditions.or'), 'Shall render correct text for operator');
  });
});
