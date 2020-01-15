import classic from 'ember-classic-decorator';

import {
  classNames,
  attributeBindings,
  classNameBindings,
  tagName,
  layout as templateLayout
} from '@ember-decorators/component';

import Component from '@ember/component';
import layout from './template';

@classic
@templateLayout(layout)
@tagName('box')
@classNames('layout-column')
@classNameBindings('columnClass')
@attributeBindings('columnClass:test-id')
export default class LayoutColumn extends Component {
  columnClass = null;
}
