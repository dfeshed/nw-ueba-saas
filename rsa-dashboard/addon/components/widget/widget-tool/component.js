import classic from 'ember-classic-decorator';
import { classNames, tagName, layout as templateLayout } from '@ember-decorators/component';
import Component from '@ember/component';
import layout from './template';
import { guidFor } from '@ember/object/internals';

@classic
@templateLayout(layout)
@tagName('hbox')
@classNames('widget-tool flexi-fit')
export default class WidgetTool extends Component {
  showConfig = true;
  panelId = null;

  init() {
    super.init(arguments);
    this.set('panelId', guidFor(this));
  }
}
