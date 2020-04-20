import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';

@classic
@tagName('box')
@classNames('flexi-fit', 'file-header')
export default class Header extends Component {}