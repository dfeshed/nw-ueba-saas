import classic from 'ember-classic-decorator';
import { attributeBindings, classNameBindings, layout as templateLayout } from '@ember-decorators/component';
import Component from '@ember/component';
import layout from './template';

@classic
@templateLayout(layout)
@classNameBindings('style')
@attributeBindings('testId:test-id')
export default class WidgetTitle extends Component {
  style = 'widget-title';
  testId = 'widget-title';
}
