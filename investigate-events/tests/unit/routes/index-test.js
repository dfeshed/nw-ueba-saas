import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { computed } from '@ember/object';
import { settled, waitUntil } from '@ember/test-helpers';
import sinon from 'sinon';

import { patchReducer } from '../../helpers/vnext-patch';
import InvestigateEventsIndex from 'investigate-events/routes/index';
import initializationCreators from 'investigate-events/actions/initialization-creators';
import dataCreators from 'investigate-events/actions/data-creators';

let redux;

const isBaseInvestigateIntializationComplete = () => {
  const { investigate } = redux.getState();
  const { dictionaries, queryNode, services, columnGroup } = investigate;
  const columnGroupsCameback = (columnGroup.columnGroups || []).length > 5;
  const preferencesCameBack = queryNode.queryTimeFormat === 'DB';
  const aliasesCameBack = Object.keys(dictionaries.aliases || {}).length === 9;
  const languagesCameBack = (dictionaries.language || []).length === 95;
  const servicesCameBack = (services.serviceData || []).length === 4;

  // Useful, leaving for later
  // console.log(columnGroupsCameback, preferencesCameBack, aliasesCameBack, languagesCameBack, servicesCameBack);// esline-disable-line

  return columnGroupsCameback && preferencesCameBack && aliasesCameBack && languagesCameBack && servicesCameBack;
};

