import 'sa/tests/helpers/with-feature';
import resolver from './helpers/resolver';
import './helpers/flash-message';
import { start } from 'ember-cli-qunit';

import {
  setResolver
} from 'ember-qunit';

setResolver(resolver);
start();
