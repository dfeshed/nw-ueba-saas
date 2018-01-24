import { module, test } from 'qunit';
import { hostDetails } from '../../../state/state';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | systemInformation');

import {
  hostFileEntries,
  selectedSystemInformationData } from 'investigate-hosts/reducers/details/system-information/selectors';

test('hostFileEntries', function(assert) {
  const result = hostFileEntries(Immutable.from({ endpoint: { overview: { hostDetails } } }));
  assert.equal(result.length, 3);
  assert.equal(result[0].ip, '127.0.0.1');
});

test('selectedSystemInformationData to get security products', function(assert) {
  const result = selectedSystemInformationData(Immutable.from({
    endpoint: {
      overview: { hostDetails },
      visuals: { activeSystemInformationTab: 'SECURITY_PRODUCTS' } } }));
  assert.equal(result.data.length, 2);
});

test('selectedSystemInformationData to get host file entries', function(assert) {
  const result = selectedSystemInformationData(Immutable.from({
    endpoint: {
      overview: { hostDetails },
      visuals: { activeSystemInformationTab: 'HOST_ENTRIES' } } }));
  assert.equal(result.data.length, 3);
});

test('selectedSystemInformationData to get windows patches', function(assert) {
  const result = selectedSystemInformationData(Immutable.from({
    endpoint: {
      overview: { hostDetails },
      visuals: { activeSystemInformationTab: 'WINDOWS_PATCHES' } } }));
  assert.equal(result.data.length, 6);
});

test('selectedSystemInformationData to get mounted paths', function(assert) {
  const result = selectedSystemInformationData(Immutable.from({
    endpoint: {
      overview: { hostDetails },
      visuals: { activeSystemInformationTab: 'MOUNTED_PATH' } } }));
  assert.equal(result.data.length, 28);
});

test('selectedSystemInformationData to get bash history', function(assert) {
  const result = selectedSystemInformationData(Immutable.from({
    endpoint: {
      overview: { hostDetails },
      visuals: { activeSystemInformationTab: 'BASH_HISTORY' } } }));
  assert.equal(result.data.length, 0);
});