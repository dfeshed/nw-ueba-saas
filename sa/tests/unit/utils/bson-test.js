import bson from '../../../utils/bson';
import { module, test } from 'qunit';

module('Unit | Utility | bson');

test('it exists', function(assert) {
  assert.ok(typeof bson.toJson === 'function');
});

test('it wraps arrays in square brackets', function(assert) {
  let result = bson.toJson(' { "foo": "bar" } ', true);
  assert.ok(result.charAt(0) === '[', 'Beginning bracket was missing.');
  assert.ok(result.charAt(result.length - 1) === ']', 'Ending bracket was missing.');
});

test('it converts _id with id', function(assert) {
  let result = bson.toJson('{"_id": 1}');
  assert.ok(result.indexOf('"id"') > -1);
});

test('it converts $date: ### to ###', function(assert) {
  let result = bson.toJson('{"created": { "$date" : 123 }}');
  assert.ok(result.indexOf('"created": 123') > -1);
});

test('it converts ISODate("XXX") to Date.parse(XXX)', function(assert) {
  let now = new Date();
  let str = now.toISOString();
  let parsed = Date.parse(str);
  let result = bson.toJson(`{"created": ISODate("${str}")}`);

  assert.ok(result.indexOf(`"created": ${parsed}`) > -1);
});

test('it converts $numberLong: ### to ###', function(assert) {
  let result = bson.toJson('{"created": { "$numberLong" : 123 }}');
  assert.ok(result.indexOf('"created": 123') > -1);
});

test('it converts NumberLong("###") to ###', function(assert) {
  let result = bson.toJson('{"created": NumberLong("123")}');
  assert.ok(result.indexOf('"created": 123') > -1);
});

test('it converts { "$oid" : X } to X', function(assert) {
  let result = bson.toJson('{"childId": { "$oid" : "ABC" }}');
  assert.ok(result.indexOf('"childId": "ABC"') > -1);
});