module('Unit | Route | investigate-events.index', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'investigate-events'
    }));
    redux = this.owner.lookup('service:redux');
    const PatchedRoute = InvestigateEventsIndex.extend({
      redux: computed(function() {
        return redux;
      }),
      transitionToPillHash() {
        // leaving this empty, can't seem to get context right
        // and get failures when called, so stubbing out/removing
      }
    });
    return PatchedRoute.create();
  };

  test('should call initializeInvestigate with hardReset if nothing defined', async function(assert) {
    assert.expect(3);
    const initializationCreatorsMock = sinon.stub(initializationCreators, 'initializeInvestigate');

    // setup reducer and route
    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);
    const params = { pdhash: undefined };

    // execute model hook
    await route.model(params);
    await settled();
    assert.equal(initializationCreatorsMock.callCount, 1, 'should call intializationCreators');
    assert.deepEqual(initializationCreatorsMock.args[0][0], params, 'params should be passed into initialize');
    assert.equal(initializationCreatorsMock.args[0][2], true, 'hardReset should be true when no populated values present');

    initializationCreatorsMock.restore();
  });

  test('base route visit with no params should initialize investigate data, no query execution', async function(assert) {
    assert.expect(1);
    const fetchInvestigateDataSpy = sinon.stub(dataCreators, 'fetchInvestigateData');

    // setup reducer and route
    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    // execute model hook
    await route.model({});
    await settled();
    return waitUntil(() => {
      if (fetchInvestigateDataSpy.callCount > 0) {
        assert.ok(false, 'should not call into fetchInvestigateData');
        fetchInvestigateDataSpy.restore();
        return true;
      }
      if (isBaseInvestigateIntializationComplete()) {
        assert.ok(true, 'all the expected data was populated');
        fetchInvestigateDataSpy.restore();
        return true;
      }
      return false;
    }, { timeout: 15000 });
  });

  test('base route visit with just serviceId should initialize investigate data, no query execution', async function(assert) {
    assert.expect(2);
    const fetchInvestigateDataSpy = sinon.stub(dataCreators, 'fetchInvestigateData');

    // setup reducer and route
    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);
    const params = { sid: '555d9a6fe4b0d37c827d402e' };

    // execute model hook
    await route.model(params);
    await settled();
    return waitUntil(() => {
      if (fetchInvestigateDataSpy.callCount > 0) {
        assert.ok(false, 'should not call into fetchInvestigateData');
        fetchInvestigateDataSpy.restore();
        return true;
      }
      if (isBaseInvestigateIntializationComplete()) {
        assert.ok(true, 'all the expected initial data was populated');
        const { investigate } = redux.getState();
        // when coming in with service, the times are set automatically and
        // the query is initialized not not executed
        if (investigate.queryNode.currentQueryHash.startsWith('555d9a6fe4b0d37c827d402e')) {
          assert.ok(true, 'currentQueryHash populated');
          fetchInvestigateDataSpy.restore();
          return true;
        }
      }
      return false;
    }, { timeout: 10000 });
  });

  test('base route visit required query input and pill data hash will retrieve inputs and execute query', async function(assert) {
    assert.expect(1);
    const fetchInvestigateDataSpy = sinon.stub(dataCreators, 'fetchInvestigateData');

    // setup reducer and route
    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);
    const params = {
      sid: '555d9a6fe4b0d37c827d402e',
      et: '10000',
      st: '1',
      pdhash: ['d9ee', '934i', 's09e']
    };

    // execute model hook
    await route.model(params);
    await settled();
    return waitUntil(() => {
      const baseComplete = isBaseInvestigateIntializationComplete();
      const calledFetchData = fetchInvestigateDataSpy.callCount === 1;
      const { queryNode } = redux.getState().investigate;
      const hashes = queryNode.pillDataHashes || [];
      const pillDataHashesPresent = hashes.length === 3 && hashes[0] === 'd9ee';
      const pillsDataPopulated = queryNode.pillsData.length === 3;
      if (baseComplete && calledFetchData && pillDataHashesPresent && pillsDataPopulated) {
        assert.ok(true, 'all the expected initial data was populated and query executed');
        fetchInvestigateDataSpy.restore();
        return true;
      }
      return false;
    }, { timeout: 10000 });
  });

  test('base route visit required query input and pill data hash will retrieve one input with multiple filters and execute query', async function(assert) {
    assert.expect(1);
    const fetchInvestigateDataSpy = sinon.stub(dataCreators, 'fetchInvestigateData');

    // setup reducer and route
    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);
    const params = {
      sid: '555d9a6fe4b0d37c827d402e',
      et: '10000',
      st: '1',
      pdhash: ['e880']
    };

    // execute model hook
    await route.model(params);
    await settled();
    return waitUntil(() => {
      const baseComplete = isBaseInvestigateIntializationComplete();
      const calledFetchData = fetchInvestigateDataSpy.callCount === 1;
      const { queryNode } = redux.getState().investigate;
      const hashes = queryNode.pillDataHashes || [];
      const pillDataHashesPresent = hashes.length === 1 && hashes[0] === 'e880';
      const pillsDataPopulated = queryNode.pillsData.length === 2;
      if (baseComplete && calledFetchData && pillDataHashesPresent && pillsDataPopulated) {
        assert.ok(true, 'all the expected initial data was populated and query executed');
        fetchInvestigateDataSpy.restore();
        return true;
      }
      return false;
    }, { timeout: 10000 });
  });

  test('base route visit required query input and pill data hash will retrieve mixed length inputs and execute query', async function(assert) {
    assert.expect(1);
    const fetchInvestigateDataSpy = sinon.stub(dataCreators, 'fetchInvestigateData');

    // setup reducer and route
    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);
    const params = {
      sid: '555d9a6fe4b0d37c827d402e',
      et: '10000',
      st: '1',
      pdhash: ['d9ee', 'e880', '934i', 's09e']
    };

    // execute model hook
    await route.model(params);
    await settled();
    return waitUntil(() => {
      const baseComplete = isBaseInvestigateIntializationComplete();
      const calledFetchData = fetchInvestigateDataSpy.callCount === 1;
      const { queryNode } = redux.getState().investigate;
      const hashes = queryNode.pillDataHashes || [];
      const pillDataHashesPresent = hashes.length === 4 && hashes[0] === 'd9ee';
      const pillsDataPopulated = queryNode.pillsData.length === 5;
      if (baseComplete && calledFetchData && pillDataHashesPresent && pillsDataPopulated) {
        assert.ok(true, 'all the expected initial data was populated and query executed');
        fetchInvestigateDataSpy.restore();
        return true;
      }
      return false;
    }, { timeout: 10000 });
  });

  test('base route visit with required query input and pill data in URL will execute query and retrieve hash for pill data', async function(assert) {
    assert.expect(1);
    const fetchInvestigateDataSpy = sinon.stub(dataCreators, 'fetchInvestigateData');

    // setup reducer and route
    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);
    const params = {
      sid: '555d9a6fe4b0d37c827d402e',
      et: '10000',
      st: '1',
      mf: "action = 'foo'"
    };

    await route.model(params);
    await settled();
    return waitUntil(() => {
      const baseComplete = isBaseInvestigateIntializationComplete();
      const calledFetchData = fetchInvestigateDataSpy.callCount === 1;
      const { queryNode } = redux.getState().investigate;
      const hashes = queryNode.pillDataHashes || [];
      // The mocks for persisting params returns a 4-character string for each filter
      const pillDataHashesPresent = hashes.length === 1 && hashes[0] === 's09e';
      const pillsDataPopulated = queryNode.pillsData.length === 1;
      if (baseComplete && calledFetchData && pillDataHashesPresent && pillsDataPopulated) {
        assert.ok(true, 'all the expected initial data was populated and query executed');
        fetchInvestigateDataSpy.restore();
        return true;
      }
      return false;
    }, { timeout: 10000 });
  });

  test('base route visit with required query input, pill data, and pdhash in URL will execute query and retrieve hash for pill data', async function(assert) {
    assert.expect(1);
    const fetchInvestigateDataSpy = sinon.stub(dataCreators, 'fetchInvestigateData');

    // setup reducer and route
    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);
    const params = {
      sid: '555d9a6fe4b0d37c827d402e',
      et: '10000',
      st: '1',
      mf: "action = 'foo'",
      pdhash: 'd9ee'
    };

    await route.model(params);
    await settled();
    return waitUntil(() => {
      const baseComplete = isBaseInvestigateIntializationComplete();
      const calledFetchData = fetchInvestigateDataSpy.callCount === 1;
      const { queryNode } = redux.getState().investigate;
      const hashes = queryNode.pillDataHashes || [];
      const pillDataHashesPresent = hashes.length === 2 && hashes.includes('d9ee');
      const arePillsInOrder = (hashes.indexOf('d9ee') === 0);
      const pillsDataPopulated = queryNode.pillsData.length === 2;
      if (baseComplete && calledFetchData && pillDataHashesPresent && pillsDataPopulated && arePillsInOrder) {
        assert.ok(true, 'all the expected initial data was populated and query executed');
        fetchInvestigateDataSpy.restore();
        return true;
      }
      return false;
    }, { timeout: 10000 });
  });
});
