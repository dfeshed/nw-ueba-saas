import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import {
  OpenParen,
  CloseParen,
  OperatorAnd
} from 'investigate-events/util/grammar-types';
import { QueryFilter } from 'investigate-events/util/filter-types';
import validationUtils from 'investigate-events/actions/pill-validation-utils';


const createPill = (meta, operator, value) => QueryFilter.create({ meta, operator, value });
const createAnd = () => OperatorAnd.create();
const createClose = () => CloseParen.create();
const createOpen = () => OpenParen.create();


module('Unit | Actions | Pill Validation Utils', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('createPillPositionMap stores pillData and their positions, handles duplicates', function(assert) {

    const pillsData = [
      createPill('foo', '=', 'bar'),
      createAnd(),
      createPill('foo', '=', 'baz'),
      createOpen(),
      createPill('bar', 'exists'),
      createClose(),
      createPill('foo', '=', 'bar') // duplicate
    ];

    const positionMap = validationUtils.createPillPositionMap(pillsData, 0);
    assert.deepEqual(positionMap.get('foo = bar'), [0, 6]);
    assert.deepEqual(positionMap.get('foo = baz'), [2]);
    assert.deepEqual(positionMap.get('bar exists'), [4]);
    assert.ok(positionMap.size, 3, 'Should only hold validatable pills');


    // Verify findPillPosition util
    let positionArray = positionMap.get('foo = bar');
    // foo = bar would have 2 pills with different positions
    assert.deepEqual(positionArray[0], 0, 'Should find one such position');
    assert.deepEqual(positionArray[1], 6, 'Should find one such position');

    positionArray = positionMap.get('foo = baz');
    // foo = baz should have one pill with its position
    assert.deepEqual(positionArray[0], 2, 'Should find one such position');

    positionArray = positionMap.get('bar exists');
    // bar exists should have one pill with its position
    assert.deepEqual(positionArray[0], 4, 'Should find one such position');
  });

  test('validatablePillsWithPositions provides an array of pill with their positions only if they are validatable', function(assert) {
    const pillsData = [
      createPill('foo', '=', 'bar'),
      createAnd(),
      createPill('foo', '=', 'baz'),
      createOpen(),
      createPill('bar', 'exists'),
      createClose(),
      createPill('foo', '=', 'bar')
    ];

    const pillPositions = validationUtils.validatablePillsWithPositions(pillsData, 0);
    assert.equal(pillPositions.length, 4, 'Should contain validatable pills');
    assert.equal(validationUtils.pillAsString(pillPositions[0].pillData), 'foo = bar', 'Should contain expected pill');
    assert.equal(pillPositions[0].position, 0, 'Should contain pill at the expected location');

    assert.equal(validationUtils.pillAsString(pillPositions[1].pillData), 'foo = baz', 'Should contain expected pill');
    assert.equal(pillPositions[1].position, 2, 'Should contain pill at the expected location');

    assert.equal(validationUtils.pillAsString(pillPositions[2].pillData), 'bar exists', 'Should contain expected pill');
    assert.equal(pillPositions[2].position, 4, 'Should contain pill at the expected location');

    assert.equal(validationUtils.pillAsString(pillPositions[3].pillData), 'foo = bar', 'Should contain expected pill');
    assert.equal(pillPositions[3].position, 6, 'Should contain pill at the expected location');

  });

});